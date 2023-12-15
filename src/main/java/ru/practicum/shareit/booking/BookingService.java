package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.exception.AvailabilityException;
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.TimeValidationException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingStorage;
    private final ItemRepository itemStorage;
    private final UserRepository userStorage;
    private final BookingMapper bookingMapper;

    @Transactional(rollbackFor = Exception.class)
    public Booking create(BookingDtoIn bookingDto, long userId) {
        if (!userStorage.existsById(userId)) {
            throw new UserNotFoundException("No user with such was found");
        }
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new TimeValidationException("Start or end time can't be null");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new TimeValidationException("Start time must be before end time");
        }
        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new TimeValidationException("End time must be after start time");
        }
        if (!itemStorage.existsById(bookingDto.getItemId())) {
            throw new ItemNotFoundException("No item with such id was found");
        }

        Item saved = itemStorage.findById(bookingDto.getItemId()).orElseThrow();

        if (!saved.getAvailable()) {
            throw new AvailabilityException("Item is not available");
        }
        if (saved.getOwner().getId() == userId) {
            throw new ItemNotFoundException("You can't book your items");
        }

        Booking booking = bookingMapper.toEntity(bookingDto);
        booking.setItem(itemStorage.findById(bookingDto.getItemId()).orElseThrow());
        booking.setBooker(userStorage.findById(userId).orElseThrow());
        booking.setStatus(BookingStatus.WAITING);
        return bookingStorage.save(booking);
    }

    @Transactional
    public Booking checkRequest(long userId, long bookingId, boolean approved) {

        if (!bookingStorage.existsById(bookingId)) {
            throw new ItemNotFoundException("Booking with such id doesn't exist");
        }

        if (!userStorage.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        Booking booking = bookingStorage.findById(bookingId).orElseThrow();
        booking.setBooker(userStorage.getReferenceById(booking.getBooker().getId()));
        booking.setItem(itemStorage.getReferenceById(booking.getItem().getId()));

        if (booking.getItem().getOwner().getId() != userId) {
            throw new UserNotFoundException("Only owner can change the status");
        }

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ItemNotExistException("This booking has been already approved");
        }

        if (approved) {
            if (!booking.getItem().getAvailable()) {
                booking.setStatus(BookingStatus.WAITING);
            }
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return booking;
    }

    public Booking getBooking(long userId, long bookingId) {
        if (!userStorage.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }
        if (!bookingStorage.existsById(bookingId)) {
            throw new ItemNotFoundException("This booking not found");
        }

        Booking saveBooking = bookingStorage.findById(bookingId).orElseThrow();

        if (saveBooking.getBooker().getId() != userId
                && (saveBooking.getItem().getOwner().getId() != userId)) {
            throw new ItemNotFoundException("Only owner/booker can info about booking");
        }

        return saveBooking;
    }

    public List<Booking> getBookingsByStatus(long userId, String state, Pageable pageable) {

        if (!userStorage.existsById(userId)) {
            throw new ItemNotFoundException("User with such id doesn't exist");
        }

        List<Booking> bookings = bookingStorage.findByBookerId(userId, pageable);
        return checkState(bookings, state);
    }

    public List<Booking> getUserBookings(long ownerId, String state, Pageable pageable) {

        List<Item> itemByOwnerId = itemStorage.findByOwnerId(ownerId);

        if (itemByOwnerId.isEmpty()) {
            throw new ItemNotFoundException("No items have been found for this owner");
        }

        List<Long> allItemsByUser = itemByOwnerId.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> bookings = bookingStorage.findByItemIdIn(allItemsByUser, pageable);

        return checkState(bookings, state);
    }

    public List<Booking> checkState(List<Booking> bookings, String state) {

        switch (state) {
            case "ALL":
                return bookings.stream()
                        .sorted((Comparator.comparing(Booking::getStart)).reversed())
                        .collect(Collectors.toList());

            case "CURRENT":
                return bookings.stream()
                        .filter(x -> x.getStart().isBefore(LocalDateTime.now())
                                && x.getEnd().isAfter(LocalDateTime.now()))
                        .sorted((Comparator.comparing(Booking::getStart)).reversed())
                        .collect(Collectors.toList());

            case "PAST":
                return bookings.stream()
                        .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                        .sorted((Comparator.comparing(Booking::getStart)).reversed())
                        .collect(Collectors.toList());

            case "FUTURE":
                return bookings.stream()
                        .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                        .sorted((Comparator.comparing(Booking::getStart)).reversed())
                        .collect(Collectors.toList());

            case "WAITING":
                return bookings.stream()
                        .filter(x -> x.getStatus().equals(BookingStatus.WAITING))
                        .sorted((Comparator.comparing(Booking::getStart)).reversed())
                        .collect(Collectors.toList());

            case "REJECTED":
                return bookings.stream()
                        .filter(x -> x.getStatus().equals(BookingStatus.REJECTED))
                        .sorted((Comparator.comparing(Booking::getStart)).reversed())
                        .collect(Collectors.toList());

            default:
                throw new ItemNotExistException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
