package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;
    private final UserStorage userStorage;

    public ItemDto create(Item item) {
        if (!userStorage.contains(item.getOwner())) {
            throw new UserNotFoundException("This item is owned by other user");
        }
        return itemMapper.toDto(itemStorage.create(item));
    }

    public ItemDto update(long userId, long itemId, Item item) {
        if (itemStorage.getById(itemId).getOwner() != userId) {
            throw new UserNotFoundException("This item is owned by other user");
        }

        Item savedItem = itemStorage.getById(itemId);
        if (item.getAvailable() != null) {
            savedItem.setAvailable(item.getAvailable());
        }
        if (item.getName() != null) {
            savedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            savedItem.setDescription(item.getDescription());
        }

        return itemMapper.toDto(itemStorage.update(userId, itemId, savedItem));
    }

    public ItemDto getById(long itemId) {
        return itemMapper.toDto(itemStorage.getById(itemId));
    }

    public List<ItemDto> getItemsByUserId(long userId) {
        return itemStorage.getItemsByUserId(userId).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> findItems(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return Collections.emptyList();
        }

        List<Item> items = itemStorage.findItems(text.toLowerCase());

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        return items.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }
}
