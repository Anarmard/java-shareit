package ru.practicum.shareitserver.user.service;

import ru.practicum.shareitserver.user.dto.UserCreateRequestDto;
import ru.practicum.shareitserver.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserDtoById(Long userId);

    UserResponseDto saveUser(UserCreateRequestDto userCreateRequestDto);

    UserResponseDto updateUser(Long userId, UserResponseDto userResponseDto);

    void deleteUser(Long userId);
}
