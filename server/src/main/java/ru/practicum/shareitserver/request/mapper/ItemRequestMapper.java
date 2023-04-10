package ru.practicum.shareitserver.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareitserver.request.dto.ItemRequestDto;
import ru.practicum.shareitserver.request.dto.ItemRequestForResponseDto;
import ru.practicum.shareitserver.request.model.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    @Mapping(target = "requestor.id", source = "userId")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto, Long userId);

    ItemRequestForResponseDto toItemRequestForResponseDto(ItemRequest itemRequest);
}
