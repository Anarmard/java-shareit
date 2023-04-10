package ru.practicum.shareitserver.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitserver.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareitserver.booking.dto.BookingResponseDto;
import ru.practicum.shareitserver.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String USERID = "X-Sharer-User-Id";

    // добавление нового запроса на бронирование
    @PostMapping
    public BookingResponseDto saveBooking(@RequestHeader(USERID) Long userId,
                                          @Valid @RequestBody BookingCreateRequestDto bookingCreateRequestDto) {
        return bookingService.saveBooking(bookingCreateRequestDto, userId);
    }

    // Подтверждение или отклонение запроса на бронирование
    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader(USERID) Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam("approved") Boolean approved) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    // Просмотр информации о конкретном бронировании по её идентификатору
    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader(USERID) Long userId,
                                  @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    // Получение списка всех бронирований текущего пользователя
    @GetMapping
    public List<BookingResponseDto> getAllBooking(
            @RequestHeader(USERID) Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return bookingService.getAllBooking(userId, state, from, size);
    }

    // Получение списка бронирований для всех вещей текущего пользователя
    @GetMapping("/owner")
    public List<BookingResponseDto> getAllItemsByOwner(
            @RequestHeader(USERID) Long userId,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return bookingService.getAllItemsByOwner(userId, state, from, size);
    }

}
