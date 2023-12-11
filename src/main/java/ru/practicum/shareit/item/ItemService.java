package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepo;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepo;
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepo;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepo itemStorage;
    private final ItemMapper itemMapper;
    private final UserRepo userStorage;
    private final BookingRepo bookingStorage;
    private final BookingMapper bookingMapper;
    private final CommentRepo commentStorage;
    private final CommentMapper commentMapper;

    @Transactional(rollbackFor = Exception.class)
    public ItemDto create(Item item, long userId) {
        if (!userStorage.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        item.setOwner(userStorage.findById(userId).orElseThrow());

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
            throw new ItemNotExistException("This field can't be empty, write the text");
        }

        List<Booking> bookingsItemByUser = bookingStorage.findByBookerIdAndItemId(userId, itemId);

        if (bookingsItemByUser.isEmpty()) {
            throw new ItemNotFoundException("You can't write the comment, because you didn't booking this item");
        }

        List<Booking> bookingsEndsBeforeNow = bookingsItemByUser.stream()
                .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());

        if (bookingsEndsBeforeNow.isEmpty()) {
            throw new ItemNotExistException("You can't comment, because you didn't use this item");
        }

        Comment comment = Comment.builder()
                .author(userStorage.getReferenceById(userId))
                .text(commentDto.getText())
                .item(itemStorage.getReferenceById(itemId))
                .created(LocalDateTime.now())
                .build();

        return commentStorage.save(comment);
    }
}
