package ru.practicum.shareitserver.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareitserver.user.dto.UserCreateRequestDto;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor // конструктор на все параметры
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentCreateRequestDto {
    Long id;
    String text;
    ItemCreateRequestDto item;
    UserCreateRequestDto authorName;
    LocalDateTime created;
}
