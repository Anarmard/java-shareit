package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addNewItem(Long userId, Item item);

    Item updateItem(Long itemId, Item item);

    Item getItem(Long itemId);

    List<Item> getItems(Long userId);

    List<Item> getItemsBySearch(String text);
    void deleteItem(Long userId, Long itemId);
}
