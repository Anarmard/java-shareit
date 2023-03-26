package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBookerOrderByStartDesc(User booker, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker = :booker and :now between b.start and b.end order by b.start desc ")
    Page<Booking> findCurrentByBooker(@Param("booker") User booker, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker = :booker and b.end < :now order by b.start desc ")
    Page<Booking> findPastByBooker(@Param("booker") User booker, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.booker = :booker and b.item = :item and b.end < :now order by b.start desc ")
    List<Booking> findBookingByBookerAndByItem(@Param("booker") User booker,
                                               @Param("item") Item item,
                                               @Param("now") LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.booker = :booker and b.start > :now order by b.start desc ")
    Page<Booking> findFutureByBooker(@Param("booker") User booker, @Param("now") LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByBookerAndStatusOrderByStartDesc(
            @Param("booker") User booker,
            @Param("status") BookingStatus status, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner = :owner order by b.start desc ")
    Page<Booking> findAllByOwnerItems(@Param("owner") User owner, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner = :owner and :now between b.start and b.end order by b.start desc ")
    Page<Booking> findCurrentByOwnerItems(@Param("owner") User owner, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner = :owner and b.end < :now order by b.start desc ")
    Page<Booking> findPastByOwnerItems(@Param("owner") User owner, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner = :owner and b.start > :now order by b.start desc ")
    Page<Booking> findFutureByOwnerItems(@Param("owner") User owner, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner = :owner and b.status = :status order by b.start desc ")
    Page<Booking> findAllByOwnerAndStatus(@Param("owner") User owner, @Param("status")BookingStatus status, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item = :item and b.status = :status and b.start < :now " +
            "order by b.start desc")
    List<Booking> findLastBookingOfItem(@Param("item") Item item,
                                        @Param("status")BookingStatus status,
                                        @Param("now") LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item = :item and b.status = :status and b.start > :now " +
            "order by b.start")
    List<Booking> findNextBookingOfItem(@Param("item") Item item,
                                        @Param("status")BookingStatus status,
                                        @Param("now") LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.owner = :owner and b.status = :status and b.start < :now " +
            "order by b.start desc")
    List<Booking> findLastBookingForOwner(@Param("owner") User owner,
                                        @Param("status")BookingStatus status,
                                        @Param("now") LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.owner = :owner and b.status = :status and b.start > :now " +
            "order by b.start")
    List<Booking> findNextBookingForOwner(@Param("owner") User owner,
                                        @Param("status")BookingStatus status,
                                        @Param("now") LocalDateTime now);
}
