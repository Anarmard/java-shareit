package ru.practicum.shareitserver.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareitserver.item.dto.CommentCreateRequestDto;
import ru.practicum.shareitserver.item.dto.CommentResponseDto;
import ru.practicum.shareitserver.item.model.Comment;


import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "authorName", source = "author.name")
    CommentResponseDto toCommentDto(Comment comment);

    List<CommentResponseDto> toListCommentDto(List<Comment> commentList);

    @Mapping(target = "item", ignore = true)
    @Mapping(target = "author", ignore = true)
    Comment toComment(CommentCreateRequestDto commentCreateRequestDto);

}
