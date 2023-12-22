package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
public class BookingDtoIn {
    private long id;

    @Positive
    private long itemId;

    @Future
    private LocalDateTime start;

    @Future
    private LocalDateTime end;
}
