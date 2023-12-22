package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.exception.AvailabilityException;
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.TimeValidationException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Test
    void create() {
        User user = createUser(1L, "Vasya", "vas@email.com");
        User user2 = createUser(2L, "Vanya", "van@email.com");
        Item item = createItem(1L, "Item", "Description", user, true);
        Booking booking = new Booking();
        booking.setId(0L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.of(2024, Month.APRIL, 8, 12, 30));
        booking.setEnd(LocalDateTime.of(2024, Month.APRIL, 12, 12, 30));
        booking.setBooker(user2);
        booking.setStatus(BookingStatus.WAITING);

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setItem(item);
        booking1.setStart(LocalDateTime.of(2024, Month.APRIL, 8, 12, 30));
        booking1.setEnd(LocalDateTime.of(2024, Month.APRIL, 12, 12, 30));
        booking1.setBooker(user2);
        booking1.setStatus(BookingStatus.WAITING);

        BookingDtoIn dtoIn = new BookingDtoIn();
        dtoIn.setItemId(1L);
        dtoIn.setStart(LocalDateTime.of(2024, Month.APRIL, 8, 12, 30));
        dtoIn.setEnd(LocalDateTime.of(2024, Month.APRIL, 12, 12, 30));

        when(userRepository.existsById(2L)).thenReturn(true);
        when(itemRepository.existsById(1L)).thenReturn(true);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        when(bookingMapper.toEntity(dtoIn)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking1);

        Booking result = bookingService.create(dtoIn, 2L);
        Assertions.assertEquals(booking1.getId(), result.getId());
    }

    @Test
    public void create_shouldThrowUserNotFoundException() {

        BookingDtoIn booking = new BookingDtoIn();
        booking.setId(0L);
        booking.setStart(LocalDateTime.of(2024, Month.APRIL, 8, 12, 30));
        booking.setEnd(LocalDateTime.of(2024, Month.APRIL, 12, 12, 30));
        booking.setItemId(1L);

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.create(booking, 1L));
        assertNotNull(exception.getMessage());
    }

    @Test
    public void create_shouldThrowTimeValidationExceptionWhenStartNull() {

        BookingDtoIn booking = new BookingDtoIn();
        booking.setId(0L);
        booking.setStart(null);
        booking.setEnd(LocalDateTime.of(2024, Month.APRIL, 12, 12, 30));
        booking.setItemId(1L);

        when(userRepository.existsById(anyLong())).thenReturn(true);

        TimeValidationException exception = assertThrows(
                TimeValidationException.class,
                () -> bookingService.create(booking, 1L));
        assertNotNull(exception.getMessage());
    }

    @Test
    public void create_shouldThrowTimeValidationExceptionWhenStartAfterEnd() {

        BookingDtoIn booking = new BookingDtoIn();
        booking.setId(0L);
        booking.setStart(LocalDateTime.of(2024, Month.JULY, 12, 12, 30));
        booking.setEnd(LocalDateTime.of(2024, Month.APRIL, 12, 12, 30));
        booking.setItemId(1L);

        when(userRepository.existsById(anyLong())).thenReturn(true);

        TimeValidationException exception = assertThrows(
                TimeValidationException.class,
                () -> bookingService.create(booking, 1L));
        assertNotNull(exception.getMessage());
    }

    @Test
    public void create_shouldThrowAvailabilityException() {
        User user = createUser(1L, "Vasya", "vas@email.com");
        User user2 = createUser(2L, "Vanya", "van@email.com");
        Item item = createItem(1L, "Item", "Description", user, false);
        BookingDtoIn booking = new BookingDtoIn();
        booking.setId(0L);
        booking.setStart(LocalDateTime.of(2024, Month.JULY, 12, 12, 30));
        booking.setEnd(LocalDateTime.of(2024, Month.JULY, 14, 12, 30));
        booking.setItemId(1L);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        AvailabilityException exception = assertThrows(
                AvailabilityException.class,
                () -> bookingService.create(booking, 1L));
        assertNotNull(exception.getMessage());
    }

    @Test
    public void create_shouldThrowItemNotFoundExceptionWhenUserEqualOwner() {
        User user = createUser(1L, "Vasya", "vas@email.com");
        User user2 = createUser(2L, "Vanya", "van@email.com");
        Item item = createItem(1L, "Item", "Description", user, true);
        BookingDtoIn booking = new BookingDtoIn();
        booking.setId(0L);
        booking.setStart(LocalDateTime.of(2024, Month.JULY, 12, 12, 30));
        booking.setEnd(LocalDateTime.of(2024, Month.JULY, 14, 12, 30));
        booking.setItemId(1L);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> bookingService.create(booking, 1L));
        assertNotNull(exception.getMessage());
    }

    @Test
    public void create_shouldThrowTimeValidationExceptionWhenStartEqualEnd() {

        BookingDtoIn booking = new BookingDtoIn();
        booking.setId(0L);
        booking.setStart(LocalDateTime.of(2024, Month.JULY, 12, 12, 30));
        booking.setEnd(LocalDateTime.of(2024, Month.JULY, 12, 12, 30));
        booking.setItemId(1L);

        when(userRepository.existsById(anyLong())).thenReturn(true);

        TimeValidationException exception = assertThrows(
                TimeValidationException.class,
                () -> bookingService.create(booking, 1L));
        assertNotNull(exception.getMessage());
    }

    @Test
    public void create_shouldThrowItemNotFoundExceptionWhenStartEqualEnd() {

        BookingDtoIn booking = new BookingDtoIn();
        booking.setId(0L);
        booking.setStart(LocalDateTime.of(2024, Month.JULY, 12, 12, 30));
        booking.setEnd(LocalDateTime.of(2024, Month.JULY, 14, 12, 30));
        booking.setItemId(1L);

        when(userRepository.existsById(anyLong())).thenReturn(true);

        ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> bookingService.create(booking, 1L));
        assertNotNull(exception.getMessage());
    }

    @Test
    void checkRequest() {
        User user = createUser(1L, "Vasya", "vas@email.com");
        User user2 = createUser(2L, "Vanya", "van@email.com");
        Item item = createItem(1L, "Item", "Description", user, true);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.of(2024, Month.APRIL, 8, 12, 30));
        booking.setEnd(LocalDateTime.of(2024, Month.APRIL, 12, 12, 30));
        booking.setBooker(user2);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.getReferenceById(2L)).thenReturn(user2);
        when(itemRepository.getReferenceById(1L)).thenReturn(item);
        when(userRepository.existsById(1L)).thenReturn(true);

        Booking result = bookingService.checkRequest(1L, 1L, true);

        Assertions.assertEquals(booking, result);
    }

    @Test
    public void checkRequest_shouldThrowItemNotFoundException() {
        ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> bookingService.checkRequest(1L, 1L, false));
        assertNotNull(exception.getMessage());
    }

    @Test
    public void checkRequest_shouldThrowUserNotFoundException() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.checkRequest(1L, 1L, false));
        assertNotNull(exception.getMessage());
    }

    @Test
    public void checkRequest_shouldThrowUserNotFoundExceptionWhenOwnerEqualsBooker() {
        User user = createUser(1L, "Vasya", "vas@email.com");
        User user2 = createUser(2L, "Vanya", "van@email.com");
        Item item = createItem(1L, "Item", "Description", user, true);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.of(2024, Month.APRIL, 8, 12, 30));
        booking.setEnd(LocalDateTime.of(2024, Month.APRIL, 12, 12, 30));
        booking.setBooker(user2);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.getReferenceById(anyLong())).thenReturn(user);
        when(itemRepository.getReferenceById(anyLong())).thenReturn(item);

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> bookingService.checkRequest(2L, 1L, false));
        assertNotNull(exception.getMessage());
    }

    @Test
    public void checkRequest_shouldThrowItemNotExistExceptionWhenOwnerEqualsBooker() {
        User user = createUser(1L, "Vasya", "vas@email.com");
        User user2 = createUser(2L, "Vanya", "van@email.com");
        Item item = createItem(1L, "Item", "Description", user, true);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.of(2024, Month.APRIL, 8, 12, 30));
        booking.setEnd(LocalDateTime.of(2024, Month.APRIL, 12, 12, 30));
        booking.setBooker(user2);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.getReferenceById(anyLong())).thenReturn(user);
        when(itemRepository.getReferenceById(anyLong())).thenReturn(item);

        ItemNotExistException exception = assertThrows(
                ItemNotExistException.class,
                () -> bookingService.checkRequest(1L, 1L, false));
        assertNotNull(exception.getMessage());
    }

    @Test
    void getBooking() {
        User user = createUser(1L, "Vasya", "vas@email.com");
        User user2 = createUser(2L, "Vanya", "van@email.com");
        Item item = createItem(1L, "Item", "Description", user, true);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.of(2024, Month.APRIL, 8, 12, 30));
        booking.setEnd(LocalDateTime.of(2024, Month.APRIL, 12, 12, 30));
        booking.setBooker(user2);
        booking.setStatus(BookingStatus.WAITING);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBooking(1L, 1L);
        Assertions.assertEquals(booking, result);
    }

    @Test
    void getBookingsByStatus() {
        List<Booking> bookingsByUserId = new ArrayList<>();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerId(1L, PageRequest.of(0, 1,
                Sort.by("start").descending()))).thenReturn(bookingsByUserId);

        List<Booking> allBookings = bookingService.checkState(bookingsByUserId, "ALL");

        List<Booking> result = bookingService.getBookingsByStatus(1L, "ALL", PageRequest.of(0, 1,
                Sort.by("start").descending()));

        Assertions.assertEquals(allBookings, result);
    }

    @Test
    void getUserBookings() {
        User user = createUser(1L, "Vasya", "vas@email.com");
        Item item = createItem(1L, "Item", "Description", user, true);
        Item item2 = createItem(2L, "Item2", "Description", user, true);
        List<Item> itemByOwnerId = List.of(item, item2);

        when(itemRepository.findByOwnerId(1L)).thenReturn(itemByOwnerId);
        List<Booking> saveBooking = new ArrayList<>();
        List<Long> allItemsByUser = List.of(1L, 2L);

        when(bookingRepository.findByItemIdIn(allItemsByUser, PageRequest.of(0, 1,
                Sort.by("start").descending()))).thenReturn(saveBooking);

        List<Booking> allBooking = bookingService.checkState(saveBooking, "ALL");
        List<Booking> result = bookingService.getUserBookings(1L, "ALL", PageRequest.of(0, 1,
                Sort.by("start").descending()));

        Assertions.assertEquals(allBooking, result);
    }

    @Test
    void checkState() {
        User user = createUser(1L, "Vasya", "vas@email.com");
        Item item = createItem(1L, "Item", "Description", user, true);
        Booking bookingCURRENT = createBooking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2), item, user, BookingStatus.APPROVED);
        Booking bookingPAST = createBooking(2L, LocalDateTime.now().minusDays(7), LocalDateTime.now().minusDays(4), item, user, BookingStatus.APPROVED);
        Booking bookingFUTURE = createBooking(3L, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(5), item, user, BookingStatus.APPROVED);
        Booking bookingWAITING = createBooking(4L, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusDays(4), item, user, BookingStatus.WAITING); //current
        Booking bookingREJECTED = createBooking(5L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1), item, user, BookingStatus.REJECTED); //past
        List<Booking> allBooking = List.of(bookingCURRENT, bookingFUTURE, bookingREJECTED, bookingPAST, bookingWAITING);

        List<Booking> resultCURRENT = bookingService.checkState(allBooking, "CURRENT");
        Assertions.assertEquals(resultCURRENT.size(), 2);

        List<Booking> resultPAST = bookingService.checkState(allBooking, "PAST");
        Assertions.assertEquals(resultPAST.size(), 2);

        List<Booking> resultFUTURE = bookingService.checkState(allBooking, "FUTURE");
        Assertions.assertEquals(resultFUTURE.get(0).getId(), bookingFUTURE.getId());

        List<Booking> resultWAITING = bookingService.checkState(allBooking, "WAITING");
        Assertions.assertEquals(resultWAITING.get(0).getId(), bookingWAITING.getId());

        List<Booking> resultREJECTED = bookingService.checkState(allBooking, "REJECTED");
        Assertions.assertEquals(resultREJECTED.get(0).getId(), bookingREJECTED.getId());
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

    private User createUser(long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    private Booking createBooking(long id, LocalDateTime start, LocalDateTime end, Item item, User user, BookingStatus status) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(status);

        return booking;
    }
}