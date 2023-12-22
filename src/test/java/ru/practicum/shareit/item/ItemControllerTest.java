package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingForOwnerDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    private MockMvc mockMvc;
    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemService itemService;

    @Mock
    private ItemMapper itemMapper;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
    }

    @Test
    void create() throws Exception {
        User user = createUser(1L, "Vasya", "vas@email.com");
        Item item = createItem(1L, "Item", "Description", user, true);
        ItemDto itemDto = createItemDto(1L, "item", "text", true);
        String requestBody = "{\n" +
                "    \"name\": \"item\",\n" +
                "    \"description\": \"text\",\n" +
                "    \"available\": \"true\"\n" +
                "}";

        when(itemService.create(any(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", item.getOwner().getId())
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("item"))
                .andExpect(jsonPath("$.description").value("text"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.requestId").value(0));
    }

    @Test
    void addComment() {
    }

    @Test
    void update() throws Exception {
        User user = createUser(1L, "Vasya", "vas@email.com");
        Item item = createItem(1L, "Item2", "Description2", user, true);
        ItemDto itemDto = createItemDto(1L, "Item2", "Description2", true);
        String requestBody = "{\n" +
                "    \"name\": \"Item2\",\n" +
                "    \"description\": \"Description2\",\n" +
                "    \"available\": \"true\"\n" +
                "}";

        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(patch("/items" + "/{itemId}", item.getId())
                        .header("X-Sharer-User-Id", item.getOwner().getId())
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item2"))
                .andExpect(jsonPath("$.description").value("Description2"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.requestId").value(0));
    }

    @Test
    void getById() throws Exception {
        User user = createUser(1L, "Vasya", "vas@email.com");
        Item item = createItem(1L, "Item", "Description", user, true);
        BookingForOwnerDto lastBooking = new BookingForOwnerDto(1L,
                LocalDateTime.of(2023, Month.JULY, 27, 12, 0),
                LocalDateTime.of(2023, Month.JULY, 28, 12, 0),
                2L);
        BookingForOwnerDto nextBooking = new BookingForOwnerDto(2L,
                LocalDateTime.of(2023, Month.JULY, 29, 12, 0),
                LocalDateTime.of(2023, Month.JULY, 30, 12, 0),
                2L);
        ItemForOwnerDto itemDto = createItemForOwnersDto(1L, "Item", "Description", true,
                nextBooking, lastBooking);

        String requestBody = "{\n" +
                "    \"name\": \"item\",\n" +
                "    \"description\": \"text\",\n" +
                "    \"lastBooking\": \"" + lastBooking + "\",\n" +
                "    \"nextBooking\": \"" + nextBooking + "\",\n" +
                "    \"available\": true\n" +
                "}";
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(get("/items" + "/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", user.getId())
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))

                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.name").value("Item"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.lastBooking.id").value(1L))
                .andExpect(jsonPath("$.nextBooking.id").value(2L));
    }

    @Test
    void getItemsByUserId() {
    }

    @Test
    void findItems() throws Exception {
        String text = "Item";
        User user = createUser(1L, "Vasya", "vas@email.com");
        ItemDto dto = createItemDto(1L, "Item", "Description", true);
        List<ItemDto> items = List.of(dto);

        when(itemService.findItems(text)).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", user.getId())
                        .param("text", "Item")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Item"))
                .andExpect(jsonPath("$[0].description").value("Description"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    private ItemForOwnerDto createItemForOwnersDto(long id, String name, String description, boolean isAvailable,
                                                   BookingForOwnerDto next, BookingForOwnerDto last) {
        return ItemForOwnerDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(isAvailable)
                .nextBooking(next)
                .lastBooking(last)
                .build();
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

    private ItemDto createItemDto(long id, String name, String description, boolean isAvailable) {
        return ItemDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(isAvailable)
                .build();
    }

    private User createUser(long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }
}