package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Valid
@Getter
@AllArgsConstructor // конструктор на все параметры
public class CommentCreateRequestDto {

    private Long id;

    @NotBlank
    private String text;

    private Item item;

    private User authorName;

    private LocalDateTime created;
}
