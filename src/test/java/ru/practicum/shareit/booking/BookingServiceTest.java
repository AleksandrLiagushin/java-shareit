package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

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
        Booking baseBooking = new Booking();
        baseBooking.setId(0L);
        baseBooking.setItem(item);
        baseBooking.setStart(LocalDateTime.of(2024, Month.APRIL, 8, 12, 30));
        baseBooking.setEnd(LocalDateTime.of(2024, Month.APRIL, 12, 12, 30));
        baseBooking.setBooker(user2);
        baseBooking.setStatus(BookingStatus.WAITING);

        Booking baseBooking1 = new Booking();
        baseBooking1.setId(1L);
        baseBooking1.setItem(item);
        baseBooking1.setStart(LocalDateTime.of(2024, Month.APRIL, 8, 12, 30));
        baseBooking1.setEnd(LocalDateTime.of(2024, Month.APRIL, 12, 12, 30));
        baseBooking1.setBooker(user2);
        baseBooking1.setStatus(BookingStatus.WAITING);

        BookingDtoIn dtoIn = new BookingDtoIn();
        dtoIn.setItemId(1L);
        dtoIn.setStart(LocalDateTime.of(2024, Month.APRIL, 8, 12, 30));
        dtoIn.setEnd(LocalDateTime.of(2024, Month.APRIL, 12, 12, 30));

        when(userRepository.existsById(2L)).thenReturn(true);
        when(itemRepository.existsById(1L)).thenReturn(true);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        when(bookingMapper.toEntity(dtoIn)).thenReturn(baseBooking);
        when(bookingRepository.save(baseBooking)).thenReturn(baseBooking1);

        Booking result = bookingService.create(dtoIn, 2L);
        Assertions.assertEquals(baseBooking1.getId(), result.getId());
    }

    @Test
    void checkRequest() {
    }

    @Test
    void getBooking() {
    }

    @Test
    void getBookingsByStatus() {
    }

    @Test
    void getUserBookings() {
    }

    @Test
    void checkState() {
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
}