package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final UserResponseDto userResponseDto = new UserResponseDto(
            1L,
            "John",
            "john.doe@mail.com");

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

    private final BookingCreateRequestDto bookingCreateRequestDto = new BookingCreateRequestDto(
            1L,
            LocalDateTime.of(2023, 5, 28, 3, 0),
            LocalDateTime.of(2023, 6, 28, 4, 0),
            1L,
            userResponseDto,
            BookingStatus.WAITING
    );

    private final BookingResponseDto bookingResponseDto = new BookingResponseDto(
            1L,
            LocalDateTime.of(2023, 5, 28, 3, 0),
            LocalDateTime.of(2023, 6, 28, 4, 0),
            itemResponseDto,
            userResponseDto,
            BookingStatus.WAITING
    );

    // тестируем метод saveBooking
    @Test
    void saveBookingTest() throws Exception {
        Mockito.when(bookingService.saveBooking(any(), anyLong()))
                .thenReturn(bookingResponseDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingCreateRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        Matchers.is(bookingResponseDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        Matchers.is(bookingResponseDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status",
                        Matchers.is(bookingResponseDto.getStatus().toString()), String.class))
                .andExpect(status().isOk());
    }

    // тестируем метод approveBooking
    @Test
    void approveBookingTest() throws Exception {
        Mockito.when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingResponseDto);

        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        Matchers.is(bookingResponseDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        Matchers.is(bookingResponseDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status",
                        Matchers.is(bookingResponseDto.getStatus().toString()), String.class))
                .andExpect(status().isOk());
    }

    // тестируем метод getBooking
    @Test
    void getBookingTest() throws Exception {
        Mockito.when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        Matchers.is(bookingResponseDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        Matchers.is(bookingResponseDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.status",
                        Matchers.is(bookingResponseDto.getStatus().toString()), String.class))
                .andExpect(status().isOk());
    }

    // тестируем метод getAllBooking
    @Test
    void getAllBookingTest() throws Exception {
        Mockito.when(bookingService.getAllBooking(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].id", Matchers.is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("[0].start",
                        Matchers.is(bookingResponseDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("[0].end",
                        Matchers.is(bookingResponseDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("[0].status",
                        Matchers.is(bookingResponseDto.getStatus().toString()), String.class))
                .andExpect(status().isOk());
    }

    // тестируем метод getAllItemsByOwner
    @Test
    void getAllItemsByOwnerTest() throws Exception {
        Mockito.when(bookingService.getAllItemsByOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto));

        mvc.perform(get("/bookings/owner?state=ALL")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].id", Matchers.is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("[0].start",
                        Matchers.is(bookingResponseDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("[0].end",
                        Matchers.is(bookingResponseDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("[0].status",
                        Matchers.is(bookingResponseDto.getStatus().toString()), String.class))
                .andExpect(status().isOk());
    }

}
