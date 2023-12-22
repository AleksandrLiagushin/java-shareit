package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserCreationException;
import ru.practicum.shareit.exception.UserNotExistException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    User userIn = createUser(0L, "Vasya", "vas@mail.ru");
    User userBd = createUser(1L, "Vasya", "vas@mail.ru");
    User userBdUpd = createUser(1L, "Vanya", "vas@mail.ru");
    UserDto userBdDto = createUserDto(1L, "Vasya", "vas@mail.ru");
    UserDto userBdUpdDto = createUserDto(1L, "Vanya", "vas@mail.ru");
    List<User> users = new ArrayList<>();

    @Test
    void create_ShouldCreateUser() {
        Mockito.when(userRepository.save(userIn)).thenReturn(userBd);
        Mockito.when(userMapper.toDto(userBd)).thenReturn(userBdDto);

        UserDto result = userService.create(userIn);

        assertEquals(userBdDto, result);
    }

    @Test
    void create_shouldThrowExceptionId() {
        UserCreationException exception = assertThrows(
                UserCreationException.class,
                () -> userService.create(userBd));

        assertNotNull(exception.getMessage());
    }

    @Test
    void create_shouldThrowExceptionSameEmail() {
        Mockito.when(userRepository.save(userIn))
                .thenThrow(new UserCreationException("User with such email already exists"));

        UserCreationException exception = assertThrows(
                UserCreationException.class,
                () -> userService.create(userIn));
        assertNotNull(exception.getMessage());
    }

    @Test
    void update_shouldUpdateUser() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userBd));

        Mockito.when(userRepository.save(userBd)).thenReturn(userBdUpd);
        Mockito.when(userMapper.toDto(userBdUpd)).thenReturn(userBdUpdDto);

        UserDto result = userService.update(1L, userBdUpd);

        Assertions.assertEquals(userBdUpdDto.getName(), result.getName());
    }

    @Test
    void update_shouldThrowUserNotExistException() {
        UserNotExistException exception = assertThrows(
                UserNotExistException.class,
                () -> userService.update(1L, userBd));

        assertNotNull(exception.getMessage());
    }

    @Test
    void deleteById_shouldNotThrowException() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> userService.deleteById(1L));
    }

    @Test
    void deleteById_shouldThrowException() {
        UserNotExistException exception = assertThrows(
                UserNotExistException.class,
                () -> userService.deleteById(1L));

        assertNotNull(exception.getMessage());
    }

    @Test
    void getById_shouldReturnUserDb() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(userBd));

        User result = userService.getById(1L);

        Assertions.assertEquals(userBd, result);
    }

    @Test
    void getById_shouldThrowException() {
        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> userService.getById(1L));

        assertNotNull(exception.getMessage());
    }

    @Test
    void getAll() {
        users.add(userBd);
        Mockito.when(userRepository.findAll()).thenReturn(users);
        List<UserDto> usersDto = users.stream()
                .map(x -> userMapper.toDto(x))
                .collect(Collectors.toList());

        List<UserDto> result = userService.getAll();

        Assertions.assertEquals(usersDto, result);
    }

    private User createUser(long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    private UserDto createUserDto(long id, String name, String email) {
        return UserDto.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }
}