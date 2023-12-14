package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDtoIn;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Booking bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                            @Valid @RequestBody BookingDtoIn bookingDto) {
        log.info("Requested booking for item id = {} from user id = {}", bookingDto.getItemId(), userId);
        return bookingService.create(bookingDto, userId);
    }

    @GetMapping("/{bookingId}")
    public Booking getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long bookingId) {
        log.info("Requested booking with id = {} from user id = {}", bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public Booking checkRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                @PathVariable long bookingId,
                                @RequestParam boolean approved) {
        log.info("Requested checking for booking id = {}", bookingId);
        return bookingService.checkRequest(userId, bookingId, approved);
    }

    @GetMapping
    public List<Booking> getBookingsByStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestParam(defaultValue = "ALL") String state) {
        log.info("Requested all bookings with state = {} of user id = {}", state, userId);
        return bookingService.getBookingsByStatus(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getUserBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(defaultValue = "ALL") String state) {
        log.info("Requested all bookings with {} state for owner = {}", state, userId);
        return bookingService.getUserBookings(userId, state);
    }
}
