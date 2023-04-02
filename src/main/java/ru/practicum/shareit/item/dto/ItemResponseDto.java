package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;

@Getter
@AllArgsConstructor // конструктор на все параметры
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemResponseDto {

    Long id;

    String name;

    String description;

    Boolean available;

    UserCreateRequestDto owner;

    Long requestId;
}
