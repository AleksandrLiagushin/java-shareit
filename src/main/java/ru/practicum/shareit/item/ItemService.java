package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemStorage;
    private final ItemMapper itemMapper;
    private final UserRepository userStorage;
    private final BookingRepository bookingStorage;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentStorage;
    private final CommentMapper commentMapper;
    private final RequestRepository requestRepository;

    @Transactional(rollbackFor = Exception.class)
    public ItemDto create(ItemDto dto, long userId) {
        if (!userStorage.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        Item item = itemMapper.toEntity(dto);
        item.setOwner(userStorage.findById(userId).orElseThrow());
        requestRepository.findById(dto.getRequestId()).ifPresent(item::setRequest);

        return itemMapper.toDto(itemStorage.save(item));

    }

    @Transactional
    public ItemDto update(long userId, long itemId, Item item) {
        if (itemStorage.findById(itemId).orElseThrow().getOwner().getId() != userId) {
            throw new UserNotFoundException("This item is owned by other user");
        }

        Item savedItem = itemStorage.findById(itemId).orElseThrow();

        if (item.getAvailable() != null) {
            savedItem.setAvailable(item.getAvailable());
        }
        if (item.getName() != null) {
            savedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            savedItem.setDescription(item.getDescription());
        }

        savedItem.setOwner(userStorage.findById(userId).orElseThrow());
        savedItem.setId(itemId);

        return itemMapper.toDto(itemStorage.save(savedItem));
    }

    public ItemForOwnerDto getById(long itemId, long userId) {
        Item item = itemStorage.findById(itemId).orElseThrow();
        ItemForOwnerDto dto = itemMapper.toOwnerDto(item);
        List<Booking> bookings = bookingStorage.findByItemIdAndStatus(itemId, BookingStatus.APPROVED);

        Booking lastBooking = bookings.stream()
                .filter(x -> x.getEnd().isBefore(LocalDateTime.now()) ||
                        ((x.getStart().isBefore(LocalDateTime.now())) && (x.getEnd().isAfter(LocalDateTime.now()))))
                .max((Comparator.comparing(Booking::getEnd))).orElse(null);

        Booking nextBooking = bookings.stream()
                .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                .min((Comparator.comparing(Booking::getStart))).orElse(null);

        dto.setLastBooking(item.getOwner().getId() == userId ? bookingMapper.toOwnerDto(lastBooking) : null);
        dto.setNextBooking(item.getOwner().getId() == userId ? bookingMapper.toOwnerDto(nextBooking) : null);
        dto.setComments(commentStorage.findByItemId(itemId).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList()));
        return dto;
    }

    public List<ItemForOwnerDto> getItemsByUserId(long userId) {
        return itemStorage.findByOwnerId(userId).stream()
                .map(x -> getById(x.getId(), userId))
                .collect(Collectors.toList());
    }

    public List<ItemDto> findItems(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return Collections.emptyList();
        }

        List<Item> items = itemStorage.search(text.toLowerCase());

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        return items.stream()
                .filter(Item::getAvailable)
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    public Comment addComment(long userId, long itemId, CommentDto commentDto) {

        if (commentDto.getText().isBlank()) {
            throw new ItemNotExistException("Empty text field is not allowed");
        }

        List<Booking> userBookings = bookingStorage.findByBookerIdAndItemId(userId, itemId);

        if (userBookings.isEmpty()) {
            throw new ItemNotFoundException("Comments is not allowed. Cause: you haven't booked this item before");
        }

        List<Booking> finishedBookings = userBookings.stream()
                .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());

        if (finishedBookings.isEmpty()) {
            throw new ItemNotExistException("Comments is allowed only after returning item to owner");
        }

        Comment comment = new Comment();
        comment.setAuthor(userStorage.getReferenceById(userId));
        comment.setText(commentDto.getText());
        comment.setItem(itemStorage.getReferenceById(itemId));
        comment.setCreated(LocalDateTime.now());

        return commentStorage.save(comment);
    }
}
