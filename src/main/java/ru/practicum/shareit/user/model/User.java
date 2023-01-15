package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Valid
@Data
public class User {
    private Long id; // уникальный идентификатор пользователя

    @NotNull
    private String name; // имя или логин пользователя

    @NotNull
    @Email
    private String email; // адрес электронной почты
}
