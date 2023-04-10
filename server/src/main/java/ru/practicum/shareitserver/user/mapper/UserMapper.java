package ru.practicum.shareitserver.user.mapper;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;

import ru.practicum.shareitserver.user.dto.UserCreateRequestDto;
import ru.practicum.shareitserver.user.dto.UserResponseDto;
import ru.practicum.shareitserver.user.model.User;

import java.util.List;

// добавили в pom зависимости с mapstruct (в 4-х местах) поэтому можем использовать данный функционал
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toUserDto(User user);
    // mapstruct сам генерит необходимый код для преобразования User в UserDto

    List<UserResponseDto> toListUserDto(List<User> userList);

    User toUser(UserCreateRequestDto userDto);

    @Mapping(target = "id", source = "userId")
    User toUser(UserResponseDto userDto, Long userId);
}
