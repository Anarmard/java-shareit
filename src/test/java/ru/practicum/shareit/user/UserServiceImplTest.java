package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserServiceImpl userService;

    private User user1;
    private User userForUpdateMistake;
    private UserResponseDto userResponseDto1;
    private UserCreateRequestDto userCreateRequestDto1;

    @BeforeEach
    void init() {
        userService = new UserServiceImpl(userRepository, userMapper);

        // User
        user1 = new User(1L, "John", "john.doe@mail.com");
        userCreateRequestDto1 = new UserCreateRequestDto(1L, "John", "john.doe@mail.com"); // owner
        userResponseDto1 = new UserResponseDto(1L, "John", "john.doe@mail.com");

        userForUpdateMistake = new User(5L, "Jony", "john.doe@mail.com");
    }

    @Test
    void getAllUsersTest() {
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user1));
        Mockito.when(userMapper.toListUserDto(List.of(user1)))
                .thenReturn(List.of(userResponseDto1));
        Assertions.assertEquals(List.of(userResponseDto1), userService.getAllUsers());
    }

    @Test
    void getUserDtoByIdTest() {
        Mockito.when(userRepository.findById(2L))
                .thenReturn(Optional.empty());
        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> userService.getUserDtoById(2L));
        Assertions.assertEquals("User is not found", e.getMessage());
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));
        Mockito.when(userMapper.toUserDto(user1))
                .thenReturn(userResponseDto1);
        Assertions.assertEquals(userResponseDto1, userService.getUserDtoById(1L));
    }

    @Test
    void saveUserTest() {
        Mockito.when(userMapper.toUser(userCreateRequestDto1))
                .thenReturn(user1);
        Mockito.when(userMapper.toUserDto(user1))
                .thenReturn(userResponseDto1);
        Assertions.assertEquals(userResponseDto1, userService.saveUser(userCreateRequestDto1));

        Mockito.verify(userRepository, Mockito.times(1))
                .save(user1);
    }

    @Test
    void updateUserTest() {
        Mockito.when(userMapper.toUser(userResponseDto1, 1L))
                .thenReturn(user1);
        Mockito.when(userRepository.findUserByEmail(any()))
                .thenReturn(userForUpdateMistake);
        Exception e = Assertions.assertThrows(AlreadyExistsException.class,
                () -> userService.updateUser(1L, userResponseDto1));
        Assertions.assertEquals("Email is not unique.", e.getMessage());
        Mockito.when(userRepository.findUserByEmail(any()))
                .thenReturn(null);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));
        Mockito.when(userMapper.toUserDto(user1))
                .thenReturn(userResponseDto1);
        Assertions.assertEquals(userResponseDto1, userService.updateUser(1L, userResponseDto1));
    }

    @Test
    void deleteUserTest() {
        userService.deleteUser(1L);
        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(1L);
    }
}
