package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class UserCreateRequestDto {

    private Long id;

    @NotNull
    private String name;

    @Email
    @NotNull
    private String email;
}
