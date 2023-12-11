package ru.practicum.shareit.comment;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@Builder
public class CommentDto {
    private long id;

    @NotNull
    private String text;

    @Positive
    private long authorId;
    private String authorName;
    private LocalDateTime created;
}
