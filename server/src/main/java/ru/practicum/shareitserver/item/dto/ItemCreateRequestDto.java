package ru.practicum.shareitserver.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareitserver.user.dto.UserCreateRequestDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor // конструктор на все параметры
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemCreateRequestDto {

    Long id;

    @NotBlank
    String name;

    @NotBlank
    String description;

    @NotNull
    Boolean available;

    UserCreateRequestDto owner;

    Long requestId;
}
