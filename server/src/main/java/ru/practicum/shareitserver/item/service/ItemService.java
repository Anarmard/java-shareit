package ru.practicum.shareitserver.item.service;

import ru.practicum.shareitserver.item.dto.*;
import ru.practicum.shareitserver.item.model.Item;

import java.util.List;

public interface ItemService {

    Item getItem(Long itemId);

    ItemResponseDto addNewItem(ItemCreateRequestDto itemCreateRequestDto, Long userId);

    ItemResponseDto updateItem(Long itemId, ItemResponseDto itemResponseDto, Long userId);

    ItemBookingResponseDto getItemBooking(Long itemId, Long userId);

    List<ItemBookingResponseDto> getItemsBooking(Long userId, Integer from, Integer size);

    List<ItemResponseDto> getItemsBySearch(String text, Integer from, Integer size);

    void deleteItem(Long userId, Long itemId);

    CommentResponseDto addComment(Long userId, CommentCreateRequestDto commentCreateRequestDto, Long itemId);
}
