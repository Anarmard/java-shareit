package ru.practicum.shareitgateway.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.exception.ValidationException;
import ru.practicum.shareitgateway.request.dto.ItemRequestDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String USERID = "X-Sharer-User-Id";

    // добавить новый запрос вещи
    @PostMapping
    public ResponseEntity<Object> addNewItemRequest(@RequestHeader(USERID) Long userId,
                                      @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.add(userId, itemRequestDto);
    }

    // получить список СВОИХ запросов вместе с данными об ответах на них
    @GetMapping
    public ResponseEntity<Object> getItemRequestsByOwner(@RequestHeader(USERID) Long userId) {
        return itemRequestClient.getAllByOwner(userId);
    }

    // получить список ВCЕХ запросов, созданных другими пользователями
    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemsRequests(
            @RequestHeader(USERID) Long userId,
            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        checkPageParams(from, size);
        return itemRequestClient.getAll(userId, from, size);
    }

    // получить данные об одном конкретном запросе вместе с данными об ответах на него
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(USERID) Long userId,
                                                    @PathVariable Long requestId) {
        return itemRequestClient.getById(userId, requestId);
    }

    private void checkPageParams(Integer from, Integer size) {
        if (size < 1) throw new ValidationException("Page size must not be less than one");
        if (from < 0) throw new ValidationException("Index 'from' must not be less than zero");
    }
}
