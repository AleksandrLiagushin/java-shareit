package ru.practicum.shareit.comment;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private final long id;

    @NotNull
    private final String text;

    @Positive
    private final long authorId;
    private final String authorName;
    private final LocalDateTime created;
}
