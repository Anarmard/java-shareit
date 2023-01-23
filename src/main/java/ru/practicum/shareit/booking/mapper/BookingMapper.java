package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

// добавили в pom зависимости с mapstruct (в 4-х местах) поэтому можем использовать данный функционал
@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface BookingMapper {

    BookingResponseDto toBookingDto(Booking booking);
    // mapstruct сам генерит необходимый код для преобразования Booking в BookingDto

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "booker", ignore = true)
    BookingResponseDateDto toBookingDateDto(Booking booking);

    List<BookingResponseDto> toListBookingDto(List<Booking> bookingList);

    @Mapping(target = "booker.id", source = "userId")
    Booking toBooking(BookingCreateRequestDto bookingCreateRequestDto, Long userId);

    @Named("convertToTimestamp")
    default Timestamp convertToTimestamp(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime);
    }

    @Named("convertToLocalDateTime")
    default LocalDateTime convertToLocalDateTime(Timestamp timestamp) {
        return timestamp.toLocalDateTime();
    }
}