package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;

import java.util.List;

public interface ItemRequestService {

    // добавить новый запрос вещи
    ItemRequestForResponseDto addNewItemRequest(ItemRequestDto itemRequestDto, Long userId);

    // получить список СВОИХ запросов вместе с данными об ответах на них
    List<ItemRequestForResponseDto> getItemRequestsByOwner(Long userId);

    // получить список ВCЕХ запросов, созданных другими пользователями
    List<ItemRequestForResponseDto> getAllItemRequests(Long userId, Integer from, Integer size);

    // получить данные об одном конкретном запросе вместе с данными об ответах на него
    ItemRequestForResponseDto getItemRequest(Long userId, Long requestId);

}
