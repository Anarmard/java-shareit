package ru.practicum.shareitgateway.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.exception.ValidationException;
import ru.practicum.shareitgateway.item.dto.CommentRequestDto;
import ru.practicum.shareitgateway.item.dto.ItemCreateDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String USERID = "X-Sharer-User-Id";

    // добавление новой вещи
    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(USERID) Long userId,
                                      @Valid @RequestBody ItemCreateDto itemCreateDto) {
        return itemClient.add(userId, itemCreateDto);
    }

    // редактирование вещи
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USERID) Long userId,
                                  @PathVariable Long itemId,
                                  @Valid @RequestBody ItemCreateDto itemCreateDto) {
        return itemClient.update(userId, itemId, itemCreateDto);
    }

    // Просмотр информации о конкретной вещи по её идентификатору
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(USERID) Long userId,
                                      @PathVariable Long itemId) {
        return itemClient.getById(userId, itemId);
    }

    // Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой
    @GetMapping
    public ResponseEntity<Object> getItems(
            @RequestHeader(USERID) Long userId,
            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        checkPageParams(from, size);
        return itemClient.getItemsBooking(userId, from, size);
    }

    // Поиск вещи потенциальным арендатором
    @GetMapping("/search")
    public ResponseEntity<Object> getItemsBySearch(
            @RequestParam("text") String text,
            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        checkPageParams(from, size);
        return itemClient.getItemsBySearch(text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(USERID) long userId,
                           @PathVariable Long itemId) {
        itemClient.deleteItem(userId, itemId);
    }

    // Добавление отзывов
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USERID) Long userId,
                                         @Valid @RequestBody CommentRequestDto commentRequestDto,
                                         @PathVariable Long itemId) {
        if (commentRequestDto.getText().isEmpty()) {
            throw new ValidationException("comment is empty");
        }
        return itemClient.addComment(userId, commentRequestDto, itemId);
    }

    private void checkPageParams(Integer from, Integer size) {
        if (size < 1) throw new ValidationException("Page size must not be less than one");
        if (from < 0) throw new ValidationException("Index 'from' must not be less than zero");
    }
}
