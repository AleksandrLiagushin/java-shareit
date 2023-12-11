package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BookingForOwnerDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long bookerId;
}
