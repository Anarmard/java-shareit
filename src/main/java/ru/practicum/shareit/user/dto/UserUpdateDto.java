package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.Valid;

@Valid
@Getter
@AllArgsConstructor
public class UserUpdateDto {

    private Long id;

    private String name;

    private String email;
}