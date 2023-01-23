package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Valid
@Getter
@AllArgsConstructor // конструктор на все параметры
public class CommentReponseDto {

    private Long id;

    private String text;

    private Item item;

    private User author;

    private LocalDateTime created;
}
