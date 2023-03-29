package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserCreateRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.nio.charset.StandardCharsets;
import java.util.List;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private final UserCreateRequestDto userCreateRequestDto = new UserCreateRequestDto(
            1L,
            "John",
            "john.doe@mail.com");

    private final UserResponseDto userResponseDto = new UserResponseDto(
            1L,
            "John",
            "john.doe@mail.com");

    // тестируем метод getAllUsers
    @Test
    void getAllUsersTest() throws Exception {
        Mockito.when(userService.getAllUsers())
                .thenReturn(List.of(userResponseDto));
        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].name", Matchers.is(userResponseDto.getName()), String.class))
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1))
                .getAllUsers();
    }

    // тестируем метод getUserById
    @Test
    void getUserByIdTest() throws Exception {
        Mockito.when(userService.getUserDtoById(anyLong()))
                .thenReturn(userResponseDto);
        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(userResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(userResponseDto.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(userResponseDto.getEmail()), String.class))
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1))
                .getUserDtoById(anyLong());
    }

    // тестируем метод saveNewUser
    @Test
    void saveNewUserTest() throws Exception {
        Mockito.when(userService.saveUser(any()))
                .thenReturn(userResponseDto);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userCreateRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(userResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(userResponseDto.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(userResponseDto.getEmail()), String.class))
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1))
                .saveUser(any());
    }

    // тестируем метод updateUser
    @Test
    void updateUserTest() throws Exception {
        Mockito.when(userService.updateUser(anyLong(), any()))
                .thenReturn(userResponseDto);
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userCreateRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(userResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(userResponseDto.getName()), String.class))
                .andExpect(jsonPath("$.email", Matchers.is(userResponseDto.getEmail()), String.class))
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1))
                .updateUser(anyLong(), any());
    }

    // тестируем метод deleteUser
    @Test
    void deleteUserTest() throws Exception {
        mvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
