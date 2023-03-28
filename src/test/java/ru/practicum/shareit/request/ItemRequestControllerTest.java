package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserResponseDto;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private final UserResponseDto userResponseDto = new UserResponseDto(
            2L,
            "John",
            "john.doe@mail.com");

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            "need drill",
            userResponseDto,
            LocalDateTime.of(2023, 3, 28, 2,0)
    );

    private final ItemForItemRequestDto itemForItemRequestDto = new ItemForItemRequestDto(
            1L,
            "drill",
            "drill makita",
            true,
            1L,
            1L
    );

    private final ItemRequestForResponseDto itemRequestForResponseDto = new ItemRequestForResponseDto(
            1L,
            "need drill",
            userResponseDto,
            LocalDateTime.of(2023, 3, 28, 2,0),
            List.of(itemForItemRequestDto)
    );

    // тестируем метод add
    @Test
    void addTest() throws Exception {
        Mockito.when(itemRequestService.addNewItemRequest(any(), anyLong()))
                .thenReturn(itemRequestForResponseDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(itemRequestForResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description",
                        Matchers.is(itemRequestForResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$.requestor.id",
                        Matchers.is(itemRequestForResponseDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.created",
                        Matchers.is(itemRequestForResponseDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(status().isOk());
    }

    // тестируем метод getItemRequestsByOwner
    @Test
    void getItemRequestsByOwnerTest() throws Exception {
        Mockito.when(itemRequestService.getItemRequestsByOwner(anyLong()))
                .thenReturn(List.of(itemRequestForResponseDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].id", Matchers.is(itemRequestForResponseDto.getId()), Long.class))
                .andExpect(jsonPath("[0].description",
                        Matchers.is(itemRequestForResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("[0].requestor.id",
                        Matchers.is(itemRequestForResponseDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("[0].created",
                        Matchers.is(itemRequestForResponseDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(status().isOk());
    }

    // тестируем метод getAllItemsRequests
    @Test
    void getAllItemsRequestsTest() throws Exception {
        Mockito.when(itemRequestService.getAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestForResponseDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].id", Matchers.is(itemRequestForResponseDto.getId()), Long.class))
                .andExpect(jsonPath("[0].description",
                        Matchers.is(itemRequestForResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("[0].requestor.id",
                        Matchers.is(itemRequestForResponseDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("[0].created",
                        Matchers.is(itemRequestForResponseDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(status().isOk());
    }

    // тестируем метод getItemRequest
    @Test
    void getItemRequestTest() throws Exception {
        Mockito.when(itemRequestService.getItemRequest(anyLong(), anyLong()))
                .thenReturn(itemRequestForResponseDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(itemRequestForResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description",
                        Matchers.is(itemRequestForResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$.requestor.id",
                        Matchers.is(itemRequestForResponseDto.getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.created",
                        Matchers.is(itemRequestForResponseDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(status().isOk());
    }

}
