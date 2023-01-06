package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

// добавили в pom зависимости с mapstruct (в 4-х местах) поэтому можем использовать данный функционал
@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ItemMapper {

    ItemResponse toItemDto(Item item);
    // mapstruct сам генерит необходимый код для преобразования Item в ItemDto

    List<ItemResponse> toListItemDto(List<Item> itemList);

    @Mapping(target = "owner.id", source = "userId")
    Item toItem(ItemCreateRequest itemCreateRequest, Long userId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "owner.id", source = "userId")
    Item toItem(ItemUpdateDto itemDto, Long userId);
}