package ru.practicum.shareit.booking.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(schema = "public", name = "bookings")
public class Booking {

    @Id
    //@SequenceGenerator(name = "pk_sequence", schema = "public", sequenceName = "bookings_id_seq", allocationSize = 1)
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id; // уникальный идентификатор бронирования

    @Column(name = "start_date", nullable = false)
    private LocalDateTime start; // дата и время начала бронирования

    @Column(name = "end_date", nullable = false)
    private LocalDateTime end; // дата и время конца бронирования

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item; // вещь, которую пользователь бронирует

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker; // пользователь, который осуществляет бронирование

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10, nullable = false)
    private BookingStatus status; // статус бронирования
}