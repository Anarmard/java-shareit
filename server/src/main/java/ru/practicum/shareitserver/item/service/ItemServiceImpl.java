package ru.practicum.shareitserver.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareitserver.booking.dto.BookingResponseDateDto;
import ru.practicum.shareitserver.booking.mapper.BookingMapper;
import ru.practicum.shareitserver.booking.model.Booking;
import ru.practicum.shareitserver.booking.model.BookingStatus;
import ru.practicum.shareitserver.booking.repository.BookingRepository;
import ru.practicum.shareitserver.exception.NotFoundException;
import ru.practicum.shareitserver.exception.ValidationException;
import ru.practicum.shareitserver.item.dto.*;
import ru.practicum.shareitserver.item.mapper.CommentMapper;
import ru.practicum.shareitserver.item.mapper.ItemMapper;
import ru.practicum.shareitserver.item.model.Comment;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.item.repository.CommentRepository;
import ru.practicum.shareitserver.item.repository.ItemRepository;
import ru.practicum.shareitserver.request.repository.ItemRequestRepository;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
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
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User is not found")); // проверка есть ли User с таким id
        Item itemFromDB = getItem(itemId); // нашли Item по Id
        ItemBookingResponseDto itemBookingResponseDto = itemMapper.toItemBookingDto(itemFromDB);

        if (Objects.equals(itemFromDB.getOwner().getId(), userId)) { // только владелец вещи может видеть последнее и будущее бронирование
            List<Booking> lastListBooking = // нашли бронирования у данного item - последнее и будущее
                    bookingRepository.findAllByItemAndStatusAndStartBeforeOrderByStartDesc(
                            itemFromDB, BookingStatus.APPROVED, LocalDateTime.now());
            List<Booking> nextListBooking =
                    bookingRepository.findAllByItemAndStatusAndStartAfterOrderByStartAsc(
                            itemFromDB, BookingStatus.APPROVED, LocalDateTime.now());
            Booking lastBooking = null;
            if (!lastListBooking.isEmpty()) lastBooking = lastListBooking.get(0);
            Booking nextBooking = null;
            if (!nextListBooking.isEmpty()) nextBooking = nextListBooking.get(0);
            BookingResponseDateDto lastBookingResponseDto = bookingMapper.toBookingDateDto(lastBooking); // переводим из Booking в DTO Booking
            BookingResponseDateDto nextBookingResponseDto = bookingMapper.toBookingDateDto(nextBooking);
            itemBookingResponseDto.setLastBooking(lastBookingResponseDto); // сохранили полученные DTO бронирования в Item
            itemBookingResponseDto.setNextBooking(nextBookingResponseDto);
        }
        itemBookingResponseDto.setComments(commentMapper.toListCommentDto(commentRepository.findByItemId(itemId))); // добавили также комментарии
        return itemBookingResponseDto;
    }

    @Override
    public List<ItemBookingResponseDto> getItemsBooking(Long userId, Integer from, Integer size) {
        if (size < 1) throw new ValidationException("Page size must not be less than one");
        if (from < 0) throw new ValidationException("Index 'from' must not be less than zero");

        Sort sortById = Sort.by(Sort.Direction.ASC, "id"); // сначала создаём описание сортировки по полю id
        Pageable page = PageRequest.of(from / size, size, sortById); // затем создаём описание "страницы" размером size элемента
        User userFromDB = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User is not found")); // выгрузили из БД User по его ID
        Page<Item> itemListFromDB = itemRepository.findItemsByOwnerId(userId, page); // выгрузили из БД все Item, где владельцем является наш User из БД
        // теперь надо добавить к каждому item lastbooking, nextbooking, comment
        // 1. для начала выгрузим сразу все прошедшие бронирования (lastbooking), где user владелец
        List<Booking> lastListBooking = bookingRepository.findAllByItemOwnerAndStatusAndStartBeforeOrderByStartDesc(
                userFromDB, BookingStatus.APPROVED, LocalDateTime.now());
        final Map<Item, Booking> lastMapBooking = new HashMap<>(); // перекладываем в map, где ключ Item, сохраняем только lastbooking
        for (Booking b : lastListBooking) {
            if (lastMapBooking.containsKey(b.getItem())) {
                if (b.getStart().isAfter(lastMapBooking.get(b.getItem()).getStart())) lastMapBooking.put(b.getItem(), b);
            } else lastMapBooking.put(b.getItem(), b); // такого item в map еще нет, записываем без сравнений
        }
        // 2. и выгрузим сразу все будущие бронирования (nextbooking), где user владелец
        List<Booking> nextListBooking = bookingRepository.findAllByItemOwnerAndStatusAndStartAfterOrderByStartAsc(
                userFromDB, BookingStatus.APPROVED, LocalDateTime.now());
        final Map<Item, Booking> nextMapBooking = new HashMap<>(); // перекладываем в map, где ключ Item, сохраняем только nextbooking
        for (Booking b : nextListBooking) {
            if (nextMapBooking.containsKey(b.getItem())) {
                if (b.getStart().isBefore(nextMapBooking.get(b.getItem()).getStart()))
                    nextMapBooking.put(b.getItem(), b);
            } else {
                nextMapBooking.put(b.getItem(), b); // такого item в map еще нет, записываем без сравнений
            }
        }

        // 3. все комментарии к Item, где user владелец
        List<Comment> allCommentsListForUser = commentRepository.findByAuthorId(userId);
        final Map<Item, List<Comment>> commentMap = new HashMap<>(); // перекладываем все комментарии в map, где ключ Item
        for (Comment c : allCommentsListForUser) {
            if (commentMap.containsKey(c.getItem())) {
                commentMap.get(c.getItem()).add(c); // такой item уже есть, значит добавляем комментарий к существующему списку
            } else {
                List<Comment> commentList = new ArrayList<>();
                commentList.add(c);
                commentMap.put(c.getItem(), commentList);
            }
        }
        List<ItemBookingResponseDto> itemBookingResponseDtoList = new ArrayList<>();
        for (Item itemFromDB : itemListFromDB.getContent()) { // теперь надо перевести из Item в DTO
            ItemBookingResponseDto itemBookingResponseDto = itemMapper.toItemBookingDto(itemFromDB);
            BookingResponseDateDto lastBookingResponseDto =
                    bookingMapper.toBookingDateDto(lastMapBooking.get(itemFromDB)); // переводим из Booking в DTO Booking - для текущего Item
            BookingResponseDateDto nextBookingResponseDto =
                    bookingMapper.toBookingDateDto(nextMapBooking.get(itemFromDB));
            List<CommentResponseDto> commentResponseDtoList =
                    commentMapper.toListCommentDto(commentMap.get(itemFromDB));
            itemBookingResponseDto.setLastBooking(lastBookingResponseDto); // сохранили полученные DTO бронирования в Item
            itemBookingResponseDto.setNextBooking(nextBookingResponseDto);
            itemBookingResponseDto.setComments(commentResponseDtoList);
            itemBookingResponseDtoList.add(itemBookingResponseDto); // получившийся itemBookingResponseDto записываем в массив ответа itemBookingResponseDtoList
        }
        return itemBookingResponseDtoList;
    }

    @Override
    public ItemResponseDto addNewItem(ItemCreateRequestDto itemCreateRequestDto, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User is not found")); // проверка есть ли User с таким id
        Item currentItem = itemMapper.toItem(itemCreateRequestDto, userId); // перевели из DTO в model Item
        if (itemCreateRequestDto.getRequestId() != null) {
            currentItem.setRequest(itemRequestRepository.findById(itemCreateRequestDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException(
                            "ItemRequest with ID " + itemCreateRequestDto.getRequestId() + " is not found")));
        }
        return itemMapper.toItemDto(itemRepository.save(currentItem));
    }

    @Override
    public ItemResponseDto updateItem(Long itemId, ItemResponseDto itemResponseDto, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User is not found")); // проверка есть ли User с таким id
        Item item = itemMapper.toItem(itemResponseDto, userId); // перевели из DTO в model Item
        if (!Objects.equals(item.getOwner().getId(), getItem(itemId).getOwner().getId())) {
            throw new NotFoundException("user is not owner");
        }
        Item itemFromDB = getItem(itemId);
        if (Objects.nonNull(item.getName())) itemFromDB.setName(item.getName());
        if (Objects.nonNull(item.getDescription())) itemFromDB.setDescription(item.getDescription());
        if (Objects.nonNull(item.getAvailable())) itemFromDB.setAvailable(item.getAvailable());
        itemRepository.save(itemFromDB);
        return itemMapper.toItemDto(itemFromDB);
    }

    @Override
    public List<ItemResponseDto> getItemsBySearch(String text, Integer from, Integer size) {
        if (size < 1) throw new ValidationException("Page size must not be less than one");
        if (from < 0) throw new ValidationException("Index 'from' must not be less than zero");
        if (text.isEmpty()) return new ArrayList<>();
        Sort sortById = Sort.by(Sort.Direction.ASC, "id"); // сначала создаём описание сортировки по полю id
        Pageable page = PageRequest.of(from / size, size, sortById); // затем создаём описание "страницы" размером size элемента
        Page<Item> itemList = itemRepository.searchItem(text, page);
        return itemMapper.toListItemDto(itemList.getContent());
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User is not found")); // проверка есть ли User с таким id
        if (!Objects.equals(userId, getItem(itemId).getOwner().getId()))
            throw new NotFoundException("user is not owner");
        itemRepository.deleteById(itemId);
    }

    @Override
    public CommentResponseDto addComment(Long userId, CommentCreateRequestDto commentCreateRequestDto, Long itemId) {
        Comment comment = commentMapper.toComment(commentCreateRequestDto);
        comment.setItem(getItem(itemId));
        comment.setAuthor(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User is not found")));
        if (bookingRepository.findAllByBookerAndItemAndEndBeforeOrderByStartDesc(
                        comment.getAuthor(), comment.getItem(), LocalDateTime.now()).isEmpty()) {
            throw new ValidationException("user is not booker or booking has not yet finished");
        } else {
            comment.setCreated(LocalDateTime.now());
            commentRepository.save(comment);
            return commentMapper.toCommentDto(comment);
        }
    }
}
