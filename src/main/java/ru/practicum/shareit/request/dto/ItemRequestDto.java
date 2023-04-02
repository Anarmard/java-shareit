package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.dto.UserResponseDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDto {
    Long id; // уникальный идентификатор запроса

    @NotBlank
    String description; // текст запроса, содержащий описание требуемой вещи

    UserResponseDto requestor; // пользователь, создавший запрос

    LocalDateTime created; // дата и время создания запроса
}
