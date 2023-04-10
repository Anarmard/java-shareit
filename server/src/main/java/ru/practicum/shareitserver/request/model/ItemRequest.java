package ru.practicum.shareitserver.request.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareitserver.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    Long id; // уникальный идентификатор запроса

    @Column(nullable = false)
    String description; // текст запроса, содержащий описание требуемой вещи

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", nullable = false)
    User requestor; // пользователь, создавший запрос

    @Column(name = "created_date", nullable = false)
    LocalDateTime created; // дата и время создания запроса
}
