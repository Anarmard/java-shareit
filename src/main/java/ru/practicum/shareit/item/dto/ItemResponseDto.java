package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;

@Getter
@AllArgsConstructor // конструктор на все параметры
public class ItemResponseDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private UserCreateRequestDto owner;

    private ItemRequestDto request;
}
