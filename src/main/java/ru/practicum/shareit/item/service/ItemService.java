package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemResponseDto addNewItem(ItemCreateRequestDto itemCreateRequestDto, Long userId);

    ItemResponseDto updateItem(Long itemId, ItemUpdateDto updateItemDto, Long userId);

    ItemBookingResponseDto getItemBooking(Long itemId, Long userId);

    List<ItemBookingResponseDto> getItemsBooking(Long userId);

    List<ItemResponseDto> getItemsBySearch(String text);

    void deleteItem(Long userId, Long itemId);

    CommentResponseDto addComment(Long userId, CommentCreateRequestDto commentCreateRequestDto, Long itemId);
}
