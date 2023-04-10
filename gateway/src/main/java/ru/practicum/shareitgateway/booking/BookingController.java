package ru.practicum.shareitgateway.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.booking.dto.BookItemRequestDto;
import ru.practicum.shareitgateway.booking.dto.BookingState;
import ru.practicum.shareitgateway.exception.ValidationException;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    private static final String USERID = "X-Sharer-User-Id";

    // добавление нового запроса на бронирование
    @PostMapping
    public ResponseEntity<Object> saveBooking(@RequestHeader(USERID) Long userId,
                                          @Valid @RequestBody BookItemRequestDto bookItemRequestDto) {
        checkBookingDates(bookItemRequestDto);
        log.info("Creating booking {}, userId={}", bookItemRequestDto, userId);
        return bookingClient.save(userId, bookItemRequestDto);
    }

    // Подтверждение или отклонение запроса на бронирование
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(USERID) Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam("approved") Boolean approved) {
        return bookingClient.approve(userId, bookingId, approved);
    }

    // Просмотр информации о конкретном бронировании по её идентификатору
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USERID) Long userId,
                                         @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getById(userId, bookingId);
    }

    // Получение списка всех бронирований текущего пользователя
    @GetMapping
    public ResponseEntity<Object> getAllBooking(
            @RequestHeader(USERID) Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String stateParam,
            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        checkPageParams(from ,size);
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAll(userId, state, from, size);
    }

    // Получение списка бронирований для всех вещей текущего пользователя
    @GetMapping("/owner")
    public ResponseEntity<Object> getAllItemsByOwner(
            @RequestHeader(USERID) Long userId,
            @RequestParam(value = "state", defaultValue = "ALL") String stateParam,
            @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        checkPageParams(from ,size);
        log.info("Get booking by owner with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAllByOwner(userId, state, from, size);
    }

    private void checkPageParams(Integer from, Integer size) {
        if (size < 1) throw new ValidationException("Page size must not be less than one");
        if (from < 0) throw new ValidationException("Index 'from' must not be less than zero");
    }

    private void checkBookingDates(BookItemRequestDto bookItemRequestDto) {
        if (bookItemRequestDto.getStart() == null || bookItemRequestDto.getEnd() == null) {
            throw new ValidationException("saveBooking: start or end date is empty");
        }

        // start > end
        if (bookItemRequestDto.getStart().isAfter(bookItemRequestDto.getEnd())) {
            throw new ValidationException("saveBooking: start date is after end date");
        }

        // start = end
        if (bookItemRequestDto.getStart().isEqual(bookItemRequestDto.getEnd())) {
            throw new ValidationException("saveBooking: start date equal end date");
        }
    }

}