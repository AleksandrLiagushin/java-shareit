package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingForOwnerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookingMapperTest {
    private final BookingMapper bookingMapper = new BookingMapper();

    @Test
    void toEntity() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2023, Month.JULY, 27, 12, 0));
        booking.setEnd(LocalDateTime.of(2023, Month.JULY, 27, 12, 0));
        BookingDtoIn dto = new BookingDtoIn();
        dto.setId(1L);
        dto.setStart(LocalDateTime.of(2023, Month.JULY, 27, 12, 0));
        dto.setEnd(LocalDateTime.of(2023, Month.JULY, 27, 12, 0));

        Booking actual = bookingMapper.toEntity(dto);

        assertEquals(booking, actual);
    }

    @Test
    void toDto() {
        User user = createUser(1L, "Vasya", "vas@email.com");
        Item item = createItem(1L, "Item", "Description", user, true);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2023, Month.JULY, 27, 12, 0));
        booking.setEnd(LocalDateTime.of(2023, Month.JULY, 27, 12, 0));
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        BookingDtoOut dto = new BookingDtoOut();
        dto.setId(1L);
        dto.setItem(item);
        dto.setBooker(user);
        dto.setStart(LocalDateTime.of(2023, Month.JULY, 27, 12, 0));
        dto.setEnd(LocalDateTime.of(2023, Month.JULY, 27, 12, 0));
        dto.setStatus(BookingStatus.APPROVED);

        BookingDtoOut actual = bookingMapper.toDto(booking);

        assertEquals(dto, actual);

        BookingDtoOut actual2 = bookingMapper.toDto(null);

        assertNull(actual2);
    }

    @Test
    void toOwnerDto() {
        User user = createUser(1L, "Vasya", "vas@email.com");
        Item item = createItem(1L, "Item", "Description", user, true);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2023, Month.JULY, 27, 12, 0));
        booking.setEnd(LocalDateTime.of(2023, Month.JULY, 27, 12, 0));
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        BookingForOwnerDto dto = BookingForOwnerDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, Month.JULY, 27, 12, 0))
                .end(LocalDateTime.of(2023, Month.JULY, 27, 12, 0))
                .bookerId(user.getId())
                .build();

        BookingForOwnerDto actual = bookingMapper.toOwnerDto(booking);

        assertEquals(dto, actual);

        BookingForOwnerDto actual2 = bookingMapper.toOwnerDto(null);

        assertNull(actual2);
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
}