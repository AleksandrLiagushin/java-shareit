package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.UserCreationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @InjectMocks
    private BookingController bookingController;

    @Mock
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @BeforeEach
    void setupStandalone() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();
    }

    @Test
    void bookItem() throws Exception {
//        User user = createUser(1L, "Vasya", "vas@email.com");
//        User user2 = createUser(2L, "Vanya", "van@email.com");
//        Item item = createItem(1L, "Item", "Description", user, true);
//        Booking booking = new Booking();
//        booking.setId(1L);
//        booking.setItem(item);
//        booking.setStart(LocalDateTime.of(2024, Month.APRIL, 8, 12, 30));
//        booking.setEnd(LocalDateTime.of(2024, Month.APRIL, 12, 12, 30));
//        booking.setBooker(user2);
//        booking.setStatus(BookingStatus.WAITING);
//
//        String requestBody = "{\n" +
//                "    \"id\": \"" + 1 + "\",\n" +
//                "    \"itemId\": \"" + 1 + "\",\n" +
//                "    \"start\": \"" + LocalDateTime.of(2024, Month.APRIL, 8, 12, 30) + "\"\n" +
//                "    \"start\": \"" + LocalDateTime.of(2024, Month.APRIL, 12, 12, 30) + "\"\n" +
//                "}";
//
//        when(bookingService.create(any(), anyLong())).thenReturn(booking);
//
//        mockMvc.perform(post("/bookings")
//                        .header("X-Sharer-User-Id", booking.getBooker().getId())
//                        .content(requestBody)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.start[0]").value(2024))
//                .andExpect(jsonPath("$.start[1]").value(4));
//
//        Mockito.verify(bookingRepository, times(0)).save(booking);
//        Mockito.verify(bookingService, times(1)).create(any(), anyLong());
//        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBooking() {
    }

    @Test
    void checkRequest() throws Exception {
//        User user = createUser(1L, "Vasya", "vas@email.com");
//        User user2 = createUser(2L, "Vanya", "van@email.com");
//        Item item = createItem(1L, "Item", "Description", user, true);
//        Booking booking = new Booking();
//        booking.setId(1L);
//        booking.setItem(item);
//        booking.setStart(LocalDateTime.of(2024, Month.APRIL, 8, 12, 30));
//        booking.setEnd(LocalDateTime.of(2024, Month.APRIL, 12, 12, 30));
//        booking.setBooker(user2);
//        booking.setStatus(BookingStatus.WAITING);
//
//        when(bookingService.checkRequest(anyLong(), anyLong(), anyBoolean())).thenReturn(booking);
//
//        mockMvc.perform(patch("/bookings/{bookingId}", booking.getId())
//                        .header("X-Sharer-User-Id", 1L)
//                        .param("approved", String.valueOf(true))
//                        .content(objectMapper.writeValueAsString(booking))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.status").value("APPROVED"))
//                .andExpect(jsonPath("$.start[0]").value(2024))
//                .andExpect(jsonPath("$.start[1]").value(4));
//        Mockito.verify(bookingRepository, times(0)).save(booking);
//        Mockito.verify(bookingService, times(1)).checkRequest(anyLong(), anyLong(), anyBoolean());
//        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingsByStatus() {
    }

    @Test
    void getUserBookings() {
    }

    private Item createItem(long id, String name, String description, User user, boolean isAvailable) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setOwner(user);
        item.setAvailable(isAvailable);

        return item;
    }

    private User createUser(long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    private Booking createBooking(long id, LocalDateTime start, LocalDateTime end, Item item, User user, BookingStatus status) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(status);

        return booking;
    }
}