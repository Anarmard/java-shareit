package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    // добавление нового запроса на бронирование
    @Override
    public Booking saveBooking(Booking booking) {
        validate(booking, booking.getItem());
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        return booking;
    }

    // Подтверждение или отклонение запроса на бронирование
    @Override
    public Booking approveBooking(Long userId, Long bookingId, Boolean approved) {
        Booking bookingFromDB = bookingRepository.findById(bookingId)
                        .orElseThrow(() -> new NotFoundException("Booking is not found"));

        if ((bookingFromDB.getStatus() == BookingStatus.APPROVED) && (approved)) {
            throw new ValidationException("booking have already APPROVED");
        }

        if (!Objects.equals(bookingFromDB.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException("user is not owner (approved or reject)");
        }

        if (approved) {
            bookingFromDB.setStatus(BookingStatus.APPROVED);
        } else {
            bookingFromDB.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(bookingFromDB);

        return bookingFromDB;
    }

    // Просмотр информации о конкретном бронировании по её идентификатору
    @Override
    public Booking getBooking(Long userId, Long bookingId) {
        Booking bookingFromDB = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking is not found"));
        if ((!Objects.equals(bookingFromDB.getBooker().getId(), userId)) &&
                (!Objects.equals(bookingFromDB.getItem().getOwner().getId(), userId))) {
            throw new NotFoundException("user is not owner or booker");
        }
        return bookingFromDB;
    }


    // Получение списка всех бронирований текущего пользователя
    @Override
    public List<Booking> getAllBooking(Long userId, String state) {
        List<Booking> result;
        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Booking is not found"));
        LocalDateTime currentMoment = LocalDateTime.now();

        // переводим из String в ENUM
        BookingState stateBooking = Objects.isNull(state) ? BookingState.ALL : BookingState.of(state);

        switch (stateBooking) {
            case ALL:
                result = bookingRepository.findAllByBookerOrderByStartDesc(booker);
                break;
            case CURRENT:
                result = bookingRepository.findCurrentByBooker(booker, currentMoment);
                break;
            case PAST:
                result = bookingRepository.findPastByBooker(booker, currentMoment);
                break;
            case FUTURE:
                result = bookingRepository.findFutureByBooker(booker, currentMoment);
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker,BookingStatus.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.REJECTED);
                break;
            case UNKNOWN:
                throw new ValidationException("Unknown state: " + state);
            default:
                result = null;
                break;
        }
        return result;
    }

    // Получение списка бронирований для всех вещей текущего пользователя
    @Override
    public List<Booking> getAllItemsByOwner(Long userId, String state) {
        List<Booking> result;
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User have not items"));
        LocalDateTime currentMoment = LocalDateTime.now();

        // переводим из String в ENUM
        BookingState stateBooking = Objects.isNull(state) ? BookingState.ALL : BookingState.of(state);

        switch (stateBooking) {
            case ALL:
                result = bookingRepository.findAllByOwnerItems(owner);
                break;
            case CURRENT:
                result = bookingRepository.findCurrentByOwnerItems(owner, currentMoment);
                break;
            case PAST:
                result = bookingRepository.findPastByOwnerItems(owner, currentMoment);
                break;
            case FUTURE:
                result = bookingRepository.findFutureByOwnerItems(owner, currentMoment);
                break;
            case WAITING:
                result = bookingRepository.findAllByOwnerAndStatus(owner,BookingStatus.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByOwnerAndStatus(owner, BookingStatus.REJECTED);
                break;
            case UNKNOWN:
                throw new ValidationException("Unknown state: " + state);
            default:
                result = null;
                break;
        }
        return result;
    }

    private void validate(Booking booking, Item item) {
        if (!item.getAvailable()) {
            throw new ValidationException("item is not available");
        }
        LocalDateTime now = LocalDateTime.now();
        if (booking.getStart().isBefore(now)) {
            throw new ValidationException("start date before now");
        }
        if (booking.getEnd().isBefore(now)) {
            throw new ValidationException("end date before now");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("end date before start date");
        }
        if (Objects.equals(booking.getBooker().getId(), item.getOwner().getId())) {
            throw new NotFoundException("owner could not be booker");
        }
    }

}
