package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemBookingResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private ItemServiceImpl itemService;

    private Item item;
    private ItemCreateRequestDto itemCreateRequestDto;
    private ItemResponseDto itemResponseDto;
    private User user1;
    private User user2;
    private UserCreateRequestDto userCreateRequestDto1;
    private UserResponseDto userResponseDto2;
    private ItemBookingResponseDto itemBookingResponseDto;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void init() {
        itemService = new ItemServiceImpl(itemRepository, bookingRepository, commentRepository,
        itemRequestRepository, userRepository, itemMapper, bookingMapper, commentMapper);

        // User
        user1 = new User(1L, "John", "john.doe@mail.com");
        user2 = new User(2L, "Bill", "bill.doe@mail.com");
        userCreateRequestDto1 = new UserCreateRequestDto(1L, "John", "john.doe@mail.com");
        userResponseDto2 = new UserResponseDto(2L, "Bill", "bill.doe@mail.com");

        // ItemRequest
        itemRequest = new ItemRequest(1L, "need drill", user2,
                LocalDateTime.of(2023, 3, 28, 2,0));
        itemRequestDto = new ItemRequestDto(1L, "need drill", userResponseDto2,
                LocalDateTime.of(2023, 3, 28, 2,0));

        // Item
        item = new Item(1L,"drill","drill makita",true, user1, itemRequest);
        itemCreateRequestDto = new ItemCreateRequestDto(
                1L,"drill","drill makita",true, userCreateRequestDto1, 1L);
        itemResponseDto = new ItemResponseDto(
                1L,"drill","drill makita",true, userCreateRequestDto1, 1L);
        itemBookingResponseDto = new ItemBookingResponseDto(
                1L,"drill","drill makita",true,
                userCreateRequestDto1, itemRequestDto, null, null, null);
    }

    @Test
    void getItemTest() {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Item itemFromDB = itemService.getItem(1L);
        Assertions.assertEquals(item, itemFromDB);

        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getItem(2L));
        Assertions.assertEquals("Item is not found", e.getMessage());
    }

    @Test
    void getItemBookingTest() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));

        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Mockito.when(itemMapper.toItemBookingDto(any()))
                .thenReturn(itemBookingResponseDto);

        Mockito.when(bookingRepository.findLastBookingOfItem(any(), any(), any()))
                .thenReturn(List.of());
        Mockito.when(bookingRepository.findNextBookingOfItem(any(), any(), any()))
                .thenReturn(List.of());
        Mockito.when(bookingMapper.toBookingDateDto(any()))
                .thenReturn(null);

        Mockito.when(commentRepository.findByItemId(anyLong()))
                .thenReturn(null);
        Mockito.when(commentMapper.toListCommentDto(any()))
                .thenReturn(null);

        Assertions.assertEquals(itemBookingResponseDto, itemService.getItemBooking(1L, 1L));

        Mockito.when(userRepository.findById(3L))
                .thenReturn(Optional.empty());
        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getItemBooking(1L, 3L));
        Assertions.assertEquals("User is not found", e.getMessage());
    }


    @Test
    void getItemsBookingTest() {
        Exception e0 = Assertions.assertThrows(ValidationException.class,
                () -> itemService.getItemsBooking(1L, 15, 0));
        Assertions.assertEquals("Page size must not be less than one", e0.getMessage());

        Exception e1 = Assertions.assertThrows(ValidationException.class,
                () -> itemService.getItemsBooking(1L, -10, 2));
        Assertions.assertEquals("Index 'from' must not be less than zero", e1.getMessage());

        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));

        Mockito.when(itemRepository.findItemsByOwnerId(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(item)));

        Mockito.when(bookingRepository.findLastBookingForOwner(any(), any(), any()))
                .thenReturn(List.of());
        Mockito.when(bookingRepository.findNextBookingForOwner(any(), any(), any()))
                .thenReturn(List.of());
        Mockito.when(commentRepository.findByAuthorId(anyLong()))
                .thenReturn(List.of());

        Mockito.when(itemMapper.toItemBookingDto(any()))
                .thenReturn(itemBookingResponseDto);
        Mockito.when(bookingMapper.toBookingDateDto(any()))
                .thenReturn(null);
        Mockito.when(commentMapper.toListCommentDto(any()))
                .thenReturn(List.of());

        Assertions.assertEquals(List.of(itemBookingResponseDto), itemService.getItemsBooking(1L, 0, 10));
    }

    @Test
    void addNewItemTest() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));

        Mockito.when(itemMapper.toItem(any(ItemCreateRequestDto.class), anyLong()))
                .thenReturn(item);

        Mockito.when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.of(itemRequest));

        Mockito.when(itemRepository.save(any()))
                .thenReturn(item);

        Mockito.when(itemMapper.toItemDto(any()))
                .thenReturn(itemResponseDto);

        Assertions.assertEquals(itemResponseDto, itemService.addNewItem(itemCreateRequestDto, 1L));

        Mockito.when(userRepository.findById(3L)).
                thenReturn(Optional.empty());
        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.addNewItem(itemCreateRequestDto, 3L));
        Assertions.assertEquals("User is not found", e.getMessage());
    }

    @Test
    void updateItemTest() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));

        Mockito.when(itemMapper.toItem(any(ItemResponseDto.class), anyLong()))
                .thenReturn(item);
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(any()))
                .thenReturn(item);
        Mockito.when(itemMapper.toItemDto(any()))
                .thenReturn(itemResponseDto);

        Assertions.assertEquals(itemResponseDto, itemService.updateItem(1L, itemResponseDto, 1L));

        Mockito.when(userRepository.findById(3L)).thenReturn(Optional.empty());
        Exception e0 = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, itemResponseDto, 3L));
        Assertions.assertEquals("User is not found", e0.getMessage());

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        Item item2 = new Item(2L,"hammer","hammer",true, user2, null);
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item2));
        Exception e1 = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, itemResponseDto, 2L));
        Assertions.assertEquals("user is not owner", e1.getMessage());
    }

    /*
    @Test
    void deleteItemTest() {

    }

    @Test
    void addCommentTest() {

    }


     */
}
