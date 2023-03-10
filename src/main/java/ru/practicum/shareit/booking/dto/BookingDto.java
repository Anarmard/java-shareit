package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.ItemStatus;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDto {
    private Long id; // уникальный идентификатор бронирования
    private LocalDateTime start; // дата и время начала бронирования
    private LocalDateTime end; // дата и время конца бронирования
    private ItemResponseDto item; // вещь, которую пользователь бронирует
    private UserResponseDto booker; // пользователь, который осуществляет бронирование
    private ItemStatus status; // статус бронирования
}
