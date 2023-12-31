package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BookingDtoIn {
    private long id;

    @Positive
    private long itemId;

    @Future
    private LocalDateTime start;

    @Future
    private LocalDateTime end;
}
