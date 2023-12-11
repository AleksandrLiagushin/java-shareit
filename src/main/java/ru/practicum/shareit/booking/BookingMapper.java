package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingForOwnerDto;

@Component
public class BookingMapper {
    public Booking toEntity(BookingDtoIn dto) {
        return Booking.builder()
                .id(dto.getId())
                .start(dto.getStart())
                .end(dto.getEnd())
                .build();
    }

    public BookingDtoOut toDto(Booking entity) {
        if (entity == null) {
            return null;
        }

        return BookingDtoOut.builder()
                .id(entity.getId())
                .item(entity.getItem())
                .booker(entity.getBooker())
                .start(entity.getStart())
                .end(entity.getEnd())
                .status(entity.getStatus())
                .build();
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
