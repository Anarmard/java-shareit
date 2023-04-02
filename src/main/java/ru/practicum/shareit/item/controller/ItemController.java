package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String USERID = "X-Sharer-User-Id";

    // добавление новой вещи
    @PostMapping
    public ItemResponseDto add(@RequestHeader(USERID) Long userId,
                               @Valid @RequestBody ItemCreateRequestDto itemCreateRequestDto) {
        return itemService.addNewItem(itemCreateRequestDto, userId);
    }

    // редактирование вещи
    @PatchMapping("/{itemId}")
    public ItemResponseDto update(@RequestHeader(USERID) Long userId,
                                  @PathVariable Long itemId,
                                  @Valid @RequestBody ItemResponseDto itemResponseDto) {
        return itemService.updateItem(itemId, itemResponseDto, userId);
    }

    // Просмотр информации о конкретной вещи по её идентификатору
    @GetMapping("/{itemId}")
    public ItemBookingResponseDto get(@RequestHeader(USERID) Long userId,
                                      @PathVariable Long itemId) {
        return itemService.getItemBooking(itemId, userId);
    }

    // Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой
    @GetMapping
    public List<ItemBookingResponseDto> getItems(
            @RequestHeader(USERID) Long userId,
            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return itemService.getItemsBooking(userId, from, size);
    }

    // Поиск вещи потенциальным арендатором
    @GetMapping("/search")
    public List<ItemResponseDto> getItemsBySearch(
            @RequestParam("text") String text,
            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return itemService.getItemsBySearch(text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(USERID) long userId,
                           @PathVariable Long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    // Добавление отзывов
    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@RequestHeader(USERID) Long userId,
                                         @Valid @RequestBody CommentCreateRequestDto commentCreateRequestDto,
                                         @PathVariable Long itemId) {
        if (commentCreateRequestDto.getText().isEmpty()) {
            throw new ValidationException("comment is empty");
        }
        return itemService.addComment(userId, commentCreateRequestDto, itemId);
    }
}
