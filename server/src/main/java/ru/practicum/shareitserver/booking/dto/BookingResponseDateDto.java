package ru.practicum.shareitserver.booking.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareitserver.booking.model.BookingStatus;

import java.time.LocalDateTime;

// отличается от BookingResponseDto тем, что здесь не Item и User, а их ID.
// этот dto используется для nextBooking & lastBooking

@Getter
@Setter
@AllArgsConstructor // конструктор на все параметры
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingResponseDateDto {
    Long id; // уникальный идентификатор бронирования
    LocalDateTime start; // дата и время начала бронирования
    LocalDateTime end; // дата и время конца бронирования
    Long itemId; // ID вещь, которую пользователь бронирует
    Long bookerId; // ID пользователь, который осуществляет бронирование
    BookingStatus status; // статус бронирования
}
