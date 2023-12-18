package ru.practicum.shareit.item;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private BookingMapper bookingMapper;


    @Test
    void create_shouldCreateNewItem() {
        User user = createUser(1L, "Vasya", "vas@email.com");
        Item item = createItem(1L, "Item", "Description", user, true);
        ItemDto itemDto = createItemDto(1L, "item", "description", true);
        Request request = createRequest(1L, "item", user);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemMapper.toEntity(itemDto)).thenReturn(item);
        when(requestRepository.findById(itemDto.getRequestId())).thenReturn(Optional.ofNullable(request));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.create(itemDto, user.getId());
        assertEquals(result.getId(), itemDto.getId());
    }

    @Test
    void create_shouldThrowUserNotFoundException() {
        User user = createUser(1L, "Vasya", "vas@email.com");
        ItemDto itemDto = createItemDto(1L, "item", "description", true);

        Mockito.when(userRepository.existsById(user.getId())).thenReturn(false);

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> itemService.create(itemDto, 1L));

        assertNotNull(exception.getMessage());
    }

    @Test
    void update_shouldUpdateItem() {
        User user = createUser(1L, "Vasya", "vas@email.com");
        Item item = createItem(1L, "Item", "Description", user, true);
        ItemDto itemDto = createItemDto(1L, "Item", "description", true);

        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(itemService.update(1L, 1L, item)).thenReturn(itemDto);

        ItemDto result = itemService.update(1L, 1L, item);

        assertEquals(itemDto.getName(), result.getName());
    }

    @Test
    void update_shouldThrowUserNotFoundException() {
        User user2 = createUser(2L, "Vasya1", "vas1@email.com");
        Item item = createItem(1L, "Item", "Description", user2, true);

        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> itemService.update(1L, 1L, item));

        assertNotNull(exception.getMessage());
    }

    @Test
    void getById() {
        User user = createUser(1L, "Vasya", "vas@email.com");
        Item item = createItem(1L, "Item", "Description", user, true);
        List<Booking> bookings = new ArrayList<>();
        when(bookingRepository.findByItemIdAndStatus(1L, BookingStatus.APPROVED)).thenReturn(bookings);

        List<Comment> comments = new ArrayList<>();
        when(commentRepository.findByItemId(1L)).thenReturn(comments);

        List<CommentDto> commentsDto = new ArrayList<>();

        ItemForOwnerDto itemForOwnerDto = ItemForOwnerDto.builder()
                .id(1L)
                .name("item")
                .description("description item")
                .available(true)
                .requestId(0)
                .lastBooking(null)
                .nextBooking(null)
                .comments(commentsDto)
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.toOwnerDto(item)).thenReturn(itemForOwnerDto);
        when(bookingMapper.toOwnerDto(null)).thenReturn(null);

        ItemForOwnerDto result = itemService.getById(1L, 1L);

        Assertions.assertEquals(itemForOwnerDto, result);
    }

    @Test
    void getItemsByUserId() {
        User user = createUser(1L, "Vasya", "vas@email.com");
        Item item = createItem(1L, "Item", "Description", user, true);
        List<Item> items = List.of(item);
        ItemForOwnerDto itemForOwnerDto = ItemForOwnerDto.builder()
                .id(1L)
                .name("item")
                .description("description item")
                .available(true)
                .requestId(0)
                .lastBooking(null)
                .nextBooking(null)
                .comments(null)
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.toOwnerDto(item)).thenReturn(itemForOwnerDto);
        when(bookingMapper.toOwnerDto(null)).thenReturn(null);
        Mockito.when(itemRepository.findByOwnerId(1L)).thenReturn(items);
        List<ItemForOwnerDto> outputItems = items.stream()
                .map(x -> itemService.getById(x.getId(), user.getId()))
                .collect(Collectors.toList());

        List<ItemForOwnerDto> result = itemService.getItemsByUserId(1L);
        Assertions.assertEquals(outputItems, result);
    }

    @Test
    void findItems_shouldReturnItemDtoList() {
        User user = createUser(1L, "Vasya", "vas@email.com");
        Item item = createItem(1L, "Item", "Description", user, true);
        ItemDto itemDto = createItemDto(1L, "Item", "description", true);
        String text = "text";
        List<Item> items = List.of(item);
        List<ItemDto> itemDtos = List.of(itemDto);
        Mockito.when(itemRepository.search(text)).thenReturn(items);
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        List<ItemDto> result = itemService.findItems(text);
        assertEquals(itemDtos, result);
    }

    @Test
    void findItems_shouldReturnEmptyList() {
        String text = "";

        List<ItemDto> result = itemService.findItems(text);
        assertTrue(result.isEmpty());
    }

    @Test
    void findItems_shouldReturnEmptyListIfNotMatchText() {
        String text = "text";

        Mockito.when(itemRepository.search(text)).thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.findItems(text);
        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_shouldAddNewComment() {
        User owner = createUser(1L, "Vasya", "vas@email.com");
        User user = createUser(2L, "Vanya", "van@email.com");
        Item item = createItem(1L, "Item", "Description", owner, true);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2023, Month.JULY, 27, 10, 0));
        booking.setEnd(LocalDateTime.of(2023, Month.JULY, 28, 10, 0));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);

        Comment comment = createComment(1L, "comment", user, item);
        CommentDto commentDto = CommentDto.builder().text("comment").build();

        Mockito.when(bookingRepository.findByBookerIdAndItemId(2L, 1L)).thenReturn(List.of(booking));
        Mockito.when(userRepository.getReferenceById(2L)).thenReturn(user);
        Mockito.when(itemRepository.getReferenceById(1L)).thenReturn(item);

        Mockito.when(commentRepository.save(comment)).thenReturn(comment);

        Comment result = itemService.addComment(2L, 1L, commentDto);
        assertEquals(comment, result);
    }

    @Test
    void addComment_shouldThrowItemNotExistException() {
        CommentDto commentDto = CommentDto.builder().text("").build();

        ItemNotExistException exception = assertThrows(
                ItemNotExistException.class,
                () -> itemService.addComment(1L, 1L, commentDto));

        assertNotNull(exception.getMessage());
    }

    @Test
    void addComment_shouldThrowItemNotFoundException() {
        CommentDto commentDto = CommentDto.builder().text("text").build();

        when(bookingRepository.findByBookerIdAndItemId(anyLong(), anyLong())).thenReturn(Collections.emptyList());

        ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> itemService.addComment(1L, 1L, commentDto));

        assertNotNull(exception.getMessage());
    }

    @Test
    void addComment_shouldThrowItemNotExistExceptionWhenNoFinishedBookings() {
        User user = createUser(1L, "Vasya", "vas@email.com");
        Item item = createItem(1L, "Item", "Description", user, true);
        CommentDto commentDto = CommentDto.builder().text("text").build();
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2023, Month.JULY, 27, 10, 0));
        booking.setEnd(LocalDateTime.now().plusMonths(1));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findByBookerIdAndItemId(anyLong(), anyLong())).thenReturn(List.of(booking));

        ItemNotExistException exception = assertThrows(
                ItemNotExistException.class,
                () -> itemService.addComment(1L, 1L, commentDto));

        assertNotNull(exception.getMessage());
    }

    private Comment createComment(long id, String text, User user, Item item) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setAuthor(user);
        comment.setItem(item);

        return comment;
    }

    private Item createItem(long id, String name, String description, User user, boolean isAvailable) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setOwner(user);
        item.setAvailable(isAvailable);

        return item;
    }

    private ItemDto createItemDto(long id, String name, String description, boolean isAvailable) {
        return ItemDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(isAvailable)
                .build();
    }

    private User createUser(long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    private Request createRequest(long id, String text, User user) {
        Request request = new Request();
        request.setId(id);
        request.setDescription(text);
        request.setUser(user);
        return request;
    }
}