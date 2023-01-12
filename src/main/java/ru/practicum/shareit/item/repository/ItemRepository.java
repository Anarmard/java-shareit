package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemRepository {
    private static Long idItem = 0L;
    private static final Map<Long, Item> ID_ITEM_MAP = new HashMap<>();
    private static final Map<Long, List<Item>> USER_ID_ITEM_LIST_MAP = new HashMap<>();

    public Item save(Item item) {
        idItem++;
        item.setId(idItem);
        ID_ITEM_MAP.put(idItem, item);

        List<Item> userItemList = USER_ID_ITEM_LIST_MAP.getOrDefault(item.getOwner().getId(), new ArrayList<>());
        userItemList.add(item);
        USER_ID_ITEM_LIST_MAP.put(item.getOwner().getId(), userItemList);
        return item;
    }

    public Item update(Long itemId, Item item) {
        Item savedItem = findByItemId(itemId);
        ID_ITEM_MAP.remove(itemId);
        USER_ID_ITEM_LIST_MAP.get(item.getOwner().getId()).removeIf(n -> Objects.equals(n, item));

        if (Objects.nonNull(item.getName())) {
            savedItem.setName(item.getName());
        }
        if (Objects.nonNull(item.getDescription())) {
            savedItem.setDescription(item.getDescription());
        }
        if (Objects.nonNull(item.getAvailable())) {
            savedItem.setAvailable(item.getAvailable());
        }

        ID_ITEM_MAP.put(itemId, savedItem);

        return savedItem;
    }

    public Item findByItemId(Long itemId) {
        if (ID_ITEM_MAP.containsKey(itemId)) {
            return ID_ITEM_MAP.get(itemId);
        } else {
            throw new NotFoundException("Item is not found");
        }
    }

    public List<Item> findAllItemsByUserId(Long userId) {
        if (USER_ID_ITEM_LIST_MAP.containsKey(userId)) {
            return USER_ID_ITEM_LIST_MAP.get(userId);
        } else {
            throw new NotFoundException("Owner is not found");
        }
    }

    public List<Item> findAllItemsThroughSearch(String text) {
        String textLowerCase = text.toLowerCase();
        List<Item> allItemsWithText = new ArrayList<>();
        for (Item currentItem:ID_ITEM_MAP.values()) {
            if (((currentItem.getName().toLowerCase().contains(textLowerCase))
                    || (currentItem.getDescription().toLowerCase().contains(textLowerCase)))
                    && (currentItem.getAvailable())) {
                allItemsWithText.add(currentItem);
            }
        }
        return allItemsWithText;
    }

    public void deleteByUserIdAndItemId(Long userId, Long itemId) {
        ID_ITEM_MAP.remove(itemId);
        USER_ID_ITEM_LIST_MAP.get(userId).removeIf(n -> n.getId().equals(itemId));
    }

    public boolean checkUserOwnsItem(Long userId, Long itemId) {
        return Objects.equals(userId, findByItemId(itemId).getOwner().getId());
    }
}
