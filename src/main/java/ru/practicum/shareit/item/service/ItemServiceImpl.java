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
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

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
    private final UserMapper userMapper;

    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item is not found"));
    }

    @Override
    public ItemBookingResponseDto getItemBooking(Long itemId, Long userId) {
        // проверка есть ли User с таким id
        userService.getUserDtoById(userId);

        // нашли Item по Id
        Item itemFromDB = getItem(itemId);
        ItemBookingResponseDto itemBookingResponseDto = itemMapper.toItemBookingDto(itemFromDB);

        // только владелец вещи может видеть последнее и будущее бронирование
        if (Objects.equals(itemFromDB.getOwner().getId(), userId)) {
            // нашли бронирования у данного item - последнее и будущее
            List<Booking> lastListBooking =
                    bookingRepository.findLastBookingOfItem(itemFromDB, BookingStatus.APPROVED, LocalDateTime.now());
            List<Booking> nextListBooking =
                    bookingRepository.findNextBookingOfItem(itemFromDB, BookingStatus.APPROVED, LocalDateTime.now());

            Booking lastBooking = null;
            if (!lastListBooking.isEmpty()) {
                lastBooking = lastListBooking.get(0);
            }

            Booking nextBooking = null;
            if (!nextListBooking.isEmpty()) {
                nextBooking = nextListBooking.get(0);
            }

            // переводим из Booking в DTO Booking
            BookingResponseDateDto lastBookingResponseDto = bookingMapper.toBookingDateDto(lastBooking);
            BookingResponseDateDto nextBookingResponseDto = bookingMapper.toBookingDateDto(nextBooking);

            // сохранили полученные DTO бронирования в Item
            itemBookingResponseDto.setLastBooking(lastBookingResponseDto);
            itemBookingResponseDto.setNextBooking(nextBookingResponseDto);
        }

        // добавили также комментарии
        itemBookingResponseDto.setComments(commentMapper.toListCommentDto(commentRepository.findByItemId(itemId)));

        return itemBookingResponseDto;
    }

    @Override
    public List<ItemBookingResponseDto> getItemsBooking(Long userId) {
        // выгрузили из БД User по его ID
        User userFromDB = userMapper.toUser(userService.getUserDtoById(userId), userId);

        // выгрузили из БД все Item, где владельцем является наш User из БД
        List<Item> itemListFromDB = itemRepository.findItemsByOwner(userFromDB);

        // теперь надо добавить к каждому item lastbooking, nextbooking, comment
        // 1. для начала выгрузим сразу все прошедшие бронирования (lastbooking), где user владелец
        List<Booking> lastListBooking =
                bookingRepository.findLastBookingForOwner(userFromDB, BookingStatus.APPROVED, LocalDateTime.now());

        // перекладываем в map, где ключ Item, сохраняем только lastbooking
        final Map<Item, Booking> lastMapBooking = new HashMap<>();
        for (Booking b : lastListBooking) {
            if (lastMapBooking.containsKey(b.getItem())) {
                Booking bFromMap = lastMapBooking.get(b.getItem());
                if (b.getStart().isAfter(bFromMap.getStart())) {
                    lastMapBooking.put(b.getItem(), b);
                }
            } else {
                // такого item в map еще нет, записываем без сравнений
                lastMapBooking.put(b.getItem(), b);
            }
        }

        // 2. и выгрузим сразу все будущие бронирования (nextbooking), где user владелец
        List<Booking> nextListBooking =
                bookingRepository.findNextBookingForOwner(userFromDB, BookingStatus.APPROVED, LocalDateTime.now());

        // перекладываем в map, где ключ Item, сохраняем только nextbooking
        final Map<Item, Booking> nextMapBooking = new HashMap<>();
        for (Booking b : nextListBooking) {
            if (nextMapBooking.containsKey(b.getItem())) {
                Booking bFromMap = nextMapBooking.get(b.getItem());
                if (b.getStart().isBefore(bFromMap.getStart())) {
                    nextMapBooking.put(b.getItem(), b);
                }
            } else {
                // такого item в map еще нет, записываем без сравнений
                nextMapBooking.put(b.getItem(), b);
            }
        }

        // 3. все комментарии к Item, где user владелец
        List<Comment> allCommentsListForUser = commentRepository.findByAuthorId(userId);

        // перекладываем все комментарии в map, где ключ Item
        final Map<Item, List<Comment>> commentMap = new HashMap<>();
        for (Comment c : allCommentsListForUser) {
            if (commentMap.containsKey(c.getItem())) {
                // такой item уже есть, значит добавляем комментарий к существующему списку
                commentMap.get(c.getItem()).add(c);
            } else {
                List<Comment> commentList = new ArrayList<>();
                commentList.add(c);
                commentMap.put(c.getItem(), commentList);
            }
        }

        List<ItemBookingResponseDto> itemBookingResponseDtoList = new ArrayList<>();

        // теперь надо перевести из Item в DTO
        for (Item itemFromDB : itemListFromDB) {
            ItemBookingResponseDto itemBookingResponseDto = itemMapper.toItemBookingDto(itemFromDB);

            // переводим из Booking в DTO Booking - для текущего Item
            BookingResponseDateDto lastBookingResponseDto =
                    bookingMapper.toBookingDateDto(lastMapBooking.get(itemFromDB));
            BookingResponseDateDto nextBookingResponseDto =
                    bookingMapper.toBookingDateDto(nextMapBooking.get(itemFromDB));
            List<CommentResponseDto> commentResponseDtoList =
                    commentMapper.toListCommentDto(commentMap.get(itemFromDB));

            // сохранили полученные DTO бронирования в Item
            itemBookingResponseDto.setLastBooking(lastBookingResponseDto);
            itemBookingResponseDto.setNextBooking(nextBookingResponseDto);
            itemBookingResponseDto.setComments(commentResponseDtoList);

            // получившийся itemBookingResponseDto записываем в массив ответа itemBookingResponseDtoList
            itemBookingResponseDtoList.add(itemBookingResponseDto);
        }
        return itemBookingResponseDtoList;
    }

    @Override
    public ItemResponseDto addNewItem(ItemCreateRequestDto itemCreateRequestDto, Long userId) {
        // проверка есть ли User с таким id
        userService.getUserDtoById(userId);

        Item currentItem = itemMapper.toItem(itemCreateRequestDto, userId);

        return itemMapper.toItemDto(itemRepository.save(currentItem));
    }

    @Override
    public ItemResponseDto updateItem(Long itemId, ItemUpdateDto updateItemDto, Long userId) {
        // проверка есть ли User с таким id
        userService.getUserDtoById(userId);

        Item item = itemMapper.toItem(updateItemDto, userId);

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
        return itemMapper.toItemDto(itemFromDB);
    }

    @Override
    public List<ItemResponseDto> getItemsBySearch(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemMapper.toListItemDto(itemRepository.searchItem(text));
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        // проверка есть ли User с таким id
        userService.getUserDtoById(userId);

        if (!Objects.equals(userId, getItem(itemId).getOwner().getId())) {
            throw new NotFoundException("user is not owner");
        }

        itemRepository.deleteById(itemId);
    }

    @Override
    public CommentResponseDto addComment(Long userId, CommentCreateRequestDto commentCreateRequestDto, Long itemId) {
        Comment comment = commentMapper.toComment(commentCreateRequestDto);
        comment.setItem(getItem(itemId));
        comment.setAuthor(userMapper.toUser(userService.getUserDtoById(userId), userId));

        if (bookingRepository
                .findBookingByBookerAndByItem(comment.getAuthor(), comment.getItem(), LocalDateTime.now()).isEmpty()) {
            throw new ValidationException("user is not booker or booking has not yet finished");
        } else {
            comment.setCreated(LocalDateTime.now());
            commentRepository.save(comment);
            return commentMapper.toCommentDto(comment);
        }
    }
}
