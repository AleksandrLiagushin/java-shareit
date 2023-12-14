package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingForOwnerDto;

@Component
public class BookingMapper {
    public Booking toEntity(BookingDtoIn dto) {
        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        return booking;
    }

    public BookingDtoOut toDto(Booking entity) {
        if (entity == null) {
            return null;
        }

        BookingDtoOut dto = new BookingDtoOut();
        dto.setId(entity.getId());
        dto.setItem(entity.getItem());
        dto.setBooker(entity.getBooker());
        dto.setStart(entity.getStart());
        dto.setEnd(entity.getEnd());
        dto.setStatus(entity.getStatus());

        return dto;
    }

    public BookingForOwnerDto toOwnerDto(Booking entity) {
        if (entity == null) {
            return null;
        }

        return BookingForOwnerDto.builder()
                .id(entity.getId())
                .bookerId(entity.getBooker().getId())
                .start(entity.getStart())
                .end(entity.getEnd())
                .build();
    }
}
