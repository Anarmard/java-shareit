package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ItemRequestForResponseDto {

    Long id; // уникальный идентификатор запроса

    String description; // текст запроса, содержащий описание требуемой вещи

    UserResponseDto requestor; // пользователь, создавший запрос

    LocalDateTime created; // дата и время создания запроса

    List<ItemForItemRequestDto> items; // список ответов на данный запрос
}
