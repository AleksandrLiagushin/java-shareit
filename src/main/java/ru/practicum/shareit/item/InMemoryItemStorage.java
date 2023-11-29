package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long uniqueId;

    @Override
    public Item create(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(long userId, long itemId, Item item) {
        item.setOwner(userId);
        item.setId(itemId);
        items.replace(item.getId(), item);
        return item;
    }

    @Override
    public Item getById(long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ItemNotExistException("There is no item with such id");
        }

        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsByUserId(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findItems(String text) {
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text)
                        && item.getAvailable())
                .collect(Collectors.toList());
    }

    private long generateId() {
        return ++uniqueId;
    }
}
