package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class UserRepository {
    private static final List<User> ALL_USERS = new ArrayList<>();
    private static long idUser = 0L;

    public List<User> findAll() {
        return ALL_USERS;
    }

    public User save(User user) {
        if (!checkIsEmailUnique(user)) {
            throw new AlreadyExistsException("Email is not unique.");
        } else {
            idUser++;
            user.setId(idUser);
            ALL_USERS.add(user);
            return user;
        }
    }

    public User update(Long userId, User user) {
        if (!checkIsEmailUnique(user)) {
            throw new AlreadyExistsException("Email is not unique.");
        }
        User savedUser = findUserById(userId);
        if (Objects.isNull(savedUser)) {
            return null;
        }
        ALL_USERS.remove(savedUser);
        if (Objects.nonNull(user.getName())) {
            savedUser.setName(user.getName());
        }
        if (Objects.nonNull(user.getEmail()) && checkIsEmailUnique(user)) {
            savedUser.setEmail(user.getEmail());
        }
        ALL_USERS.add(savedUser);
        return savedUser;
    }

    public void deleteUser(Long userId) {
        for (int id = 1; id <= ALL_USERS.size(); id++) {
            User currentUser = ALL_USERS.get(id - 1);
            if (userId == currentUser.getId()) {
                ALL_USERS.remove(currentUser);
            }
        }
    }

    public User findUserById(Long userId) {
        for (int id = 1; id <= ALL_USERS.size(); id++) {
            if (userId == ALL_USERS.get(id - 1).getId()) {
                return ALL_USERS.get(id - 1);
            }
        }
        throw new NotFoundException("User is not found");
    }

    public boolean checkIsEmailUnique(User user) {
        for (int id = 1; id <= ALL_USERS.size(); id++) {
            if (Objects.equals(user.getEmail(), ALL_USERS.get(id - 1).getEmail())) {
                return false;
            }
        }
        return true;
    }
}
