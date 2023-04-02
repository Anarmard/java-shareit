package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private BookingServiceImpl bookingService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingMapper bookingMapper;

    private Item item;
    private ItemResponseDto itemResponseDto;
    private User user1; //owner
    private User user2; // booker
    private User user3; // no owner, no booker
    private Booking booking;
    private BookingCreateRequestDto bookingCreateRequestDto;
    private BookingResponseDto bookingResponseDto;

    @BeforeEach
    void init() {
        bookingService = new BookingServiceImpl(bookingRepository, bookingMapper, userRepository, itemRepository);

        // User
        user1 = new User(1L, "John", "john.doe@mail.com");
        user2 = new User(2L, "Bill", "bill.doe@mail.com");
        user3 = new User(3L, "Mike", "mike.doe@mail.com");
        UserCreateRequestDto userCreateRequestDto1 =
                new UserCreateRequestDto(1L, "John", "john.doe@mail.com"); // owner
        UserResponseDto userResponseDto2 = new UserResponseDto(2L, "Bill", "bill.doe@mail.com");

        // ItemRequest
        ItemRequest itemRequest = new ItemRequest(1L, "need drill", user2,
                LocalDateTime.of(2023, 1, 28, 2, 0));

        // Booking
        booking = new Booking(1L,
                LocalDateTime.of(2023, 4, 28, 2,0),
                LocalDateTime.of(2023, 4, 28, 3,0),
                item,
                user2,
                BookingStatus.WAITING);
        bookingCreateRequestDto = new BookingCreateRequestDto(1L,
                LocalDateTime.of(2023, 4, 28, 2,0),
                LocalDateTime.of(2023, 4, 28, 3,0),
                1L,
                userResponseDto2,
                BookingStatus.WAITING);
        bookingResponseDto = new BookingResponseDto(1L,
                LocalDateTime.of(2023, 4, 28, 2,0),
                LocalDateTime.of(2023, 4, 28, 3,0),
                itemResponseDto,
                userResponseDto2,
                BookingStatus.WAITING);

        // Item
        item = new Item(1L,"drill","drill makita",true, user1, itemRequest);
        itemResponseDto = new ItemResponseDto(
                1L,"drill","drill makita",true, userCreateRequestDto1, 1L);
    }

    @Test
    void saveBookingTest() {
        bookingCreateRequestDto.setStart(bookingCreateRequestDto.getEnd().plusDays(1L));
        Exception e1 = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.saveBooking(bookingCreateRequestDto, 2L));
        Assertions.assertEquals("saveBooking: start date is after end date", e1.getMessage());

        bookingCreateRequestDto.setStart(bookingCreateRequestDto.getEnd());
        Exception e2 = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.saveBooking(bookingCreateRequestDto, 2L));
        Assertions.assertEquals("saveBooking: start date equal end date", e2.getMessage());

        bookingCreateRequestDto.setStart(LocalDateTime.of(2023, 4, 28, 2,0));

        Mockito.when(userRepository.findById(4L))
                .thenReturn(Optional.empty());
        Exception e3 = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.saveBooking(bookingCreateRequestDto, 4L));
        Assertions.assertEquals("saveBooking: User is not found", e3.getMessage());

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));

        Mockito.when(bookingMapper.toBooking(any(), any()))
                .thenReturn(booking);

        Mockito.when(itemRepository.findById(any()))
                .thenReturn(Optional.empty());

        Exception e4 = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.saveBooking(bookingCreateRequestDto, 2L));
        Assertions.assertEquals("saveBooking: Item with ID " + bookingCreateRequestDto.getItemId() + " is not found",
                e4.getMessage());

        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        booking.setItem(item);

        item.setAvailable(false);
        Exception e5 = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.saveBooking(bookingCreateRequestDto, 2L));
        Assertions.assertEquals("validate: item is not available", e5.getMessage());
        item.setAvailable(true);

        booking.setStart(LocalDateTime.of(2000, 4, 28, 2,0));
        Exception e6 = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.saveBooking(bookingCreateRequestDto, 2L));
        Assertions.assertEquals("validate: start date before now", e6.getMessage());
        booking.setStart(LocalDateTime.of(2023, 4, 28, 2,0));

        booking.setEnd(LocalDateTime.of(2000, 4, 28, 3,0));
        Exception e7 = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.saveBooking(bookingCreateRequestDto, 2L));
        Assertions.assertEquals("validate: end date before now", e7.getMessage());
        booking.setEnd(LocalDateTime.of(2023, 4, 28, 3,0));

        booking.setEnd(booking.getStart().minusDays(1L));
        Exception e8 = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.saveBooking(bookingCreateRequestDto, 2L));
        Assertions.assertEquals("validate: end date before start date", e8.getMessage());
        booking.setEnd(LocalDateTime.of(2023, 4, 28, 3,0));

        Mockito.when(bookingMapper.toBookingDto(booking))
                        .thenReturn(bookingResponseDto);

        Assertions.assertEquals(bookingResponseDto, bookingService.saveBooking(bookingCreateRequestDto, 2L));

        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(booking);
    }

    @Test
    void approveBookingTest() {
        Mockito.when(userRepository.findById(4L))
                .thenReturn(Optional.empty());
        Exception e0 = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(4L, 1L,true));
        Assertions.assertEquals("approveBooking: User is not found", e0.getMessage());

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findById(2L))
                .thenReturn(Optional.empty());
        Exception e1 = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(1L, 2L,true));
        Assertions.assertEquals("approveBooking: Booking is not found", e1.getMessage());

        Mockito.when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        booking.setStatus(BookingStatus.APPROVED);
        Exception e2 = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(1L, 1L,true));
        Assertions.assertEquals("approveBooking: booking have already APPROVED", e2.getMessage());
        booking.setStatus(BookingStatus.WAITING);

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        booking.setItem(item);
        Exception e3 = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(2L, 1L,true));
        Assertions.assertEquals("approveBooking: user is not owner (approved or reject)", e3.getMessage());

        Mockito.when(bookingMapper.toBookingDto(booking))
                .thenReturn(bookingResponseDto);
        Assertions.assertEquals(bookingResponseDto, bookingService.approveBooking(1L, 1L, true));
        Mockito.verify(bookingRepository, Mockito.times(1))
                .save(booking);
    }

    @Test
    void getBookingTest() {
        Mockito.when(userRepository.findById(4L))
                .thenReturn(Optional.empty());
        Exception e0 = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(4L, 1L));
        Assertions.assertEquals("getBooking: User is not found", e0.getMessage());

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findById(2L))
                .thenReturn(Optional.empty());
        Exception e1 = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(1L, 2L));
        Assertions.assertEquals("getBooking: Booking is not found", e1.getMessage());

        Mockito.when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        booking.setItem(item);
        Mockito.when(userRepository.findById(3L))
                .thenReturn(Optional.of(user3));
        Exception e2 = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(3L, 1L));
        Assertions.assertEquals("getBooking: user is not owner or booker", e2.getMessage());

        Mockito.when(bookingMapper.toBookingDto(booking))
                .thenReturn(bookingResponseDto);
        Assertions.assertEquals(bookingResponseDto, bookingService.getBooking(1L, 1L));
    }

    @Test
    void getAllBookingTest() {
        Exception e0 = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.getAllBooking(1L, "ALL",15, 0));
        Assertions.assertEquals("Page size must not be less than one", e0.getMessage());
        Exception e1 = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.getAllBooking(1L, "ALL", -10, 2));
        Assertions.assertEquals("Index 'from' must not be less than zero", e1.getMessage());

        Mockito.when(userRepository.findById(4L))
                .thenReturn(Optional.empty());
        Exception e2 = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getAllBooking(4L, "ALL", 0, 10));
        Assertions.assertEquals("getAllBooking: User is not found", e2.getMessage());

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));

        Mockito.when(bookingRepository.findAllByBookerOrderByStartDesc(any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartAsc(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(bookingRepository.findAllByBookerAndEndBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(bookingRepository.findAllByBookerAndStartAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(bookingRepository.findAllByBookerAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        Mockito.when(bookingMapper.toListBookingDto(List.of(booking)))
                .thenReturn(List.of(bookingResponseDto));
        Assertions.assertEquals(List.of(bookingResponseDto), bookingService.getAllBooking(2L, "ALL", 0, 10));
        Assertions.assertEquals(List.of(bookingResponseDto), bookingService.getAllBooking(2L, "CURRENT", 0, 10));
        Assertions.assertEquals(List.of(bookingResponseDto), bookingService.getAllBooking(2L, "PAST", 0, 10));
        Assertions.assertEquals(List.of(bookingResponseDto), bookingService.getAllBooking(2L, "FUTURE", 0, 10));
        Assertions.assertEquals(List.of(bookingResponseDto), bookingService.getAllBooking(2L, "WAITING", 0, 10));
        Assertions.assertEquals(List.of(bookingResponseDto), bookingService.getAllBooking(2L, "REJECTED", 0, 10));

        Exception e3 = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.getAllBooking(2L, "UNKNOWN", 0, 10));
        Assertions.assertEquals("Unknown state: UNKNOWN", e3.getMessage());
    }

    @Test
    void getAllItemsByOwnerTest() {
        Exception e0 = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.getAllItemsByOwner(1L, "ALL",15, 0));
        Assertions.assertEquals("Page size must not be less than one", e0.getMessage());
        Exception e1 = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.getAllItemsByOwner(1L, "ALL", -10, 2));
        Assertions.assertEquals("Index 'from' must not be less than zero", e1.getMessage());

        Mockito.when(userRepository.findById(4L))
                .thenReturn(Optional.empty());
        Exception e2 = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getAllItemsByOwner(4L, "ALL", 0, 10));
        Assertions.assertEquals("getAllItemsByOwner: User is not found", e2.getMessage());

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));

        Mockito.when(bookingRepository.findAllByItemOwnerOrderByStartDesc(any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(bookingRepository.findAllByItemOwnerAndEndBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(bookingRepository.findAllByItemOwnerAndStartAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));
        Mockito.when(bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        Mockito.when(bookingMapper.toListBookingDto(List.of(booking)))
                .thenReturn(List.of(bookingResponseDto));
        Assertions.assertEquals(List.of(bookingResponseDto), bookingService.getAllItemsByOwner(1L, "ALL", 0, 10));
        Assertions.assertEquals(List.of(bookingResponseDto), bookingService.getAllItemsByOwner(1L, "CURRENT", 0, 10));
        Assertions.assertEquals(List.of(bookingResponseDto), bookingService.getAllItemsByOwner(1L, "PAST", 0, 10));
        Assertions.assertEquals(List.of(bookingResponseDto), bookingService.getAllItemsByOwner(1L, "FUTURE", 0, 10));
        Assertions.assertEquals(List.of(bookingResponseDto), bookingService.getAllItemsByOwner(1L, "WAITING", 0, 10));
        Assertions.assertEquals(List.of(bookingResponseDto), bookingService.getAllItemsByOwner(1L, "REJECTED", 0, 10));

        Exception e3 = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.getAllItemsByOwner(1L, "UNKNOWN", 0, 10));
        Assertions.assertEquals("Unknown state: UNKNOWN", e3.getMessage());
    }
}
