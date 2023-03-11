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

        // 2. и выгрузим сразу все будущие бронирования (nextbooking), где user владелец
        List<Booking> nextListBooking =
                bookingRepository.findNextBookingForOwner(userFromDB, BookingStatus.APPROVED, LocalDateTime.now());

        // 3. все комментарии к Item, где user владелец
        List<Comment> allCommentsListForUser = commentRepository.findByAuthorId(userId);

        List<ItemBookingResponseDto> itemBookingResponseDtoList = new ArrayList<>();

        // теперь надо добавить к каждому item lastbooking, nextbooking, comment
        for (Item itemFromDB : itemListFromDB) {
            ItemBookingResponseDto itemBookingResponseDto = itemMapper.toItemBookingDto(itemFromDB);

            // 1. ищем lastbooking для текущего Item
            Booking lastBooking = null;
            for (Booking currentLastBooking : lastListBooking) {
                if (Objects.equals(currentLastBooking.getItem(), itemFromDB)) {
                    if (lastBooking == null) {
                        lastBooking = currentLastBooking;
                    }

                    // если StartDate нового букинга позже уже сохраненного, то перезаписываем lastbooking
                    if (currentLastBooking.getStart().isAfter(lastBooking.getStart())) {
                        lastBooking = currentLastBooking;
                    }
                }
            }

            // 2. ищем nextbooking для текущего Item
            Booking nextbooking = null;
            for (Booking currentNextBooking : nextListBooking) {
                if (Objects.equals(currentNextBooking.getItem(), itemFromDB)) {
                    if (nextbooking == null) {
                        nextbooking = currentNextBooking;
                    }

                    // если StartDate нового букинга раньше уже сохраненного, то перезаписываем nextbooking
                    if (currentNextBooking.getStart().isBefore(nextbooking.getStart())) {
                        nextbooking = currentNextBooking;
                    }
                }
            }

            // 3. ищем все комментарии к текущему Item
            List<Comment> commentListOfItem = new ArrayList<>();
            for (Comment c : allCommentsListForUser) {
                if (Objects.equals(c.getItem(), itemFromDB)) {
                    commentListOfItem.add(c);
                }
            }

            // переводим из Booking в DTO Booking - для текущего Item
            BookingResponseDateDto lastBookingResponseDto = bookingMapper.toBookingDateDto(lastBooking);
            BookingResponseDateDto nextBookingResponseDto = bookingMapper.toBookingDateDto(nextbooking);
            List<CommentResponseDto> commentResponseDtoList = commentMapper.toListCommentDto(commentListOfItem);

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
