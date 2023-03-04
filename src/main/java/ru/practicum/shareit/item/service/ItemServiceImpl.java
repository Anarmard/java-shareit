package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingResponseDateDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemBookingResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

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

    @Override
    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item is not found"));
    }

    @Override
    public ItemBookingResponseDto getItemBooking(Long itemId, Long userId) {

        // нашли Item по Id
        Item itemFromDB = getItem(itemId);
        ItemBookingResponseDto itemBookingResponseDto = itemMapper.toItemBookingDto(itemFromDB);

        // только владелец вещи может видеть последнее и будущее бронирование
        if (Objects.equals(itemFromDB.getOwner().getId(), userId)) {
            // нашли бронирования у данного item - последнее и будущее
            List<Booking> lastListBooking = bookingRepository.findLastBooking(itemFromDB, BookingStatus.APPROVED, LocalDateTime.now());
            List<Booking> nextListBooking = bookingRepository.findNextBooking(itemFromDB, BookingStatus.APPROVED, LocalDateTime.now());

            Booking lastBooking = null;
            if (!lastListBooking.isEmpty()) {
                lastBooking = lastListBooking.get(0);
            }

            Booking nextBooking = null;
            if (!nextListBooking.isEmpty()) {
                nextBooking = nextListBooking.get(0);
            }

            // убрали лишние данные (оставили только даты)
            BookingResponseDateDto lastBookingResponseDto = bookingMapper.toBookingDateDto(lastBooking);
            BookingResponseDateDto nextBookingResponseDto = bookingMapper.toBookingDateDto(nextBooking);

            // сохранили полученные скоращенные броинрования в Item
            itemBookingResponseDto.setLastBooking(lastBookingResponseDto);
            itemBookingResponseDto.setNextBooking(nextBookingResponseDto);
        }

        // добавили также комментарии
        itemBookingResponseDto.setComments(commentMapper.toListCommentDto(commentRepository.findByItemId(itemId)));

        return itemBookingResponseDto;
    }

    @Override
    public List<ItemBookingResponseDto> getItemsBooking(Long userId) {
        User userFromDB = userService.getUserById(userId);
        List<Item> itemListFromDB = itemRepository.findItemsByOwner(userFromDB);
        List<ItemBookingResponseDto> itemBookingResponseDtoList = new ArrayList<>();
        for (Item itemFromDB : itemListFromDB) {
            itemBookingResponseDtoList.add(getItemBooking(itemFromDB.getId(), userId));
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
    public CommentResponseDto addComment(Comment comment) {

        if (bookingRepository.findPastByBooker(comment.getAuthor(), LocalDateTime.now()).isEmpty()) {
            throw new ValidationException("user is not booker or booking has not yet finished");
        } else {
            comment.setCreated(LocalDateTime.now());
            commentRepository.save(comment);
            return commentMapper.toCommentDto(comment);
        }
    }
}
