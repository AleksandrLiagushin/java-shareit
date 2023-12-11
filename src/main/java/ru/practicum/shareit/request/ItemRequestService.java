package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.model.ItemRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestService {
    private final RequestRepo requestStorage;

    public ItemRequest create(long userId, ItemRequest request) {
        return requestStorage.save(request);
    }

    public ItemRequest findRequestById(long userId, long requestId) {
        return requestStorage.findById(requestId).orElseThrow();
    }
}
