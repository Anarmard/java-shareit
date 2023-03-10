package ru.practicum.shareit.item.model;

import lombok.Data;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
public class Item {
    private Long id; // уникальный идентификатор вещи
    private String name; // краткое название
    private String description; // развёрнутое описание
    private Boolean available; // статус о том, доступна или нет вещь для аренды
    private User owner; // владелец вещи
    private ItemRequest request; // если вещь была создана по запросу другого пользователя, то в этом
    // поле будет храниться ссылка на соответствующий запрос
}
