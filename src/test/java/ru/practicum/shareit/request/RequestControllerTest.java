package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {
    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private RequestController requestController;

    @Mock
    private RequestService requestService;

    @BeforeEach
    void setupStandalone() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(requestController)
                .build();
    }

    @Test
    void createRequest() throws Exception {
        User user = createUser(0L, "Vasya", "vas@mail.ru");
        Request request = createRequest(1L, "text", user);
        request.setCreated(LocalDateTime.now().minusMonths(1));

        Mockito.when(requestService.create(any(), anyLong())).thenReturn(request);

        String requestBody = "{\n" +
                "    \"id\": \"" + 1 + "\",\n" +
                "    \"created\": \"" + LocalDateTime.now().minusMonths(1) + "\",\n" +
                "    \"description\": \"text\"\n" +
                "}";

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(requestBody)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$..created[2]").value(LocalDateTime.now().minusMonths(1).getDayOfMonth()))
                .andExpect(jsonPath("$.description").value("text"));

        Mockito.verify(requestService, times(1)).create(any(), anyLong());
        Mockito.verifyNoMoreInteractions(requestService);
    }

    @Test
    void findRequestById() throws Exception {
        RequestDto dto = createRequestDto(1L, "text", 1L);
        dto.setCreated(LocalDateTime.now().minusMonths(1));

        Mockito.when(requestService.findRequestById(anyLong(), anyLong())).thenReturn(dto);

        mockMvc.perform(get("/requests" + "/{requestId}", dto.getId())
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$..created[2]").value(LocalDateTime.now().minusMonths(1).getDayOfMonth()))
                .andExpect(jsonPath("$.description").value("text"));
    }

    @Test
    void getRequest() throws Exception {
        RequestDto dto = createRequestDto(1L, "text", 1L);
        dto.setCreated(LocalDateTime.now().minusMonths(1));

        Mockito.when(requestService.getRequests(anyLong())).thenReturn(List.of(dto));
        List<RequestDto> result = requestService.getRequests(anyLong());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]id").value(result.get(0).getId()))
                .andExpect(jsonPath("$..created[2]").value(result.get(0).getCreated().getDayOfMonth()))
                .andExpect(jsonPath("$.[0]description").value(result.get(0).getDescription()));
    }

    @Test
    void getAllRequests() throws Exception {
        RequestDto dto = createRequestDto(1L, "text", 1L);
        dto.setCreated(LocalDateTime.now().minusMonths(1));

        Mockito.when(requestService.getAllRequests(anyLong(), any())).thenReturn(List.of(dto));
        List<RequestDto> result = requestService.getAllRequests(1L,
                PageRequest.of(0, 1, Sort.by("created").descending()));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0", "size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]id").value(result.get(0).getId()))
                .andExpect(jsonPath("$..created[2]").value(result.get(0).getCreated().getDayOfMonth()))
                .andExpect(jsonPath("$.[0]description").value(result.get(0).getDescription()));
    }

    private User createUser(long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    private Request createRequest(long id, String text, User user) {
        Request request = new Request();
        request.setId(id);
        request.setDescription(text);
        request.setUser(user);
        return request;
    }

    private RequestDto createRequestDto(long id, String text, long userId) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setDescription(text);
        requestDto.setRequester(userId);
        return requestDto;
    }
}