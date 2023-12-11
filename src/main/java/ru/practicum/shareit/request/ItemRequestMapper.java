package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Component
public class ItemRequestMapper {

    public ItemRequest toEntity(ItemDto dto) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .build();
    }

    public ItemRequestDto toDto(ItemRequest entity) {
        return ItemRequestDto.builder()
                .description(entity.getDescription())
                .build();
    }
}
