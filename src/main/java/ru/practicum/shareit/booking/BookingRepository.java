package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndItemId(long userId, long itemId);

    List<Booking> findByBookerId(long userId, Pageable pageable);

    List<Booking> findByItemIdIn(List<Long> allItemsByUser, Pageable pageable);

    List<Booking> findByItemIdAndStatus(long itemId, BookingStatus approved);
}
