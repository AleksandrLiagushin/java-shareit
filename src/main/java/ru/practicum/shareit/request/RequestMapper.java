package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestMapper {
    public RequestDto toDto(Request entity, List<Item> items) {
        RequestDto dto = new RequestDto();
        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setRequester(entity.getUser().getId());
        dto.setCreated(entity.getCreated());
        dto.setItems(items.stream().map(this::toItemsRequestDto).collect(Collectors.toList()));
        return dto;
    }

    private ItemsForRequestDto toItemsRequestDto(Item item) {
        ItemsForRequestDto dto = new ItemsForRequestDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setRequestId(item.getRequest().getId());
        dto.setAvailable(item.getAvailable());

        return dto;
    }

    public Request toEntity(RequestDto dto) {
        Request entity = new Request();
        entity.setDescription(dto.getDescription());
        return entity;
    }
}
