package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor // конструктор на все параметры
public class BookingResponseDto {
    private Long id; // уникальный идентификатор бронирования
    private LocalDateTime start; // дата и время начала бронирования
    private LocalDateTime end; // дата и время конца бронирования
    private ItemResponseDto item; // вещь, которую пользователь бронирует
    private UserResponseDto booker; // пользователь, который осуществляет бронирование
    private BookingStatus status; // статус бронирования
}
