package ru.practicum.shareit.comment;

import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public Comment toEntity(CommentDto dto) {
        Comment comment = new Comment();
        comment.setId(dto.getId());
        comment.setText(dto.getText());
        comment.setCreated(dto.getCreated());
        return comment;
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
