package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserCreationException;
import ru.practicum.shareit.exception.UserNotExistException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    public UserDto create(User user) {
        if (userStorage.existByEmail(user.getEmail())) {
            throw new UserCreationException("User with such email already exists");
        }

        return userMapper.toDto(userStorage.create(user));
    }

    public UserDto update(long id, User user) {
        if (!userStorage.existsById(id)) {
            throw new UserNotExistException("No user with such was found");
        }
        if (userStorage.existByEmail(user.getEmail()) && !userStorage.getById(id).getEmail().equals(user.getEmail())) {
            throw new UserCreationException("User with such email already exists");
        }

        User saved = userStorage.getById(id);

        if (user.getName() != null) {
            saved.setName(user.getName());
        }
        if (user.getEmail() != null) {
            saved.setEmail(user.getEmail());
        }

        return userMapper.toDto(userStorage.update(saved));
    }

    public void delete(Long userId) {
        userStorage.delete(userId);
    }

    public User getById(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new UserNotExistException("No user with such id was found");
        }
        return userStorage.getById(userId);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }
}
