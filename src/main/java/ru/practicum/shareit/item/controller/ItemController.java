package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final UserService userService;

    // добавление новой вещи
    @PostMapping
    public ItemResponseDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @Valid @RequestBody ItemCreateRequestDto itemCreateRequest) {

        userService.getUserById(userId);
        Item currentItem = itemMapper.toItem(itemCreateRequest, userId);
        Item returnedItem = itemService.addNewItem(currentItem);
        return itemMapper.toItemDto(returnedItem);
    }

    // редактирование вещи
    @PatchMapping("/{itemId}")
    public ItemResponseDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemId,
                                  @Valid @RequestBody ItemUpdateDto updateItemDto) {
        userService.getUserById(userId);
        Item currentItem = itemMapper.toItem(updateItemDto, userId);
        Item returnedItem = itemService.updateItem(itemId, currentItem);
        return itemMapper.toItemDto(returnedItem);
    }

    // Просмотр информации о конкретной вещи по её идентификатору
    @GetMapping("/{itemId}")
    public ItemBookingResponseDto get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long itemId) {
        return itemService.getItemBooking(itemId, userId);
    }

    // Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой
    @GetMapping
    public List<ItemBookingResponseDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        userService.getUserById(userId);
        return itemService.getItemsBooking(userId);
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
        userService.getUserById(userId);
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
        Comment currentComment = commentMapper.toComment(commentCreateRequestDto);
        currentComment.setItem(itemService.getItem(itemId));
        currentComment.setAuthor(userService.getUserById(userId));
        return itemService.addComment(currentComment);
    }
}
