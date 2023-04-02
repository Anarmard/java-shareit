package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemMapperImplTest {

    private ItemMapperImpl itemMapper;
    private Item item;
    private ItemCreateRequestDto itemCreateRequestDto;
    private ItemResponseDto itemResponseDto;
    private ItemForItemRequestDto itemForItemRequestDto;
    private ItemBookingResponseDto itemBookingResponseDto;

    @BeforeEach
    void init() {
        itemMapper = new ItemMapperImpl();

        User user1 = new User(1L, "John", "john.doe@mail.com");
        User user2 = new User(2L, "Bill", "bill.doe@mail.com");
        UserCreateRequestDto userCreateRequestDto1 = new UserCreateRequestDto(1L, "John", "john.doe@mail.com"); // owner
        UserCreateRequestDto userCreateRequestDto2 = new UserCreateRequestDto(2L, "Bill", "bill.doe@mail.com"); // booker
        UserResponseDto userResponseDto = new UserResponseDto(2L, "Bill", "bill.doe@mail.com");

        // ItemRequest
        ItemRequest itemRequest = new ItemRequest(1L, "need drill", user2,
                LocalDateTime.of(2023, 1, 28, 2, 0));
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "need drill", userResponseDto,
                LocalDateTime.of(2023, 1, 28, 2, 0));

        // Item
        item = new Item(1L,"drill","drill makita",true, user1, itemRequest);
        itemCreateRequestDto = new ItemCreateRequestDto(
                1L,"drill","drill makita",true, userCreateRequestDto1, 1L);
        itemResponseDto = new ItemResponseDto(
                1L,"drill","drill makita",true, userCreateRequestDto1, 1L);
        itemBookingResponseDto = new ItemBookingResponseDto(
                1L,"drill","drill makita",true,
                userCreateRequestDto2, itemRequestDto, null, null, null);
        itemForItemRequestDto = new ItemForItemRequestDto(1L,"drill","drill makita",true, 1L, 1L);
    }

    @Test
    void toItemDtoTestResponse() {
        Assertions.assertNull(itemMapper.toItemDto(null));
        ItemResponseDto itemResponseDtoNew = itemMapper.toItemDto(item);
        Assertions.assertEquals(itemResponseDto.getId(), itemResponseDtoNew.getId());

    }

    @Test
    void toItemForItemRequestDtoTest() {
        Assertions.assertNull(itemMapper.toItemForItemRequestDto(null));
        ItemForItemRequestDto itemForItemRequestDtoNew = itemMapper.toItemForItemRequestDto(item);
        Assertions.assertEquals(itemForItemRequestDto.getId(), itemForItemRequestDtoNew.getId());
    }

    @Test
    void toListItemDtoTest() {
        Assertions.assertNull(itemMapper.toListItemDto(null));
        List<ItemResponseDto> itemResponseDtoNewList = itemMapper.toListItemDto(List.of(item));
        Assertions.assertEquals(itemResponseDto.getId(), itemResponseDtoNewList.get(0).getId());
    }

    @Test
    void toItemTestFromRequest() {
        Item itemNew = itemMapper.toItem(itemCreateRequestDto, 1L);
        Assertions.assertEquals(item.getId(), itemNew.getId());
    }

    @Test
    void toItemTestFromResponse() {
        Item itemNew = itemMapper.toItem(itemResponseDto, 1L);
        Assertions.assertEquals(item.getName(), itemNew.getName());
    }

    @Test
    void toItemBookingDtoTest() {
        Assertions.assertNull(itemMapper.toItemBookingDto(null));
        ItemBookingResponseDto itemBookingResponseDtoNew = itemMapper.toItemBookingDto(item);
        Assertions.assertEquals(itemBookingResponseDto.getName(), itemBookingResponseDtoNew.getName());
    }
}
