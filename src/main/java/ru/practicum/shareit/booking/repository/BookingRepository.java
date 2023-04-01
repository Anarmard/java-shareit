package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBookerOrderByStartDesc(User booker, Pageable pageable);

    Page<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User booker, LocalDateTime now0, LocalDateTime now1, Pageable pageable);

    Page<Booking> findAllByBookerAndEndBeforeOrderByStartDesc(User booker, LocalDateTime now0, Pageable pageable);

    List<Booking> findAllByBookerAndItemAndEndBeforeOrderByStartDesc(User booker, Item item, LocalDateTime now0);

    Page<Booking> findAllByBookerAndStartAfterOrderByStartDesc(User booker, LocalDateTime now0, Pageable pageable);

    Page<Booking> findAllByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status, Pageable pageable);

    Page<Booking> findAllByItemOwnerOrderByStartDesc(User owner, Pageable pageable);

    Page<Booking> findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(
            User owner, LocalDateTime now0, LocalDateTime now1, Pageable pageable);

    Page<Booking> findAllByItemOwnerAndEndBeforeOrderByStartDesc(User owner, LocalDateTime now0, Pageable pageable);

    Page<Booking> findAllByItemOwnerAndStartAfterOrderByStartDesc(User owner, LocalDateTime now0, Pageable pageable);

    Page<Booking> findAllByItemOwnerAndStatusOrderByStartDesc(User owner, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemAndStatusAndStartBeforeOrderByStartDesc(Item item, BookingStatus status, LocalDateTime now0);

    List<Booking> findAllByItemAndStatusAndStartAfterOrderByStartAsc(Item item, BookingStatus status, LocalDateTime now0);

    List<Booking> findAllByItemOwnerAndStatusAndStartBeforeOrderByStartDesc(User owner, BookingStatus status, LocalDateTime now0);

    List<Booking> findAllByItemOwnerAndStatusAndStartAfterOrderByStartAsc(User owner, BookingStatus status, LocalDateTime now0);
}
