package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentCreateRequestDto;
import ru.practicum.shareit.item.dto.CommentReponseDto;
import ru.practicum.shareit.item.model.Comment;


import java.util.List;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface CommentMapper {

    CommentReponseDto toCommentDto(Comment comment);

    List<CommentReponseDto> toListCommentDto(List<Comment> commentList);

    @Mapping(target = "item.id", source = "itemId")
    @Mapping(target = "author.id", source = "userId")
    Comment toComment(CommentCreateRequestDto commentCreateRequestDto,Long itemId, Long userId);

}
