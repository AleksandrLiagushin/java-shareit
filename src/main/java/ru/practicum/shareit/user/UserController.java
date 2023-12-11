package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Requested user creation. UserDto = {}", userDto);
        return userService.create(userMapper.toEntity(userDto));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Valid @PathVariable long userId, @RequestBody UserDto userDto) {
        log.info("Requested user update. UserId = {}, userDto = {}", userId, userDto);
        return userService.update(userId, userMapper.toEntity(userDto));
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable long userId) {
        log.info("Requested user by id = {}", userId);
        return userMapper.toDto(userService.getById(userId));
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Requested all users");
        return userService.getAll();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Requested user delete by id = {}", userId);
        userService.deleteById(userId);
    }
}
