package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(Long userId);

    User saveUser(User user);

    User updateUser(Long userId, User user);

    void deleteUser(Long userId);
}
