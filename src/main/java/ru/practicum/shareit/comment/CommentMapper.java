package ru.practicum.shareit.comment;

import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public Comment toEntity(CommentDto dto) {
        return Comment.builder()
                .id(dto.getId())
                .text(dto.getText())
                .created(dto.getCreated())
                .build();
    }

    public CommentDto toDto(Comment entity) {
        return CommentDto.builder()
                .id(entity.getId())
                .text(entity.getText())
                .authorId(entity.getAuthor().getId())
                .authorName(entity.getAuthor().getName())
                .created(entity.getCreated())
                .build();
    }
}
