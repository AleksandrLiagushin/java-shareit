package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingForOwnerDto;
import ru.practicum.shareit.comment.CommentDto;

import java.util.List;

@Data
@Builder
public class ItemForOwnerDto {
    private final long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final long requestId;
    private BookingForOwnerDto lastBooking;
    private BookingForOwnerDto nextBooking;
    private List<CommentDto> comments;
}
