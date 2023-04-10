package ru.practicum.shareitserver.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitserver.request.dto.ItemRequestDto;
import ru.practicum.shareitserver.request.dto.ItemRequestForResponseDto;
import ru.practicum.shareitserver.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String USERID = "X-Sharer-User-Id";

    // добавить новый запрос вещи
    @PostMapping
    public ItemRequestForResponseDto add(@RequestHeader(USERID) Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addNewItemRequest(itemRequestDto, userId);
    }

    // получить список СВОИХ запросов вместе с данными об ответах на них
    @GetMapping
    public List<ItemRequestForResponseDto> getItemRequestsByOwner(@RequestHeader(USERID) Long userId) {
        return itemRequestService.getItemRequestsByOwner(userId);
    }

    // получить список ВCЕХ запросов, созданных другими пользователями
    @GetMapping("/all")
    public List<ItemRequestForResponseDto> getAllItemsRequests(
            @RequestHeader(USERID) Long userId,
            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    // получить данные об одном конкретном запросе вместе с данными об ответах на него
    @GetMapping("/{requestId}")
    public ItemRequestForResponseDto getItemRequest(@RequestHeader(USERID) Long userId,
                                                    @PathVariable Long requestId) {
        return itemRequestService.getItemRequest(userId, requestId);
    }
}
