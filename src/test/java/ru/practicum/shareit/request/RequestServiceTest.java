package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.IdValidationException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {
    @InjectMocks
    private RequestService requestService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private RequestMapper requestMapper;

    @Test
    void create_shouldCreateNewRequest() {
        User user = createUser(1L, "Vasya", "vas@mail.ru");
        RequestDto requestDto = createRequestDto(0L, "text", 1L);
        Request requestDb = createRequest(1L, "text", user);
        Request request = createRequest(1L, "text", user);

        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(requestMapper.toEntity(requestDto)).thenReturn(requestDb);
        Mockito.when(requestRepository.save(requestDb)).thenReturn(request);

        Request result = requestService.create(requestDto, 1L);

        assertEquals(request, result);
    }

    @Test
    void create_shouldThrowIdValidException() {
        RequestDto requestDto = createRequestDto(1L, "text", 1L);

        IdValidationException exception = assertThrows(
                IdValidationException.class,
                () -> requestService.create(requestDto, 1L));

        assertNotNull(exception.getMessage());
    }

    @Test
    void create_shouldThrowUserNotFoundException() {
        User user = createUser(2L, "Vasya", "vas@mail.ru");
        RequestDto requestDto = createRequestDto(0L, "text", 1L);

        userRepository.save(user);
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> requestService.create(requestDto, 1L));

        assertNotNull(exception.getMessage());
    }

    @Test
    void findRequestById_shouldReturnRequest() {
        Request request = new Request();
        Item item = new Item();
        RequestDto requestDto = new RequestDto();
        List<Item> items = List.of(item);

        Mockito.when(userRepository.existsById(any())).thenReturn(true);
        Mockito.when(requestRepository.existsById(any())).thenReturn(true);
        Mockito.when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        Mockito.when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));
        Mockito.when(requestMapper.toDto(request, items)).thenReturn(requestDto);

        RequestDto result = requestService.findRequestById(1L, 1L);
        assertEquals(requestDto, result);
    }

    @Test
    void findRequestById_shouldThrowUserNotFoundException() {
        User user = createUser(2L, "Vasya", "vas@mail.ru");

        userRepository.save(user);
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> requestService.findRequestById(1L, 1L));

        assertNotNull(exception.getMessage());
    }

    @Test
    void findRequestById_shouldThrowItemNotFoundException() {
        User user = createUser(1L, "Vasya", "vas@mail.ru");
        Request request = createRequest(1L, "text", user);

        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        requestRepository.save(request);
        ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> requestService.findRequestById(1L, 2L));

        assertNotNull(exception.getMessage());
    }

    @Test
    void getRequests() {
        Item item = new Item();
        Request itemRequest = new Request();
        itemRequest.setId(1L);
        RequestDto itemRequestDto = new RequestDto();
        RequestDto itemRequestDto1 = new RequestDto();

        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(requestRepository.findByUserId(1L)).thenReturn(List.of(itemRequest));
        List<Request> baseItemRequests = List.of(itemRequest);

        Mockito.when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));
        List<Item> items = List.of(item);

        Mockito.when(requestMapper.toDto(itemRequest, items)).thenReturn(itemRequestDto, itemRequestDto1);

        List<RequestDto> itemRequestFinal = baseItemRequests.stream().map(x -> itemRequestDto)
                .sorted(Comparator.comparing((RequestDto::getCreated)))
                .collect(Collectors.toList());

        List<RequestDto> result = requestService.getRequests(1L);

        assertEquals(itemRequestFinal, result);
    }

    @Test
    void getRequests_shouldThrowUserNotFoundException() {
        User user = createUser(2L, "Vasya", "vas@mail.ru");

        userRepository.save(user);
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> requestService.getRequests(1L));

        assertNotNull(exception.getMessage());
    }

    @Test
    void getRequests_shouldReturnEmtyList() {
        User user = createUser(2L, "Vasya", "vas@mail.ru");

        userRepository.save(user);
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(requestRepository.findByUserId(user.getId())).thenReturn(Collections.emptyList());
        List<RequestDto> actual = requestService.getRequests(user.getId());

        assertTrue(actual.isEmpty());
    }

    @Test
    void getAllRequests() {
        List<Request> allItemRequest = new ArrayList<>();
        Page<Request> allPage = new PageImpl<>(allItemRequest);

        Mockito.when(requestRepository.findAll(PageRequest.of(0, 1, Sort.by("created").descending())))
                .thenReturn(allPage);

        List<RequestDto> outputItemRequestDto =
                requestRepository.findAll(PageRequest.of(0, 1, Sort.by("created").descending())).stream()
                        .filter(x -> x.getUser().getId() != 1L)
                        .map(x -> requestMapper.toDto(x, itemRepository.findByRequestId(x.getId())))
                        .collect(Collectors.toList());

        List<RequestDto> result = requestService.getAllRequests(1L, PageRequest.of(0, 1, Sort.by("created").descending()));
        assertEquals(outputItemRequestDto, result);
    }

    private User createUser(long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
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
}