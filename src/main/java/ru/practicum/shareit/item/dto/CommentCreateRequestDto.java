package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor // конструктор на все параметры
public class CommentCreateRequestDto {

    private Long id;

    @NotBlank
    private String text;

    private ItemCreateRequestDto item;

    private UserCreateRequestDto authorName;

    private LocalDateTime created;
}
