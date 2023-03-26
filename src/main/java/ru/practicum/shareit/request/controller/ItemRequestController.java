package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    // добавить новый запрос вещи
    @PostMapping
    public ItemRequestForResponseDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addNewItemRequest(itemRequestDto, userId);
    }

    // получить список СВОИХ запросов вместе с данными об ответах на них
    @GetMapping
    public List<ItemRequestForResponseDto> getItemRequestsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getItemRequestsByOwner(userId);
    }

    // получить список ВCЕХ запросов, созданных другими пользователями
    @GetMapping("/all")
    public List<ItemRequestForResponseDto> getAllItemsRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                               @RequestParam(value = "from", required = false) Integer from,
                                                               @RequestParam(value = "size", required = false) Integer size) {
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    // получить данные об одном конкретном запросе вместе с данными об ответах на него
    @GetMapping("/{requestId}")
    public ItemRequestForResponseDto getItemRequest(@PathVariable Long requestId) {
        return itemRequestService.getItemRequest(requestId);
    }
}
