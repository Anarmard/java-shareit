package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;

import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

// добавили в pom зависимости с mapstruct (в 4-х местах) поэтому можем использовать данный функционал
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toUserDto(User user);
    // mapstruct сам генерит необходимый код для преобразования User в UserDto

    List<UserResponseDto> toListUserDto(List<User> userList);

    User toUser(UserCreateRequestDto userDto);

    @Mapping(target = "id", source = "userId")
    User toUser(UserUpdateDto userDto, Long userId);

    @Mapping(target = "id", source = "userId")
    User toUser(UserResponseDto userResponseDto, Long userId);
}
