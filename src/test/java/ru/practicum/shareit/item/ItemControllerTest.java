package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final UserCreateRequestDto userCreateRequestDto = new UserCreateRequestDto(
            1L,
            "John",
            "john.doe@mail.com");

    private final ItemResponseDto itemResponseDto = new ItemResponseDto(
            1L,
            "drill",
            "drill makita",
            true,
            userCreateRequestDto,
            1L);

    private final ItemCreateRequestDto itemCreateRequestDto = new ItemCreateRequestDto(
            1L,
            "drill",
            "drill makita",
            true,
            userCreateRequestDto,
            1L);

    private final ItemBookingResponseDto itemBookingResponseDto = new ItemBookingResponseDto(
            1L,
            "drill",
            "drill makita",
            true,
            userCreateRequestDto,
            null,
            null,
            null,
            null);

    private final CommentResponseDto commentResponseDto = new CommentResponseDto(
            1L,
            "good drill!",
            itemResponseDto,
            "Ivan",
            LocalDateTime.of(2022,1,21,11,0));

    private final CommentCreateRequestDto commentCreateRequestDto = new CommentCreateRequestDto(
            1L,
            "good drill!",
            itemCreateRequestDto,
            userCreateRequestDto,
            LocalDateTime.of(2022,1,21,11,0));

    // тестируем метод addItem
    @Test
    void addTest() throws Exception {
        Mockito.when(itemService.addNewItem(any(), anyLong()))
                .thenReturn(itemResponseDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemCreateRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(itemResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(itemResponseDto.getName()), String.class))
                .andExpect(jsonPath("$.description", Matchers.is(itemResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", Matchers.is(itemResponseDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", Matchers.is(itemResponseDto.getRequestId()), Long.class))
                .andExpect(status().isOk());

        Mockito.verify(itemService, Mockito.times(1))
                .addNewItem(any(), anyLong());
    }

    // тестируем метод update
    @Test
    void updateTest() throws Exception {
        Mockito.when(itemService.updateItem(anyLong(), any(), anyLong()))
                .thenReturn(itemResponseDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemResponseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(itemResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(itemResponseDto.getName()), String.class))
                .andExpect(jsonPath("$.description", Matchers.is(itemResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", Matchers.is(itemResponseDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", Matchers.is(itemResponseDto.getRequestId()), Long.class))
                .andExpect(status().isOk());

        Mockito.verify(itemService, Mockito.times(1))
                .updateItem(anyLong(), any(), anyLong());
    }

    // тестируем метод get
    @Test
    void getTest() throws Exception {
        Mockito.when(itemService.getItemBooking(anyLong(), anyLong()))
                .thenReturn(itemBookingResponseDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(itemBookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(itemBookingResponseDto.getName()), String.class))
                .andExpect(jsonPath("$.description", Matchers.is(itemBookingResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", Matchers.is(itemBookingResponseDto.getAvailable()), Boolean.class))
                .andExpect(status().isOk());

        Mockito.verify(itemService, Mockito.times(1))
                .getItemBooking(anyLong(), anyLong());
    }

    // тестируем метод getItems
    @Test
    void getItemsTest() throws Exception {
        Mockito.when(itemService.getItemsBooking(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemBookingResponseDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].id", Matchers.is(itemBookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("[0].name", Matchers.is(itemBookingResponseDto.getName()), String.class))
                .andExpect(jsonPath("[0].description", Matchers.is(itemBookingResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("[0].available", Matchers.is(itemBookingResponseDto.getAvailable()), Boolean.class))
                .andExpect(status().isOk());

        Mockito.verify(itemService, Mockito.times(1))
                .getItemsBooking(anyLong(), anyInt(), anyInt());
    }

    // тестируем метод getItemsBySearch
    @Test
    void getItemsBySearchTest() throws Exception {
        Mockito.when(itemService.getItemsBySearch(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemResponseDto));

        mvc.perform(get("/items/search?text='drill")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].id", Matchers.is(itemResponseDto.getId()), Long.class))
                .andExpect(jsonPath("[0].name", Matchers.is(itemResponseDto.getName()), String.class))
                .andExpect(jsonPath("[0].description", Matchers.is(itemResponseDto.getDescription()), String.class))
                .andExpect(jsonPath("[0].available", Matchers.is(itemResponseDto.getAvailable()), Boolean.class))
                .andExpect(status().isOk());

        Mockito.verify(itemService, Mockito.times(1))
                .getItemsBySearch(anyString(), anyInt(), anyInt());
    }

    // тестируем метод addComment
    @Test
    void addCommentTest() throws Exception {
        Mockito.when(itemService.addComment(anyLong(), any(), anyLong()))
                .thenReturn(commentResponseDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentCreateRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(commentResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", Matchers.is(commentResponseDto.getText()), String.class))
                .andExpect(status().isOk());

        Mockito.verify(itemService, Mockito.times(1))
                .addComment(anyLong(), any(), anyLong());
    }

    // тестируем метод deleteItem
    @Test
    void deleteItemTest() throws Exception {
        mvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
