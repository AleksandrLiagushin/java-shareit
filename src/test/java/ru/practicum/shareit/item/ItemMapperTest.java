package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.Request;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {
    private final ItemMapper itemMapper = new ItemMapper();

    @Test
    void toDto() {
        Request request = new Request();
        request.setId(1L);
        request.setDescription("text");
        request.setCreated(LocalDateTime.of(2023, Month.JULY, 27, 10, 0));
        ItemDto dto = ItemDto.builder()
                .id(1)
                .name("name")
                .description("text")
                .available(true)
                .requestId(1)
                .build();
        Item item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("text");
        item.setAvailable(true);
        item.setRequest(request);

        ItemDto actual = itemMapper.toDto(item);

        Assertions.assertEquals(actual, dto);
    }

    @Test
    void toOwnerDto() {
        ItemForOwnerDto dto = ItemForOwnerDto.builder()
                .id(1)
                .name("name")
                .description("text")
                .available(true)
                .build();

        Item item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("text");
        item.setAvailable(true);

        ItemForOwnerDto actual = itemMapper.toOwnerDto(item);

        Assertions.assertEquals(actual, dto);
    }

    @Test
    void toEntity() {
        ItemDto dto = ItemDto.builder()
                .id(1)
                .name("name")
                .description("text")
                .available(true)
                .requestId(1)
                .build();
        Item item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("text");
        item.setAvailable(true);

        Item actual = itemMapper.toEntity(dto);

        Assertions.assertEquals(actual, item);
    }
}