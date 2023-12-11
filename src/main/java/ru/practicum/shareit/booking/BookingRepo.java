package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepo extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndItemId(long userId, long itemId);

    List<Booking> findByBookerId(long userId);

    List<Booking> findByItemIdIn(List<Long> allItemsByUser);

    List<Booking> findByItemIdAndStatus(long itemId, BookingStatus approved);
}
