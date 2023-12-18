package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

class RequestMapperTest {

    @Test
    void toDto() {
    }

    @Test
    void toEntity() {
    }

    private Request createRequest(long id, String text, User user) {
        Request request = new Request();
        request.setId(id);
        request.setDescription(text);
        request.setUser(user);
        return request;
    }

    private RequestDto createRequestDto(long id, String text, long userId) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setDescription(text);
        requestDto.setRequester(userId);
        return requestDto;
    }

    private Item createItem(long id, String name, String text, boolean available, User user) {
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