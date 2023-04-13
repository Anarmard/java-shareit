package ru.practicum.shareitserver.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor // конструктор на все параметры
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemForItemRequestDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long userId;
    Long requestId;
}
