package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "requests")
public class ItemRequest {

    @Id
    //@SequenceGenerator(name = "pk_sequence", schema = "public", sequenceName = "requests_id_seq", allocationSize = 1)
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id; // уникальный идентификатор запроса

    @Column(name = "description", nullable = false)
    private String description; // текст запроса, содержащий описание требуемой вещи

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor; // пользователь, создавший запрос

    private LocalDateTime created; // дата и время создания запроса
}
