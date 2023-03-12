package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentCreateRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;


import java.util.List;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface CommentMapper {

    @Mapping(target = "authorName", source = "author.name")
    CommentResponseDto toCommentDto(Comment comment);

    List<CommentResponseDto> toListCommentDto(List<Comment> commentList);

    @Mapping(target = "item", ignore = true)
    @Mapping(target = "author", ignore = true)
    Comment toComment(CommentCreateRequestDto commentCreateRequestDto);

}
