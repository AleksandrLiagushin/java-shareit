package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

class UserMapperTest {
    private final UserMapper userMapper = new UserMapper();

    @Test
    void toEntity_shouldReturnEntityFromDto() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Vasya")
                .email("vas@mail.ru")
                .build();
        User user = new User();
        user.setId(1L);
        user.setName("Vasya");
        user.setEmail("vas@mail.ru");

        User expected = userMapper.toEntity(userDto);

        Assertions.assertEquals(user.getName(), expected.getName());
        Assertions.assertEquals(user.getEmail(), expected.getEmail());
    }

    @Test
    void toDto_shouldReturnDtoFromEntity() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Vasya")
                .email("vas@mail.ru")
                .build();
        User user = new User();
        user.setId(1L);
        user.setName("Vasya");
        user.setEmail("vas@mail.ru");

        UserDto expected = userMapper.toDto(user);

        Assertions.assertEquals(user.getName(), expected.getName());
        Assertions.assertEquals(user.getEmail(), expected.getEmail());
    }

}