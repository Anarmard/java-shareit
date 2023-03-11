package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    // добавление нового запроса на бронирование
    @PostMapping
    public BookingResponseDto saveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @Valid @RequestBody BookingCreateRequestDto bookingCreateRequestDto) {
        return bookingService.saveBooking(bookingCreateRequestDto, userId);
    }

    // Подтверждение или отклонение запроса на бронирование
    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam("approved") Boolean approved) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    // Просмотр информации о конкретном бронировании по её идентификатору
    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    // Получение списка всех бронирований текущего пользователя
    @GetMapping
    public List<BookingResponseDto> getAllBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(value = "state",
                                                          required = false,
                                                          defaultValue = "ALL") String state) {
        return bookingService.getAllBooking(userId, state);
    }

    // Получение списка бронирований для всех вещей текущего пользователя
    @GetMapping("/owner")
    public List<BookingResponseDto> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(value = "state", required = false) String state) {
        return bookingService.getAllItemsByOwner(userId, state);
    }

}
