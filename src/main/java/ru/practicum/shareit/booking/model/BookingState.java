package ru.practicum.shareit.booking.model;

import java.util.Arrays;
import java.util.Objects;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED,
    UNKNOWN;

    // если не нашли один из статусов, то выдаем UNKNOWN
    public static BookingState of(String value) {
        return Arrays.stream(values())
                .filter(item -> Objects.equals(value, item.name()))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
