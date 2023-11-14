package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Item {
    private long id;
    private String name;
    private String description;
    private long owner;
    private Boolean available;
    private long requestId;

}
