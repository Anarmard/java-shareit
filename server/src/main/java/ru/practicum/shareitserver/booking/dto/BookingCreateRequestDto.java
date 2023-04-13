package ru.practicum.shareitserver.booking.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareitserver.booking.model.BookingStatus;
import ru.practicum.shareitserver.user.dto.UserResponseDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor // конструктор на все параметры
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingCreateRequestDto {
    Long id; // уникальный идентификатор бронирования
    LocalDateTime start; // дата и время начала бронирования
    LocalDateTime end; // дата и время конца бронирования
    Long itemId; // ID вещь, которую пользователь бронирует
    UserResponseDto booker; // пользователь, который осуществляет бронирование
    BookingStatus status; // статус бронирования
}
