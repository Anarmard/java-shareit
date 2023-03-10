package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor // конструктор на все параметры
public class CommentResponseDto {

    private Long id;

    private String text;

    private ItemResponseDto item;

    private String authorName;

    private LocalDateTime created;
}
