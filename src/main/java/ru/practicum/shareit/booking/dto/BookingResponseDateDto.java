package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

// отличается от BookingResponseDto тем, что здесь не Item и User, а их ID.
// этот dto используется для nextBooking & lastBooking

@Getter
@AllArgsConstructor // конструктор на все параметры
public class BookingResponseDateDto {
    private Long id; // уникальный идентификатор бронирования
    private LocalDateTime start; // дата и время начала бронирования
    private LocalDateTime end; // дата и время конца бронирования
    private Long itemId; // ID вещь, которую пользователь бронирует
    private Long bookerId; // ID пользователь, который осуществляет бронирование
    private BookingStatus status; // статус бронирования
}
