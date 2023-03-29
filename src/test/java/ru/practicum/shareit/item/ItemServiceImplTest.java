package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ItemMapper itemMapper;
    private BookingMapper bookingMapper;
    private CommentMapper commentMapper;
    private Item item;
    private ItemServiceImpl itemService;

    @BeforeEach
    void init() {
        itemService = new ItemServiceImpl(itemRepository, bookingRepository, commentRepository,
        itemRequestRepository, userRepository, itemMapper, bookingMapper, commentMapper);
        User user1 = new User(1L, "John", "john.doe@mail.com");
        User user2 = new User(2L, "Bill", "bill.doe@mail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "need drill", user2,
                LocalDateTime.of(2023, 3, 28, 2,0));
        item = new Item(1L,"drill","drill makita",true, user1, itemRequest);
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
                ()-> itemService.getItem(2L));
        Assertions.assertEquals("Item is not found", e.getMessage());
    }

    @Test
    void getItemBookingTest() {

    }

    @Test
    void getItemsBookingTest() {

    }

    @Test
    void addNewItemTest() {

    }

    @Test
    void updateItemTest() {

    }

    @Test
    void deleteItemTest() {

    }

    @Test
    void addCommentTest() {

    }

}
