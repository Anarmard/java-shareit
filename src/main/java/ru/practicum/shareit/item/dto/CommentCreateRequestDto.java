package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor // конструктор на все параметры
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentCreateRequestDto {

    Long id;

    @NotBlank
    String text;

    ItemCreateRequestDto item;

    UserCreateRequestDto authorName;

    LocalDateTime created;
}
