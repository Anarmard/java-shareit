package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    private Long id; // уникальный идентификатор запроса
    private String description; // текст запроса, содержащий описание требуемой вещи
    private UserResponseDto requestor; // пользователь, создавший запрос
    private LocalDateTime created; // дата и время создания запроса
}
