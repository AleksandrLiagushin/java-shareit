package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ItemRequest {
    private long id;
    private String name;
    private String description;
    private long owner;
}
