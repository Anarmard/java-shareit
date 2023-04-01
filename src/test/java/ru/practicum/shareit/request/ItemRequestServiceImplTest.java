package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private ItemRequestServiceImpl itemRequestService;

    private Item item;
    private User user2; // booker
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemRequestForResponseDto itemRequestForResponseDto;
    private ItemForItemRequestDto itemForItemRequestDto;

    @BeforeEach
    void init() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemRequestMapper,
                itemRepository, itemMapper, userRepository);
        User user1 = new User(1L, "John", "john.doe@mail.com");
        user2 = new User(2L, "Bill", "bill.doe@mail.com");
        UserResponseDto userResponseDto2 = new UserResponseDto(2L, "Bill", "bill.doe@mail.com");

        // ItemRequest
        itemRequest = new ItemRequest(1L, "need drill", user2,
                LocalDateTime.of(2023, 1, 28, 2,0));
        itemRequestDto = new ItemRequestDto(1L, "need drill", userResponseDto2,
                LocalDateTime.of(2023, 1, 28, 2, 0));
        itemRequestForResponseDto = new ItemRequestForResponseDto(
                1L, "need drill", userResponseDto2,
                LocalDateTime.of(2023, 1, 28, 2, 0), null);

        // Item
        item = new Item(1L,"drill","drill makita",true, user1, itemRequest);
        itemForItemRequestDto = new ItemForItemRequestDto(1L,"drill","drill makita",true, 1L, 1L);
    }

    @Test
    void addNewItemRequestTest() {
        Mockito.when(userRepository.findById(4L))
                .thenReturn(Optional.empty());
        Exception e0 = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.addNewItemRequest(itemRequestDto, 4L));
        Assertions.assertEquals("User with such ID does not exist", e0.getMessage());

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));
        Mockito.when(itemRequestMapper.toItemRequest(itemRequestDto, 2L))
                .thenReturn(itemRequest);

        Mockito.when(itemRequestMapper.toItemRequestForResponseDto(itemRequest))
                .thenReturn(itemRequestForResponseDto);

        Assertions.assertEquals(itemRequestForResponseDto, itemRequestService.addNewItemRequest(
                itemRequestDto, 2L));

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .save(itemRequest);
    }

    @Test
    void getItemRequestsByOwnerTest() {
        Mockito.when(userRepository.findById(4L))
                .thenReturn(Optional.empty());
        Exception e0 = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestsByOwner(4L));
        Assertions.assertEquals("User with such ID does not exist", e0.getMessage());

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));

        Mockito.when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(2L))
                .thenReturn(List.of(itemRequest));

        Mockito.when(itemRepository.findAll())
                .thenReturn(List.of(item));
        Mockito.when(itemMapper.toItemForItemRequestDto(item))
                .thenReturn(itemForItemRequestDto);
        Mockito.when(itemRequestMapper.toItemRequestForResponseDto(itemRequest))
                .thenReturn(itemRequestForResponseDto);

        Assertions.assertEquals(List.of(itemRequestForResponseDto), itemRequestService.getItemRequestsByOwner(2L));
    }

    @Test
    void getAllItemRequestsTest() {
        Exception e0 = Assertions.assertThrows(ValidationException.class,
                () -> itemRequestService.getAllItemRequests(2L, 15, 0));
        Assertions.assertEquals("Page size must not be less than one", e0.getMessage());
        Exception e1 = Assertions.assertThrows(ValidationException.class,
                () -> itemRequestService.getAllItemRequests(2L,  -10, 2));
        Assertions.assertEquals("Index 'from' must not be less than zero", e1.getMessage());

        Mockito.when(itemRequestRepository.findAllByRequestorIdNot(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));

        Mockito.when(itemRepository.findAll())
                .thenReturn(List.of(item));
        Mockito.when(itemMapper.toItemForItemRequestDto(item))
                .thenReturn(itemForItemRequestDto);
        Mockito.when(itemRequestMapper.toItemRequestForResponseDto(itemRequest))
                .thenReturn(itemRequestForResponseDto);

        Assertions.assertEquals(List.of(itemRequestForResponseDto), itemRequestService.getAllItemRequests(2L, 0, 10));
    }

    @Test
    void getItemRequestTest() {
        Mockito.when(userRepository.findById(4L))
                .thenReturn(Optional.empty());
        Exception e0 = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequest(4L, 1L));
        Assertions.assertEquals("User with such ID does not exist", e0.getMessage());

        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.of(user2));

        Mockito.when(itemRequestRepository.findById(2L))
                .thenReturn(Optional.empty());
        Exception e1 = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequest(2L, 2L));
        Assertions.assertEquals("ItemRequest is not found", e1.getMessage());

        Mockito.when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.findAll())
                .thenReturn(List.of(item));
        Mockito.when(itemMapper.toItemForItemRequestDto(item))
                .thenReturn(itemForItemRequestDto);
        Mockito.when(itemRequestMapper.toItemRequestForResponseDto(itemRequest))
                .thenReturn(itemRequestForResponseDto);
        Assertions.assertEquals(itemRequestForResponseDto, itemRequestService.getItemRequest( 2L, 1L));
    }
}
