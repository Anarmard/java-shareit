package ru.practicum.shareitserver.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareitserver.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareitserver.booking.dto.BookingResponseDateDto;
import ru.practicum.shareitserver.booking.dto.BookingResponseDto;
import ru.practicum.shareitserver.booking.mapper.BookingMapperImpl;
import ru.practicum.shareitserver.booking.model.Booking;
import ru.practicum.shareitserver.booking.model.BookingStatus;
import ru.practicum.shareitserver.item.dto.ItemResponseDto;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.user.dto.UserCreateRequestDto;
import ru.practicum.shareitserver.user.dto.UserResponseDto;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class BookingMapperImplTest {

    private BookingMapperImpl bookingMapper;
    private Item item;
    private ItemResponseDto itemResponseDto;
    private Booking booking;
    private BookingResponseDateDto bookingResponseDateDto;
    private BookingResponseDto bookingResponseDto;
    private BookingCreateRequestDto bookingCreateRequestDto;

    @BeforeEach
    void init() {
        bookingMapper = new BookingMapperImpl();

        User user1 = new User(1L, "John", "john.doe@mail.com");
        User user2 = new User(2L, "Bill", "bill.doe@mail.com");
        UserCreateRequestDto userCreateRequestDto1 = new UserCreateRequestDto(1L, "John", "john.doe@mail.com"); // owner
        UserResponseDto userResponseDto = new UserResponseDto(2L, "Bill", "bill.doe@mail.com");

        // ItemRequest
        ItemRequest itemRequest = new ItemRequest(1L, "need drill", user2,
                LocalDateTime.of(2023, 1, 28, 2, 0));

        // Booking
        booking = new Booking(1L,
                LocalDateTime.of(2023, 2, 28, 2,0),
                LocalDateTime.of(2023, 2, 28, 3,0),
                item,
                user2,
                BookingStatus.APPROVED);
        bookingResponseDateDto = new BookingResponseDateDto(1L,
                LocalDateTime.of(2023, 2, 28, 2,0),
                LocalDateTime.of(2023, 2, 28, 3,0),
                1L,
                2L,
                BookingStatus.APPROVED);
        bookingResponseDto = new BookingResponseDto(1L,
                LocalDateTime.of(2023, 2, 28, 2,0),
                LocalDateTime.of(2023, 2, 28, 3,0),
                itemResponseDto,
                userResponseDto,
                BookingStatus.APPROVED);
        bookingCreateRequestDto = new BookingCreateRequestDto(1L,
                LocalDateTime.of(2023, 2, 28, 2,0),
                LocalDateTime.of(2023, 2, 28, 3,0),
                1L,
                userResponseDto,
                BookingStatus.APPROVED);

        // Item
        item = new Item(1L,"drill","drill makita",true, user1, itemRequest);
        itemResponseDto = new ItemResponseDto(
                1L,"drill","drill makita",true, userCreateRequestDto1, 1L);
    }

    @Test
    void toBookingDtoTest() {
        Assertions.assertNull(bookingMapper.toBookingDto(null));
        BookingResponseDto bookingResponseDtoNew = bookingMapper.toBookingDto(booking);
        Assertions.assertEquals(bookingResponseDto.getId(), bookingResponseDtoNew.getId());
    }

    @Test
    void toBookingDateDtoTest() {
        Assertions.assertNull(bookingMapper.toBookingDateDto(null));
        BookingResponseDateDto bookingResponseDateDtoNew = bookingMapper.toBookingDateDto(booking);
        Assertions.assertEquals(bookingResponseDateDto.getId(), bookingResponseDateDtoNew.getId());
    }

    @Test
    void toListBookingDtoTest() {
        Assertions.assertNull(bookingMapper.toListBookingDto(null));
        List<BookingResponseDto> bookingResponseDtoList = bookingMapper.toListBookingDto(List.of(booking));
        Assertions.assertEquals(bookingResponseDto.getId(), bookingResponseDtoList.get(0).getId());
    }

    @Test
    void toBookingTest() {
        Assertions.assertNull(bookingMapper.toBooking(null, null));
        Booking bookingNew = bookingMapper.toBooking(bookingCreateRequestDto, 1L);
        Assertions.assertEquals(booking.getId(), bookingNew.getId());
    }
}
