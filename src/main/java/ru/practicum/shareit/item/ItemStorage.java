package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item create(Item item);

    Item update(long userId, long itemId, Item item);

    Item getById(long itemId);

    List<Item> getItemsByUserId(long userId);

    List<Item> findItems(String text);
}
