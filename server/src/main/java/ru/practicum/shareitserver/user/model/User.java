package ru.practicum.shareitserver.user.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    Long id; // уникальный идентификатор пользователя

    @Column(name = "name", nullable = false)
    String name; // имя или логин пользователя

    @Column(name = "email", nullable = false, unique = true)
    String email; // адрес электронной почты
}
