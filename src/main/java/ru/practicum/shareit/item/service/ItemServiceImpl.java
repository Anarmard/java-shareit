package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingResponseDateDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentReponseDto;
import ru.practicum.shareit.item.dto.ItemBookingResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId).
                orElseThrow(() -> new NotFoundException("Item is not found"));
    }

    @Override
    public ItemBookingResponseDto getItemBooking(Long itemId) {

        // нашли Item по Id
        Item itemFromDB = getItem(itemId);
        ItemBookingResponseDto itemBookingResponseDto = itemMapper.toItemBookingDto(itemFromDB);

        // нашли бронирования у данного item - последнее и будущее
        Booking lastBooking = bookingRepository.findFirstByItemAndStatusOrderByStartAsc(itemFromDB, BookingStatus.APPROVED);
        Booking nextBooking = bookingRepository.findFirstByItemAndStatusOrderByEndDesc(itemFromDB, BookingStatus.APPROVED);

        // убрали лишние данные (оставили только даты)
        BookingResponseDateDto lastBookingResponseDto = bookingMapper.toBookingDateDto(lastBooking);
        BookingResponseDateDto nextBookingResponseDto = bookingMapper.toBookingDateDto(nextBooking);

        // сохранили полученные скоращенные броинрования в Item
        itemBookingResponseDto.setLastBooking(lastBookingResponseDto);
        itemBookingResponseDto.setNextBooking(nextBookingResponseDto);

        // добавили также комментарии
        itemBookingResponseDto.setCommentList(commentMapper.toListCommentDto(commentRepository.findByItemId(itemId)));

        return itemBookingResponseDto;
    }

    @Override
    public List<ItemBookingResponseDto> getItemsBooking(Long userId) {
        User userFromDB = userService.getUserById(userId);
        List<Item> itemListFromDB = itemRepository.findItemsByOwner(userFromDB);
        List<ItemBookingResponseDto> itemBookingResponseDtoList = new ArrayList<>();
        for (Item itemFromDB : itemListFromDB) {
            itemBookingResponseDtoList.add(getItemBooking(itemFromDB.getId()));
        }
        return itemBookingResponseDtoList;
    }

    @Override
    public Item addNewItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        if (!Objects.equals(item.getOwner().getId(), getItem(itemId).getOwner().getId())) {
            throw new NotFoundException("user is not owner");
        }

        Item itemFromDB = getItem(itemId);

        if (Objects.nonNull(item.getName())) {
            itemFromDB.setName(item.getName());
        }
        if (Objects.nonNull(item.getDescription())) {
            itemFromDB.setDescription(item.getDescription());
        }
        if (Objects.nonNull(item.getAvailable())) {
            itemFromDB.setAvailable(item.getAvailable());
        }
        itemRepository.save(itemFromDB);
        return itemFromDB;
    }

    @Override
    public List<Item> getItemsBySearch(String text) {
        return itemRepository.searchItem(text);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        if (!Objects.equals(userId, getItem(itemId).getOwner().getId())) {
            throw new NotFoundException("user is not owner");
        }

        itemRepository.deleteById(itemId);
    }

    @Override
    public CommentReponseDto addComment(Comment comment) {

        if (bookingRepository.findPastByBooker(comment.getAuthor(), Timestamp.valueOf(LocalDateTime.now())).isEmpty()) {
            throw new NotFoundException("user is not booker or booking has not yet finished");
        } else {
            comment.setCreated(LocalDateTime.now());
            return commentMapper.toCommentDto(commentRepository.save(comment));
        }
    }
}
