package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentReponseDto;
import ru.practicum.shareit.item.dto.ItemBookingResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addNewItem(Item item);

    Item updateItem(Long itemId, Item item);

    ItemBookingResponseDto getItemBooking(Long itemId);

    List<ItemBookingResponseDto> getItemsBooking(Long userId);

    List<Item> getItemsBySearch(String text);

    void deleteItem(Long userId, Long itemId);

    CommentReponseDto addComment(Comment comment);
}
