package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemBookingResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

// добавили в pom зависимости с mapstruct (в 4-х местах) поэтому можем использовать данный функционал
@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ItemMapper {

    ItemResponseDto toItemDto(Item item);
    // mapstruct сам генерит необходимый код для преобразования Item в ItemDto

    List<ItemResponseDto> toListItemDto(List<Item> itemList);

    @Mapping(target = "owner.id", source = "userId")
    Item toItem(ItemCreateRequestDto itemCreateRequest, Long userId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "owner.id", source = "userId")
    Item toItem(ItemUpdateDto itemDto, Long userId);

    ItemBookingResponseDto toItemBookingDto(Item item);
}