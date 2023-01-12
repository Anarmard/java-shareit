package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController extends ErrorHandler {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        List<User> returnedListUser = userService.getAllUsers();
        return userMapper.toListUserDto(returnedListUser);
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserById(@PathVariable Long userId) {
        User returnedUser = userService.getUserById(userId);
        return userMapper.toUserDto(returnedUser);
    }

    @PostMapping
    public UserResponseDto saveNewUser(@Valid @RequestBody UserCreateRequestDto userCreateRequestDto) {
        User currentUser = userMapper.toUser(userCreateRequestDto);
        User returnedUser = userService.saveUser(currentUser);
        return userMapper.toUserDto(returnedUser);
    }

    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(@RequestBody UserUpdateDto userUpdateDto,
                                      @PathVariable Long userId) {
        User currentUser = userMapper.toUser(userUpdateDto, userId);
        User returnedUser = userService.updateUser(userId, currentUser);
        return userMapper.toUserDto(returnedUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
