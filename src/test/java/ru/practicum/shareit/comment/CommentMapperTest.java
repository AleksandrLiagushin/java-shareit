package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {
    private final CommentMapper commentMapper = new CommentMapper();

    @Test
    void toEntity() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("text");
        comment.setCreated(LocalDateTime.of(2023, Month.JULY, 27, 12, 0));

        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("text")
                .created(LocalDateTime.of(2023, Month.JULY, 27, 12, 0))
                .build();

        Comment actual = commentMapper.toEntity(dto);

        assertEquals(comment, actual);
    }

    @Test
    void toDto() {
        User user = createUser(2L, "Vanya", "van@email.com");
        Item item = createItem(1L, "Item", "Description", user, true);
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("text");
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.of(2023, Month.JULY, 27, 12, 0));

        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorId(user.getId())
                .authorName(user.getName())
                .created(LocalDateTime.of(2023, Month.JULY, 27, 12, 0))
                .build();

        CommentDto actual = commentMapper.toDto(comment);

        assertEquals(dto, actual);
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
}