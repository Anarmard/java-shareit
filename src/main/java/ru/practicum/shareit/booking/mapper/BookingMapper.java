package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

// добавили в pom зависимости с mapstruct (в 4-х местах) поэтому можем использовать данный функционал
@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingResponseDto toBookingDto(Booking booking);
    // mapstruct сам генерит необходимый код для преобразования Booking в BookingDto

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "bookerId", source = "booker.id")
    BookingResponseDateDto toBookingDateDto(Booking booking);

    List<BookingResponseDto> toListBookingDto(List<Booking> bookingList);

    @Mapping(target = "booker.id", source = "userId")
    @Mapping(target = "item", ignore = true)
    Booking toBooking(BookingCreateRequestDto bookingCreateRequestDto, Long userId);

}