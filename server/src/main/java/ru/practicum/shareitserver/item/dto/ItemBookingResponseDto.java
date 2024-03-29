package ru.practicum.shareitserver.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareitserver.booking.dto.BookingResponseDateDto;
import ru.practicum.shareitserver.request.dto.ItemRequestDto;
import ru.practicum.shareitserver.user.dto.UserCreateRequestDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor // конструктор на все параметры
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemBookingResponseDto {
    Long id;
    String name;
    String description;
    Boolean available;
    UserCreateRequestDto owner;
    ItemRequestDto request;
    BookingResponseDateDto lastBooking;
    BookingResponseDateDto nextBooking;
    List<CommentResponseDto> comments;
}
