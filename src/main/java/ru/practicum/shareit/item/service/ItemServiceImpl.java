package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Item addNewItem(Long userId, Item item) {
        userService.getUserById(userId);
        item.setOwner(userService.getUserById(item.getOwner().getId()));
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Long itemId,  Item item) {
        validate(itemId, item);
        return itemRepository.update(itemId, item);
    }

    @Override
    public Item getItem(Long itemId) {
        return itemRepository.findByItemId(itemId);
    }

    @Override
    public List<Item> getItems(Long userId) {
        return itemRepository.findAllItemsByUserId(userId);
    }

    @Override
    public List<Item> getItemsBySearch(String text) {
        return itemRepository.findAllItemsThroughSearch(text);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    private void validate(Long itemId, Item item) {
        if (!itemRepository.checkUserOwnsItem(item.getOwner().getId(),itemId)) {
            throw new NotFoundException("user is not owner");
        }
    }
}
