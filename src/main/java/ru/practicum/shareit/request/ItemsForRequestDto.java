package ru.practicum.shareit.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItemsForRequestDto {
    private long id;
    private String name;
    private String description;
    private long requestId;
    private boolean available;
}
