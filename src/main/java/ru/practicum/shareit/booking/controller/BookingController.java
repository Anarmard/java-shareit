package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemBookingResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    // добавление нового запроса на бронирование
    @PostMapping
    public BookingResponseDto saveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @Valid @RequestBody BookingCreateRequestDto bookingCreateRequestDto) {
        userService.getUserById(userId);
        ItemBookingResponseDto currentItem = itemService.getItemBooking(bookingCreateRequestDto.getItemId(), userId);
        Booking currentBooking = bookingMapper.toBooking(bookingCreateRequestDto, userId);
        currentBooking.setItem(itemMapper.toItem(currentItem, currentItem.getOwner().getId()));
        Booking returnedBooking = bookingService.saveBooking(currentBooking);
        return bookingMapper.toBookingDto(returnedBooking);
    }

    // Подтверждение или отклонение запроса на бронирование
    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam("approved") Boolean approved) {
        userService.getUserById(userId);
        Booking returnedBooking = bookingService.approveBooking(userId, bookingId, approved);
        return bookingMapper.toBookingDto(returnedBooking);
    }

    // Просмотр информации о конкретном бронировании по её идентификатору
    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long bookingId) {
        userService.getUserById(userId);
        Booking returnedBooking = bookingService.getBooking(userId, bookingId);
        return bookingMapper.toBookingDto(returnedBooking);
    }

    // Получение списка всех бронирований текущего пользователя
    @GetMapping
    public List<BookingResponseDto> getAllBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(value = "state",
                                                          required = false,
                                                          defaultValue = "ALL") String state) {
        userService.getUserById(userId);
        List<Booking> returnedListBooking;
        returnedListBooking = bookingService.getAllBooking(userId, state);
        return bookingMapper.toListBookingDto(returnedListBooking);
    }

    // Получение списка бронирований для всех вещей текущего пользователя
    @GetMapping("/owner")
    public List<BookingResponseDto> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(value = "state", required = false) String state) {
        userService.getUserById(userId);
        List<Booking> returnedListBooking;
        returnedListBooking = bookingService.getAllItemsByOwner(userId, state);
        return bookingMapper.toListBookingDto(returnedListBooking);
    }

}
