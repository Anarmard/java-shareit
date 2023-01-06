package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class ItemRepository {
    private static final List<Item> ALL_ITEMS = new ArrayList<>();
    private static long idItem = 0L;

    public Item save(Item item) {
        idItem++;
        item.setId(idItem);
        ALL_ITEMS.add(item);
        return item;
    }

    public Item update(Long itemId, Item item) {
        Item savedItem = findByItemId(itemId);
        if (Objects.isNull(savedItem)) {
            return null;
        }
        ALL_ITEMS.remove(findByItemId(itemId));
        if (Objects.nonNull(item.getName())) {
            savedItem.setName(item.getName());
        }
        if (Objects.nonNull(item.getDescription())) {
            savedItem.setDescription(item.getDescription());
        }
        if (Objects.nonNull(item.getAvailable())) {
            savedItem.setAvailable(item.getAvailable());
        }
        ALL_ITEMS.add(savedItem);
        return savedItem;
    }

    public Item findByItemId(long itemId) {
        for (int id = 1; id <= ALL_ITEMS.size(); id++) {
            Item currentItem = ALL_ITEMS.get(id-1);
            if (itemId == currentItem.getId()) {
                return currentItem;
            }
        }
        return null;
    }

    public List<Item> findAllItemsByUserId(long userId) {
        List<Item> allUserItems = new ArrayList<>();
        for (int id = 1; id <= ALL_ITEMS.size(); id++) {
            Item currentItem = ALL_ITEMS.get(id-1);
            if (userId == currentItem.getOwner().getId()) {
                 allUserItems.add(currentItem);
            }
        }
        return allUserItems;
    }

    public List<Item> findAllItemsThroughSearch(String text) {
        List<Item> allItemsWithText = new ArrayList<>();
        for (int id = 1; id <= ALL_ITEMS.size(); id++) {
            Item currentItem = ALL_ITEMS.get(id-1);
            if (((currentItem.getName().toLowerCase().contains(text.toLowerCase()))
                    || (currentItem.getDescription().toLowerCase().contains(text.toLowerCase())) )
                    && (currentItem.getAvailable())) {
                allItemsWithText.add(currentItem);
            }
        }
        return allItemsWithText;
    }

    public void deleteByUserIdAndItemId(long userId, long itemId) {
        for (int id = 1; id <= ALL_ITEMS.size(); id++) {
            Item currentItem = ALL_ITEMS.get(id-1);
            if (userId == currentItem.getOwner().getId() && itemId == currentItem.getId()) {
                ALL_ITEMS.remove(currentItem);
            }
        }
    }

    public boolean checkUserOwnsItem(long userId, long itemId) {
        Item currentItem = findByItemId(itemId);
        return userId == currentItem.getOwner().getId();
    }
}
