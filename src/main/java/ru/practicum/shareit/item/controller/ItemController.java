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

    // добавление новой вещи
    @PostMapping
    public ItemResponseDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @Valid @RequestBody ItemCreateRequestDto itemCreateRequestDto) {
        return itemService.addNewItem(itemCreateRequestDto, userId);
    }

    // редактирование вещи
    @PatchMapping("/{itemId}")
    public ItemResponseDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemId,
                                  @Valid @RequestBody ItemUpdateDto updateItemDto) {
        return itemService.updateItem(itemId, updateItemDto, userId);
    }

    // Просмотр информации о конкретной вещи по её идентификатору
    @GetMapping("/{itemId}")
    public ItemBookingResponseDto get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long itemId) {
        return itemService.getItemBooking(itemId, userId);
    }

    // Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой
    @GetMapping
    public List<ItemBookingResponseDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(value = "from", required = false) Integer from,
                                                 @RequestParam(value = "size", required = false) Integer size) {
        return itemService.getItemsBooking(userId, from, size);
    }

    // Поиск вещи потенциальным арендатором
    @GetMapping("/search")
    public List<ItemResponseDto> getItemsBySearch(@RequestParam("text") String text,
                                                  @RequestParam(value = "from", required = false) Integer from,
                                                  @RequestParam(value = "size", required = false) Integer size) {
        return itemService.getItemsBySearch(text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Later-User-Id") long userId,
                           @PathVariable Long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    // Добавление отзывов
    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody CommentCreateRequestDto commentCreateRequestDto,
                                         @PathVariable Long itemId) {
        if (commentCreateRequestDto.getText().isEmpty()) {
            throw new ValidationException("comment is empty");
        }
        return itemService.addComment(userId, commentCreateRequestDto, itemId);
    }
}
