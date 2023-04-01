package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    // добавить новый запрос вещи
    @Override
    public ItemRequestForResponseDto addNewItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        // проверили User по его ID
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with such ID does not exist"));

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto, userId);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestForResponseDto(itemRequest);
    }

    // получить список СВОИХ запросов вместе с данными об ответах на них
    @Override
    public List<ItemRequestForResponseDto> getItemRequestsByOwner(Long userId) {
        // проверили User по его ID
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with such ID does not exist"));

        // выгрузили из БД все ItemRequest, где владельцем является наш User из БД
        List<ItemRequest> itemRequestList =
                itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);

        // выгружаем все Item перекладываем в Map c c ключом request.id
        Map<Long, List<ItemForItemRequestDto>> itemRequestMap = createAllItemMap();

        return addItemsToItemsRequestsDto(itemRequestMap, itemRequestList);
    }

    // получить список ВCЕХ запросов, созданных другими пользователями
    @Override
    public List<ItemRequestForResponseDto> getAllItemRequests(Long userId, Integer from, Integer size) {
        if (size < 1) throw new ValidationException("Page size must not be less than one");
        if (from < 0) throw new ValidationException("Index 'from' must not be less than zero");

        // сначала создаём описание сортировки по полю created
        Sort sortByCreatedDate = Sort.by(Sort.Direction.DESC, "created");
        // затем создаём описание "страницы" размером size элемента
        Pageable page = PageRequest.of(from / size, size, sortByCreatedDate);

        // выгрузили из БД все ItemRequest, которые не принадлежат нашему userId
        Page<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorIdNot(userId, page);

        // выгружаем все Item перекладываем в Map c c ключом request.id
        Map<Long, List<ItemForItemRequestDto>> itemRequestMap = createAllItemMap();

        return addItemsToItemsRequestsDto(itemRequestMap, itemRequestList.getContent());
    }

    // выгружаем все Item перекладываем в Map c c ключом request.id
    private Map<Long, List<ItemForItemRequestDto>> createAllItemMap() {
        List<Item> itemListWithRequest = itemRepository.findAll();
        final Map<Long, List<ItemForItemRequestDto>> itemRequestMap = new HashMap<>();
        for (Item item : itemListWithRequest) {
            if (item.getRequest() != null) {
                ItemForItemRequestDto itemForItemRequestDto =
                        itemMapper.toItemForItemRequestDto(item);
                if (itemRequestMap.containsKey(item.getRequest().getId())) {
                    // такой request.id уже есть, значит добавляем itemForItemRequestDto к существующему списку
                    itemRequestMap.get(item.getRequest().getId()).add(itemForItemRequestDto);
                } else {
                    List<ItemForItemRequestDto> itemForCurrentItemRequestDtoList = new ArrayList<>();
                    itemForCurrentItemRequestDtoList.add(itemForItemRequestDto);
                    itemRequestMap.put(item.getRequest().getId(), itemForCurrentItemRequestDtoList);
                }
            }
        }
        return itemRequestMap;
    }

    // надо добавить к каждому ItemRequestForResponseDto список ответов
    // + теперь надо перевести все ItemRequest в DTO
    private List<ItemRequestForResponseDto> addItemsToItemsRequestsDto(Map<Long, List<ItemForItemRequestDto>> itemRequestMap,
                                                                       List<ItemRequest> itemRequestList) {
        List<ItemRequestForResponseDto> itemRequestForResponseDtoList = new ArrayList<>();
        if (!itemRequestList.isEmpty()) {
            for (ItemRequest itemRequest : itemRequestList) {
                ItemRequestForResponseDto itemRequestForResponseDto =
                        itemRequestMapper.toItemRequestForResponseDto(itemRequest);
                if (itemRequestMap.get(itemRequest.getId()) == null) {
                    itemRequestForResponseDto.setItems(new ArrayList<>());
                } else {
                    itemRequestForResponseDto.setItems(itemRequestMap.get(itemRequest.getId()));
                }
                itemRequestForResponseDtoList.add(itemRequestForResponseDto);
            }
        }
        return itemRequestForResponseDtoList;
    }

    // получить данные об одном конкретном запросе вместе с данными об ответах на него
    @Override
    public ItemRequestForResponseDto getItemRequest(Long userId, Long requestId) {
        // проверили User по его ID
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with such ID does not exist"));

        ItemRequest itemRequestFromDB = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest is not found"));

        ItemRequestForResponseDto itemRequestForResponseDto =
                itemRequestMapper.toItemRequestForResponseDto(itemRequestFromDB);

        // добавляем список ответов на запрос
        List<Item> itemListWithRequest = itemRepository.findAll();
        for (Item item : itemListWithRequest) {
            if (item.getRequest() != null) {
                if (Objects.equals(item.getRequest().getId(), requestId)) {
                    ItemForItemRequestDto itemForItemRequestDto =
                            itemMapper.toItemForItemRequestDto(item);
                    if (itemRequestForResponseDto.getItems() == null) {
                        List<ItemForItemRequestDto> itemsList = new ArrayList<>();
                        itemsList.add(itemForItemRequestDto);
                        itemRequestForResponseDto.setItems(itemsList);
                    } else {
                        itemRequestForResponseDto.getItems().add(itemForItemRequestDto);
                    }
                }
            }
        }
        return itemRequestForResponseDto;
    }
}
