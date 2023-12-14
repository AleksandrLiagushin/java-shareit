package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BookingForOwnerDto {
    private final long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final long bookerId;
}
