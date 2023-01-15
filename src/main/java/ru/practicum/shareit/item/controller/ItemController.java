package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    // добавление новой вещи
    @PostMapping
    public ItemResponseDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @Valid @RequestBody ItemCreateRequestDto itemCreateRequest) {
        Item currentItem = itemMapper.toItem(itemCreateRequest, userId);
        Item returnedItem = itemService.addNewItem(currentItem);
        return itemMapper.toItemDto(returnedItem);
    }

    // редактирование вещи
    @PatchMapping("/{itemId}")
    public ItemResponseDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemId,
                                  @Valid @RequestBody ItemUpdateDto updateItemDto) {
        Item currentItem = itemMapper.toItem(updateItemDto, userId);
        Item returnedItem = itemService.updateItem(itemId, currentItem);
        return itemMapper.toItemDto(returnedItem);
    }

    // Просмотр информации о конкретной вещи по её идентификатору
    @GetMapping("/{itemId}")
    public ItemResponseDto get(@PathVariable Long itemId) {
        Item returnedItem = itemService.getItem(itemId);
        return itemMapper.toItemDto(returnedItem);
    }

    // Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой
    @GetMapping
    public List<ItemResponseDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<Item> returnedListItem = itemService.getItems(userId);
        return itemMapper.toListItemDto(returnedListItem);
    }

    // Поиск вещи потенциальным арендатором
    @GetMapping("/search")
    public List<ItemResponseDto> getItemsBySearch(@RequestParam("text") String text) {
        List<Item> returnedListItem = new ArrayList<>();
        if (!text.isEmpty()) {
            returnedListItem = itemService.getItemsBySearch(text);
        }
        return itemMapper.toListItemDto(returnedListItem);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Later-User-Id") long userId,
                           @PathVariable Long itemId) {
        itemService.deleteItem(userId, itemId);
    }
}
