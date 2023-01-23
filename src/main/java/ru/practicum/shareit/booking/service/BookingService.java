package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    // добавление нового запроса на бронирование
    Booking saveBooking(Booking booking);

    // Подтверждение или отклонение запроса на бронирование
    Booking approveBooking(Long userId, Long bookingId, Boolean approved);

    // Просмотр информации о конкретном бронировании по её идентификатору
    Booking getBooking(Long userId, Long bookingId);

    // Получение списка всех бронирований текущего пользователя
    List<Booking> getAllBooking(Long userId, String state);

    // Получение списка бронирований для всех вещей текущего пользователя
    List<Booking> getAllItemsByOwner(Long userId, String state);
}
