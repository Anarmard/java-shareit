package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.user.dto.UserResponseDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ItemRequestDto {
    private Long id; // уникальный идентификатор запроса

    @NotBlank
    private String description; // текст запроса, содержащий описание требуемой вещи

    private UserResponseDto requestor; // пользователь, создавший запрос

    private LocalDateTime created; // дата и время создания запроса
}
