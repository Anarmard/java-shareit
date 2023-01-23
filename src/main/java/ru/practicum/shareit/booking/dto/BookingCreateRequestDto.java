package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Valid
@Getter
@AllArgsConstructor // конструктор на все параметры
public class BookingCreateRequestDto {

    private Long id; // уникальный идентификатор бронирования

    @NotBlank
    private LocalDateTime start; // дата и время начала бронирования

    @NotBlank
    private LocalDateTime end; // дата и время конца бронирования

    @NotBlank
    private ItemResponseDto item; // вещь, которую пользователь бронирует

    @NotBlank
    private UserResponseDto booker; // пользователь, который осуществляет бронирование

    private BookingStatus status; // статус бронирования
}
