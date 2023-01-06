package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
@Valid
@Getter
@AllArgsConstructor // конструктор на все параметры
public class ItemUpdateDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private UserCreateRequestDto owner;

    private ItemRequestDto request;
}
