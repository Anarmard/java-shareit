package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingResponseDateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor // конструктор на все параметры
public class ItemBookingResponseDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private UserCreateRequestDto owner;

    private ItemRequestDto request;

    private BookingResponseDateDto lastBooking;

    private BookingResponseDateDto nextBooking;

    private List<CommentResponseDto> comments;
}
