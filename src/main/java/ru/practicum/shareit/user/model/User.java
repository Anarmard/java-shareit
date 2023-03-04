package ru.practicum.shareit.user.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(schema = "public", name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id; // уникальный идентификатор пользователя

    @Column(name = "name", nullable = false)
    private String name; // имя или логин пользователя

    @Column(name = "email", nullable = false, unique = true)
    private String email; // адрес электронной почты
}
