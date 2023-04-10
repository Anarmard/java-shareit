package ru.practicum.shareitserver.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitserver.exception.ErrorHandler;
import ru.practicum.shareitserver.user.dto.UserCreateRequestDto;
import ru.practicum.shareitserver.user.dto.UserResponseDto;
import ru.practicum.shareitserver.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController extends ErrorHandler {
    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserById(@PathVariable Long userId) {
        return userService.getUserDtoById(userId);
    }

    @PostMapping
    public UserResponseDto saveNewUser(@Valid @RequestBody UserCreateRequestDto userCreateRequestDto) {
        return userService.saveUser(userCreateRequestDto);
    }

    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(@RequestBody UserResponseDto userResponseDto,
                                      @PathVariable Long userId) {
        return userService.updateUser(userId, userResponseDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
