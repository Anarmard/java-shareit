package ru.practicum.shareitserver.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareitserver.user.dto.UserCreateRequestDto;
import ru.practicum.shareitserver.user.dto.UserResponseDto;
import ru.practicum.shareitserver.user.mapper.UserMapperImpl;
import ru.practicum.shareitserver.user.model.User;

import java.util.List;

public class UserMapperImplTest {
    private UserMapperImpl userMapper;
    private User user;
    private UserResponseDto userResponseDto;
    private UserCreateRequestDto userCreateRequestDto;

    @BeforeEach
    void init() {
        userMapper = new UserMapperImpl();
        user = new User(1L, "John", "john.doe@mail.com");
        userCreateRequestDto = new UserCreateRequestDto(1L, "John", "john.doe@mail.com"); // owner
        userResponseDto = new UserResponseDto(1L, "John", "john.doe@mail.com");
    }

    @Test
    void toUserDtoTest() {
        UserResponseDto userResponseDtoNew = userMapper.toUserDto(user);
        Assertions.assertEquals(userResponseDto.getId(), userResponseDtoNew.getId());
    }

    @Test
    void toListUserDtoTest() {
        List<UserResponseDto> userResponseDtoNewList = userMapper.toListUserDto(List.of(user));
        Assertions.assertEquals(userResponseDto.getId(), userResponseDtoNewList.get(0).getId());
    }

    @Test
    void toUserFromUserRequestTest() {
        User userNew = userMapper.toUser(userCreateRequestDto);
        Assertions.assertEquals(user.getId(), userNew.getId());
    }

    @Test
    void toUserFromUserResponseTest() {
        User userNew = userMapper.toUser(userResponseDto, 1L);
        Assertions.assertEquals(user.getId(), userNew.getId());
    }
}
