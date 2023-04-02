package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.dto.UserResponseDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor // конструктор на все параметры
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingCreateRequestDto {

    Long id; // уникальный идентификатор бронирования

    @FutureOrPresent
    @NotNull
    LocalDateTime start; // дата и время начала бронирования

    @Future
    @NotNull
    LocalDateTime end; // дата и время конца бронирования

    Long itemId; // ID вещь, которую пользователь бронирует

    UserResponseDto booker; // пользователь, который осуществляет бронирование

    BookingStatus status; // статус бронирования
}
