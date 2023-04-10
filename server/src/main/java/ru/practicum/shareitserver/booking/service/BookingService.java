package ru.practicum.shareitserver.booking.service;

import ru.practicum.shareitserver.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareitserver.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    // добавление нового запроса на бронирование
    BookingResponseDto saveBooking(BookingCreateRequestDto bookingCreateRequestDto, Long userId);

    // Подтверждение или отклонение запроса на бронирование
    BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved);

    // Просмотр информации о конкретном бронировании по её идентификатору
    BookingResponseDto getBooking(Long userId, Long bookingId);

    // Получение списка всех бронирований текущего пользователя
    List<BookingResponseDto> getAllBooking(Long userId, String state, Integer from, Integer size);

    // Получение списка бронирований для всех вещей текущего пользователя
    List<BookingResponseDto> getAllItemsByOwner(Long userId, String state, Integer from, Integer size);
}
