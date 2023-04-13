package ru.practicum.shareitserver.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareitserver.item.mapper.CommentMapperImpl;
import ru.practicum.shareitserver.item.dto.CommentCreateRequestDto;
import ru.practicum.shareitserver.item.dto.CommentResponseDto;
import ru.practicum.shareitserver.item.dto.ItemCreateRequestDto;
import ru.practicum.shareitserver.item.dto.ItemResponseDto;
import ru.practicum.shareitserver.item.model.Comment;
import ru.practicum.shareitserver.item.model.Item;
import ru.practicum.shareitserver.request.model.ItemRequest;
import ru.practicum.shareitserver.user.dto.UserCreateRequestDto;
import ru.practicum.shareitserver.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class CommentMapperImplTest {
    private CommentMapperImpl commentMapper;
    private Item item;
    private ItemCreateRequestDto itemCreateRequestDto;
    private ItemResponseDto itemResponseDto;
    private CommentResponseDto commentResponseDto;
    private CommentCreateRequestDto commentCreateRequestDto;
    private Comment comment;

    @BeforeEach
    void init() {
        commentMapper = new CommentMapperImpl();

        User user1 = new User(1L, "John", "john.doe@mail.com");
        User user2 = new User(2L, "Bill", "bill.doe@mail.com");
        UserCreateRequestDto userCreateRequestDto1 = new UserCreateRequestDto(1L, "John", "john.doe@mail.com"); // owner
        UserCreateRequestDto userCreateRequestDto2 = new UserCreateRequestDto(2L, "Bill", "bill.doe@mail.com"); // booker

        // ItemRequest
        ItemRequest itemRequest = new ItemRequest(1L, "need drill", user2,
                LocalDateTime.of(2023, 1, 28, 2, 0));

        // Comment
        comment = new Comment(1L, "work well", item, user2,
                LocalDateTime.of(2023, 3, 28, 2,0));
        commentResponseDto = new CommentResponseDto(1L, "work well",
                itemResponseDto,
                "Bill",
                LocalDateTime.of(2023, 3, 28, 2,0));
        commentCreateRequestDto = new CommentCreateRequestDto(1L, "work well",
                itemCreateRequestDto,
                userCreateRequestDto2,
                LocalDateTime.of(2023, 3, 28, 2,0));

        // Item
        item = new Item(1L,"drill","drill makita",true, user1, itemRequest);
        itemCreateRequestDto = new ItemCreateRequestDto(
                1L,"drill","drill makita",true, userCreateRequestDto1, 1L);
        itemResponseDto = new ItemResponseDto(
                1L,"drill","drill makita",true, userCreateRequestDto1, 1L);
    }

    @Test
    void toCommentDtoTest() {
        Assertions.assertNull(commentMapper.toCommentDto(null));
        CommentResponseDto commentResponseDtoNew = commentMapper.toCommentDto(comment);
        Assertions.assertEquals(commentResponseDto.getId(), commentResponseDtoNew.getId());
        Assertions.assertEquals(commentResponseDto.getItem(), commentResponseDtoNew.getItem());
    }

    @Test
    void toListCommentDtoTest() {
        Assertions.assertNull(commentMapper.toListCommentDto(null));
        List<CommentResponseDto> commentResponseDtoNewList = commentMapper.toListCommentDto(List.of(comment));
        Assertions.assertEquals(commentResponseDto.getId(), commentResponseDtoNewList.get(0).getId());
    }

    @Test
    void toCommentTest() {
        Assertions.assertNull(commentMapper.toComment(null));
        Comment commentNew = commentMapper.toComment(commentCreateRequestDto);
        Assertions.assertEquals(comment.getId(), commentNew.getId());
    }
}
