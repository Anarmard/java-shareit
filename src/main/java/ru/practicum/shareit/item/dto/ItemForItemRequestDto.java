package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // конструктор на все параметры
public class ItemForItemRequestDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long userId;

    private Long requestId;
}
