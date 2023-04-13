package ru.practicum.shareitserver.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareitserver.request.dto.ItemRequestDto;
import ru.practicum.shareitserver.request.dto.ItemRequestForResponseDto;
import ru.practicum.shareitserver.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.user.dto.UserResponseDto;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDateTime;

public class ItemRequestMapperImplTest {

    private ItemRequestMapperImpl itemRequestMapper;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemRequestForResponseDto itemRequestForResponseDto;

    @BeforeEach
    void init() {
        itemRequestMapper = new ItemRequestMapperImpl();

        User user2 = new User(2L, "Bill", "bill.doe@mail.com");
        UserResponseDto userResponseDto = new UserResponseDto(2L, "Bill", "bill.doe@mail.com");

        // ItemRequest
        itemRequest = new ItemRequest(1L, "need drill", user2,
                LocalDateTime.of(2023, 1, 28, 2,0));
        itemRequestDto = new ItemRequestDto(1L, "need drill", userResponseDto,
                LocalDateTime.of(2023, 1, 28, 2, 0));
        itemRequestForResponseDto = new ItemRequestForResponseDto(1L, "need drill", userResponseDto,
                LocalDateTime.of(2023, 1, 28, 2, 0), null);
    }

    @Test
    void toItemRequestTest() {
        Assertions.assertNull(itemRequestMapper.toItemRequest(null, null));
        ItemRequest itemRequestNew = itemRequestMapper.toItemRequest(itemRequestDto, 2L);
        Assertions.assertEquals(itemRequest.getId(), itemRequestNew.getId());
    }

    @Test
    void toItemRequestForResponseDtoTest() {
        Assertions.assertNull(itemRequestMapper.toItemRequestForResponseDto(null));
        ItemRequestForResponseDto itemRequestForResponseDtoNew = itemRequestMapper.toItemRequestForResponseDto(itemRequest);
        Assertions.assertEquals(itemRequestForResponseDto.getId(), itemRequestForResponseDtoNew.getId());
    }
}
