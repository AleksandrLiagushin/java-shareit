package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.exception.AvailabilityException;
import ru.practicum.shareit.exception.ItemNotExistException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.TimeValidationException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepo;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepo;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingService {
    private final BookingRepo bookingStorage;
    private final ItemRepo itemStorage;
    private final UserRepo userStorage;
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
    public Booking checkRequest(long userId, long bookingId, String approved) {

        Booking saveBooking = bookingStorage.findById(bookingId).orElseThrow();
        saveBooking.setBooker(userStorage.getReferenceById(saveBooking.getBooker().getId()));
        saveBooking.setItem(itemStorage.getReferenceById(saveBooking.getItem().getId()));

        if (approved.isBlank()) {
            throw new ItemNotFoundException("approved must be true/false");
        }

        if (bookingId == 0) {
            throw new ItemNotFoundException("bookingId can't be null");
        }

        if (!bookingStorage.existsById(bookingId)) {
            throw new ItemNotFoundException("This booking not found");
        }

        if (!userStorage.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        if (saveBooking.getItem().getOwner().getId() != userId) {
            log.warn("Change status can only owner");
            throw new UserNotFoundException("Change status can only owner");
        }

        if (saveBooking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ItemNotExistException("This booking has been already approved");
        }

        switch (approved) {
            case "true":
                if (!saveBooking.getItem().getAvailable()) {
                    saveBooking.setStatus(BookingStatus.WAITING);
                }
                saveBooking.setStatus(BookingStatus.APPROVED);

                return saveBooking;

            case "false":
                saveBooking.setStatus(BookingStatus.REJECTED);
                return saveBooking;

            default:
                throw new ItemNotFoundException("Approved must be true or false");
        }
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
            throw new ItemNotFoundException("Get information about booking can owner item or booker only");
        }

        return saveBooking;
    }

    public List<Booking> getBookingsByStatus(long userId, String state) {

        if (!userStorage.existsById(userId)) {
            throw new ItemNotFoundException("This user not exist");
        }

        List<Booking> bookingsByUserId = bookingStorage.findByBookerId(userId);
        return checkState(bookingsByUserId, state);
    }

    public List<Booking> getUserBookings(long ownerId, String state) {

        List<Item> itemByOwnerId = itemStorage.findByOwnerId(ownerId);

        if (itemByOwnerId.isEmpty()) {
            throw new ItemNotFoundException("This owner haven't any item");
        }

        List<Long> allItemsByUser = itemByOwnerId.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> saveBooking = bookingStorage.findByItemIdIn(allItemsByUser);

        return checkState(saveBooking, state);

    }

    public List<Booking> checkState(List<Booking> saveBooking, String state) {

        switch (state) {
            case "ALL":
                log.info("Get list by status ALL");
                return saveBooking.stream()
                        .sorted((Comparator.comparing(Booking::getStart)).reversed())
                        .collect(Collectors.toList());

            case "CURRENT":
                log.info("Get list by status CURRENT");
                return saveBooking.stream()
                        .filter(x -> x.getEnd().isAfter(LocalDateTime.now()) && x.getStart().isBefore(LocalDateTime.now()))
                        .sorted((Comparator.comparing(Booking::getStart)).reversed())
                        .collect(Collectors.toList());

            case "PAST":
                log.info("Get list by status PAST");
                return saveBooking.stream()
                        .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                        .sorted((Comparator.comparing(Booking::getStart)).reversed())
                        .collect(Collectors.toList());

            case "FUTURE":
                log.info("Get list by status FUTURE");
                return saveBooking.stream()
                        .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                        .sorted((Comparator.comparing(Booking::getStart)).reversed())
                        .collect(Collectors.toList());

            case "WAITING":
                log.info("Get list by status WAITING");
                return saveBooking.stream()
                        .filter(x -> x.getStatus().equals(BookingStatus.WAITING))
                        .sorted((Comparator.comparing(Booking::getStart)).reversed())
                        .collect(Collectors.toList());

            case "REJECTED":
                log.info("Get list by status REJECTED");
                return saveBooking.stream()
                        .filter(x -> x.getStatus().equals(BookingStatus.REJECTED))
                        .sorted((Comparator.comparing(Booking::getStart)).reversed())
                        .collect(Collectors.toList());

            default:
                throw new ItemNotExistException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
