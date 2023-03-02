package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.dto.UserResponseDto;

import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Valid
@Getter
@AllArgsConstructor // конструктор на все параметры
public class BookingCreateRequestDto {

    private Long id; // уникальный идентификатор бронирования

    @FutureOrPresent
    private LocalDateTime start; // дата и время начала бронирования

    @Future
    private LocalDateTime end; // дата и время конца бронирования

    private Long itemId; // ID вещь, которую пользователь бронирует

    private UserResponseDto booker; // пользователь, который осуществляет бронирование

    private BookingStatus status; // статус бронирования
}
