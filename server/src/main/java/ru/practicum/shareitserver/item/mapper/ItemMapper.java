package ru.practicum.shareitserver.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareitserver.item.dto.ItemBookingResponseDto;
import ru.practicum.shareitserver.item.dto.ItemCreateRequestDto;
import ru.practicum.shareitserver.item.dto.ItemForItemRequestDto;
import ru.practicum.shareitserver.item.dto.ItemResponseDto;
import ru.practicum.shareitserver.item.model.Item;

import java.util.List;

// добавили в pom зависимости с mapstruct (в 4-х местах) поэтому можем использовать данный функционал

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(target = "requestId", source = "request.id")
    ItemResponseDto toItemDto(Item item);
    // mapstruct сам генерит необходимый код для преобразования Item в ItemDto

    @Mapping(target = "userId", source = "owner.id")
    @Mapping(target = "requestId", source = "request.id")
    ItemForItemRequestDto toItemForItemRequestDto(Item item);

    List<ItemResponseDto> toListItemDto(List<Item> itemList);

    @Mapping(target = "owner.id", source = "userId")
    @Mapping(target = "request", ignore = true)
    Item toItem(ItemCreateRequestDto itemCreateRequest, Long userId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "owner.id", source = "userId")
    Item toItem(ItemResponseDto itemDto, Long userId);

    ItemBookingResponseDto toItemBookingDto(Item item);
}