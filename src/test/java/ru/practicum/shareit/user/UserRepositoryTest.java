package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByIdTest_shouldReturnSavedUserWithId1() {
        User user = new User();
        user.setId(1L);
        user.setName("userName");
        user.setEmail("email@mail.ru");

        userRepository.save(user);
        User saved = userRepository.findById(user.getId()).orElseThrow();

        assertNotNull(saved);
        assertEquals(saved.getId(), user.getId());
        assertEquals(saved.getName(), user.getName());
        assertEquals(saved.getEmail(), user.getEmail());
    }

}