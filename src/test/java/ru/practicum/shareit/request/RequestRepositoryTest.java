package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setOffsets() {
        User user = new User();
        user.setId(1L);
        user.setName("Vasya");
        user.setEmail("vas@mail.ru");
        userRepository.save(user);

        Item item = new Item();
        item.setId(1L);
        item.setName("thing");
        item.setDescription("someThing");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);
    }

    @AfterEach
    void clearData() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        requestRepository.deleteAll();
    }

    @Test
    void findByUserId_shouldReturnRequestByUserId() {
        User user = new User();
        user.setId(1L);
        user.setName("Vasya");
        user.setEmail("vas@mail.ru");

        Request request = new Request();
        request.setId(1L);
        request.setDescription("description");
        request.setCreated(LocalDateTime.now());
        request.setUser(user);

        requestRepository.save(request);
        List<Request> saved = requestRepository.findByUserId(request.getId());

        assertNotNull(saved);
        assertEquals(saved.size(), 1);
        assertEquals(saved.get(0).getId(), request.getId());
        assertEquals(saved.get(0).getDescription(), request.getDescription());
        assertEquals(saved.get(0).getUser().getName(), user.getName());
        assertEquals(saved.get(0).getUser().getName(), user.getName());
    }
}