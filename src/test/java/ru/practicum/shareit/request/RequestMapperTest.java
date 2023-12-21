package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

class RequestMapperTest {
    private final RequestMapper requestMapper = new RequestMapper();

    @Test
    void toDto_shouldReturnDtoFromEntity() {
        User owner = createUser(1L, "Vasya", "vas@email.com");
        Request request = new Request();
        request.setId(1L);
        request.setUser(owner);
        request.setDescription("text");
        request.setCreated(LocalDateTime.of(2023, Month.JULY, 27, 10, 0));
        Item item = createItem(1L, "Item", "Description", owner, true);
        item.setRequest(request);
        RequestDto dto = new RequestDto();
        dto.setId(1L);
        dto.setDescription("text");
        dto.setRequester(1L);
        dto.setCreated(LocalDateTime.of(2023, Month.JULY, 27, 10, 0));
        dto.setItems(List.of(item).stream().map(requestMapper::toItemsRequestDto).collect(Collectors.toList()));

        RequestDto actual = requestMapper.toDto(request, List.of(item));

        Assertions.assertEquals(dto.getRequester(), actual.getRequester());
        Assertions.assertEquals(dto.getItems(), actual.getItems());
    }

    @Test
    void toEntity() {
        RequestDto dto = new RequestDto();
        dto.setId(1L);
        dto.setDescription("text");
        dto.setRequester(1L);
        dto.setCreated(LocalDateTime.of(2023, Month.JULY, 27, 10, 0));
        Request request = new Request();
        request.setId(1L);
        request.setDescription("text");
        request.setCreated(LocalDateTime.of(2023, Month.JULY, 27, 10, 0));

        Request actual = requestMapper.toEntity(dto);

        Assertions.assertEquals(actual.getDescription(), request.getDescription());
    }

    private Item createItem(long id, String name, String text, User user, boolean available) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(text);
        item.setAvailable(available);
        item.setOwner(user);

        return item;
    }

    private User createUser(long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }
}