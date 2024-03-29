package ru.practicum.shareitserver.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareitserver.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareitserver.booking.dto.BookingResponseDto;
import ru.practicum.shareitserver.booking.mapper.BookingMapper;
import ru.practicum.shareitserver.booking.model.Booking;
import ru.practicum.shareitserver.booking.model.BookingStatus;
import ru.practicum.shareitserver.booking.model.BookingState;
import ru.practicum.shareitserver.booking.repository.BookingRepository;
import ru.practicum.shareitserver.exception.NotFoundException;
import ru.practicum.shareitserver.exception.ValidationException;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.item.repository.ItemRepository;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    // добавление нового запроса на бронирование
    @Override
    public BookingResponseDto saveBooking(BookingCreateRequestDto bookingCreateRequestDto, Long userId) {
        // start > end
        if (bookingCreateRequestDto.getStart().isAfter(bookingCreateRequestDto.getEnd())) {
            throw new ValidationException("saveBooking: start date is after end date");
        }

        // start = end
        if (bookingCreateRequestDto.getStart().isEqual(bookingCreateRequestDto.getEnd())) {
            throw new ValidationException("saveBooking: start date equal end date");
        }

        // проверка есть ли User с таким id
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("saveBooking: User is not found"));

        // перевод Booking из DTO в модель
        Booking currentBooking = bookingMapper.toBooking(bookingCreateRequestDto, userId);

        // перевод Item из DTO в модель + установка Item в текущий Booking
        currentBooking.setItem(itemRepository.findById(bookingCreateRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException(
                        "saveBooking: Item with ID " + bookingCreateRequestDto.getItemId() + " is not found")));

        // check Booking & Item
        validate(currentBooking, currentBooking.getItem());

        currentBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(currentBooking);
        return bookingMapper.toBookingDto(currentBooking);
    }

    // Подтверждение или отклонение запроса на бронирование
    @Override
    public BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        // проверка есть ли User с таким id
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("approveBooking: User is not found"));

        Booking bookingFromDB = bookingRepository.findById(bookingId)
                        .orElseThrow(() -> new NotFoundException("approveBooking: Booking is not found"));

        if ((bookingFromDB.getStatus() == BookingStatus.APPROVED) && (approved)) {
            throw new ValidationException("approveBooking: booking have already APPROVED");
        }

        if (!Objects.equals(bookingFromDB.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException("approveBooking: user is not owner (approved or reject)");
        }

        if (approved) {
            bookingFromDB.setStatus(BookingStatus.APPROVED);
        } else {
            bookingFromDB.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(bookingFromDB);

        return bookingMapper.toBookingDto(bookingFromDB);
    }

    // Просмотр информации о конкретном бронировании по её идентификатору
    @Override
    public BookingResponseDto getBooking(Long userId, Long bookingId) {
        // проверка есть ли User с таким id
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("getBooking: User is not found"));

        Booking bookingFromDB = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("getBooking: Booking is not found"));
        if ((!Objects.equals(bookingFromDB.getBooker().getId(), userId)) &&
                (!Objects.equals(bookingFromDB.getItem().getOwner().getId(), userId))) {
            throw new NotFoundException("getBooking: user is not owner or booker");
        }
        return bookingMapper.toBookingDto(bookingFromDB);
    }


    // Получение списка всех бронирований текущего пользователя
    @Override
    public List<BookingResponseDto> getAllBooking(Long userId, String state, Integer from, Integer size) {
        if (size < 1) throw new ValidationException("Page size must not be less than one");
        if (from < 0) throw new ValidationException("Index 'from' must not be less than zero");

        // сначала создаём описание сортировки по полю start
        Sort sortById = Sort.by(Sort.Direction.DESC, "start");
        // затем создаём описание "страницы" размером size элемента
        Pageable page = PageRequest.of(from / size, size, sortById);
        Page<Booking> result;
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("getAllBooking: User is not found"));
        LocalDateTime currentMoment = LocalDateTime.now();

        // переводим из String в ENUM
        BookingState stateBooking = BookingState.of(state);

        switch (stateBooking) {
            case ALL:
                result = bookingRepository.findAllByBookerOrderByStartDesc(booker, page);
                break;
            case CURRENT:
                result = bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartAsc(booker, currentMoment, currentMoment, page);
                break;
            case PAST:
                result = bookingRepository.findAllByBookerAndEndBeforeOrderByStartDesc(booker, currentMoment, page);
                break;
            case FUTURE:
                result = bookingRepository.findAllByBookerAndStartAfterOrderByStartDesc(booker, currentMoment, page);
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.REJECTED, page);
                break;
            case UNKNOWN:
                throw new ValidationException("Unknown state: " + state);
            default:
                return null;
        }
        return bookingMapper.toListBookingDto(result.getContent());
    }

    // Получение списка бронирований для всех вещей текущего пользователя
    @Override
    public List<BookingResponseDto> getAllItemsByOwner(Long userId, String state, Integer from, Integer size) {
        if (size < 1) throw new ValidationException("Page size must not be less than one");
        if (from < 0) throw new ValidationException("Index 'from' must not be less than zero");
        // сначала создаём описание сортировки по полю start
        Sort sortById = Sort.by(Sort.Direction.DESC, "start");
        // затем создаём описание "страницы" размером size элемента
        Pageable page = PageRequest.of(from / size, size, sortById);
        Page<Booking> result;
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("getAllItemsByOwner: User is not found"));
        LocalDateTime currentMoment = LocalDateTime.now();

        // переводим из String в ENUM
        BookingState stateBooking = Objects.isNull(state) ? BookingState.ALL : BookingState.of(state);

        switch (stateBooking) {
            case ALL:
                result = bookingRepository.findAllByItemOwnerOrderByStartDesc(owner, page);
                break;
            case CURRENT:
                result = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(owner, currentMoment, currentMoment, page);
                break;
            case PAST:
                result = bookingRepository.findAllByItemOwnerAndEndBeforeOrderByStartDesc(owner, currentMoment, page);
                break;
            case FUTURE:
                result = bookingRepository.findAllByItemOwnerAndStartAfterOrderByStartDesc(owner, currentMoment, page);
                break;
            case WAITING:
                result = bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(owner,BookingStatus.WAITING, page);
                break;
            case REJECTED:
                result = bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(owner, BookingStatus.REJECTED, page);
                break;
            case UNKNOWN:
                throw new ValidationException("Unknown state: " + state);
            default:
                return null;
        }
        return bookingMapper.toListBookingDto(result.getContent());
    }

    private void validate(Booking booking, Item item) {
        if (!item.getAvailable()) {
            throw new ValidationException("validate: item is not available");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("validate: start date before now");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("validate: end date before now");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("validate: end date before start date");
        }
        if (Objects.equals(booking.getBooker().getId(), item.getOwner().getId())) {
            throw new NotFoundException("validate: owner could not be booker");
        }
    }

}
