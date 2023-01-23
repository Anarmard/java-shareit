package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.sql.Timestamp;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerOrderByStartDesc(User booker);

    @Query("select b from Booking b " +
            "where b.booker = :booker and :now between b.start and b.end order by b.start desc ")
    List<Booking> findCurrentByBooker(@Param("booker") User booker, @Param("now") Timestamp now);

    @Query("select b from Booking b " +
            "where b.booker = :booker and b.end < :now order by b.start desc ")
    List<Booking> findPastByBooker(@Param("booker") User booker, @Param("now") Timestamp now);

    @Query("select b from Booking b " +
            "where b.booker = :booker and b.start > :now order by b.start desc ")
    List<Booking> findFutureByBooker(@Param("booker") User booker, @Param("now") Timestamp now);

    List<Booking> findAllByBookerAndStatusOrderByStartDesc(
            @Param("booker") User booker,
            @Param("status") BookingStatus status);

    @Query("select b from Booking b " +
            "where b.item.owner = :owner order by b.start desc ")
    List<Booking> findAllByOwnerItems(@Param("owner") User owner);

    @Query("select b from Booking b " +
            "where b.item.owner = :owner and :now between b.start and b.end order by b.start desc ")
    List<Booking> findCurrentByOwnerItems(@Param("owner") User owner, @Param("now") Timestamp now);

    @Query("select b from Booking b " +
            "where b.item.owner = :owner and b.end < :now order by b.start desc ")
    List<Booking> findPastByOwnerItems(@Param("owner") User owner, @Param("now") Timestamp now);

    @Query("select b from Booking b " +
            "where b.item.owner = :owner and b.start > :now order by b.start desc ")
    List<Booking> findFutureByOwnerItems(@Param("owner") User owner, @Param("now") Timestamp now);

    @Query("select b from Booking b " +
            "where b.item.owner = :owner and b.status = :status order by b.start desc ")
    List<Booking> findAllByOwnerAndStatus(@Param("owner") User owner, @Param("status")BookingStatus status);

    Booking findFirstByItemAndStatusOrderByStartAsc (
            @Param("item") Item item,
            @Param("status") BookingStatus status);

    Booking findFirstByItemAndStatusOrderByEndDesc (
            @Param("item") Item item,
            @Param("status") BookingStatus status);
}
