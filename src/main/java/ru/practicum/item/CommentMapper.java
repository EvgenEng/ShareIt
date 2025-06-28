package ru.practicum.item;

import org.springframework.stereotype.Component;
import ru.practicum.item.dto.CommentDto;

@Component
public class CommentMapper {
    public static CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setCreated(comment.getCreated());

        if (comment.getAuthor() != null) {
            dto.setAuthorName(comment.getAuthor().getName());
        }

        return dto;
    }

    public Comment toEntity(CommentDto commentDto) {
        if (commentDto == null) {
            return null;
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        return comment;
    }
}
