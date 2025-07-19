package ru.practicum.item;

import org.mapstruct.*;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.user.User;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "authorName", source = "author.name")
    @Mapping(target = "created", source = "created")
    CommentDto toDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "item", ignore = true)
    Comment toEntity(CommentDto commentDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    Comment toEntity(CommentDto commentDto, User author, Item item);

    @Named("mapCommentList")
    default List<CommentDto> mapCommentList(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        return comments.stream()
                .map(this::toDto)
                .toList();
    }
}

/*package ru.practicum.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.user.User;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "authorName", source = "author", qualifiedByName = "mapAuthorName")
    CommentDto toDto(Comment comment);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "created", ignore = true)
    Comment toEntity(CommentDto commentDto);

    @Named("mapAuthorName")
    default String mapAuthorName(User author) {
        if (author == null) {
            return null;
        }
        return author.getName();
    }
}
*/