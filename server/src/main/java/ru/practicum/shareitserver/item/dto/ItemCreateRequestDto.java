package ru.practicum.shareitserver.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareitserver.user.dto.UserCreateRequestDto;

@Getter
@AllArgsConstructor // конструктор на все параметры
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemCreateRequestDto {
    Long id;
    String name;
    String description;
    Boolean available;
    UserCreateRequestDto owner;
    Long requestId;
}
