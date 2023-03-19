package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserDtoById(Long userId);

    UserResponseDto saveUser(UserCreateRequestDto userCreateRequestDto);

    UserResponseDto updateUser(Long userId, UserUpdateDto userUpdateDto);

    void deleteUser(Long userId);
}
