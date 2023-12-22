package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserCreationException;
import ru.practicum.shareit.exception.UserNotExistException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userStorage;
    private final UserMapper userMapper;

    @Transactional(rollbackFor = Exception.class)
    public UserDto create(User user) {
        if (user.getId() != 0) {
            throw new UserCreationException("Id must be 0");
        }

        try {
            return userMapper.toDto(userStorage.save(user));
        } catch (Exception e) {
            throw new UserCreationException("User with such email already exists");
        }
    }

    @Transactional
    public UserDto update(long id, User user) {
        if (!userStorage.existsById(id)) {
            throw new UserNotExistException("No user with such was found");
        }

        User saved = userStorage.findById(id).orElseThrow();

        if (user.getName() != null) {
            saved.setName(user.getName());
        }
        if (user.getEmail() != null) {
            saved.setEmail(user.getEmail());
        }

        return userMapper.toDto(userStorage.save(saved));
    }

    @Transactional
    public void deleteById(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new UserNotExistException("Can't delete user with id = " + userId + " because user doesn't exist");
        }
        userStorage.deleteById(userId);
    }

    public User getById(Long userId) {
        return userStorage.findById(userId).orElseThrow();
    }

    public List<UserDto> getAll() {
        return userStorage.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
