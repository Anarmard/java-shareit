package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    // добавление нового запроса на бронирование
    BookingResponseDto saveBooking(BookingCreateRequestDto bookingCreateRequestDto, Long userId);

    // Подтверждение или отклонение запроса на бронирование
    BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved);

    // Просмотр информации о конкретном бронировании по её идентификатору
    BookingResponseDto getBooking(Long userId, Long bookingId);

    // Получение списка всех бронирований текущего пользователя
    List<BookingResponseDto> getAllBooking(Long userId, String state);

    // Получение списка бронирований для всех вещей текущего пользователя
    List<BookingResponseDto> getAllItemsByOwner(Long userId, String state);
}
