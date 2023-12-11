package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingForOwnerDto;
import ru.practicum.shareit.comment.CommentDto;

import java.util.List;

@Getter
@Setter
@Builder
public class ItemForOwnerDto {
    private long id;

    private String name;

    private String description;

    private Boolean available;

    private long requestId;

    private BookingForOwnerDto lastBooking;

    private BookingForOwnerDto nextBooking;

    private List<CommentDto> comments;
}
