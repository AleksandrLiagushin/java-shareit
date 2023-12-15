package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IdValidationException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    public Request create(RequestDto requestDto, @Valid long userId) {
        if (requestDto.getId() != 0) {
            throw new IdValidationException("Id must be zero");
        }

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("There is no user with such id = " + requestDto.getRequester());
        }

        Request request = requestMapper.toEntity(requestDto);
        request.setUser(userRepository.findById(userId).orElseThrow());
        request.setCreated(LocalDateTime.now());

        return requestRepository.save(request);
    }

    public RequestDto findRequestById(long userId, long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("There is no user with such id = " + userId);
        }

        if (!requestRepository.existsById(requestId)) {
            throw new ItemNotFoundException("There is no request with such id = " + requestId);
        }

        return requestMapper.toDto(requestRepository.findById(requestId).orElseThrow(),
                itemRepository.findByRequestId(requestId));
    }

    public List<RequestDto> getRequests(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("There is no user with such id = " + userId);
        }

        List<Request> requests = requestRepository.findByUserId(userId);
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }

        return requests.stream()
                .map(r -> requestMapper.toDto(r, itemRepository.findByRequestId(r.getId())))
                .collect(Collectors.toList());
    }

    public List<RequestDto> getAllRequests(long userId, Pageable pageable) {
        return requestRepository.findAll(pageable).stream()
                .filter(r -> r.getUser().getId() != userId)
                .map(r -> requestMapper.toDto(r, itemRepository.findByRequestId(r.getId())))
                .collect(Collectors.toList());
    }
}
