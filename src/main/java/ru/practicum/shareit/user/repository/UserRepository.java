package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepository {
    private static Long idUser = 0L;
    private static final Map<Long, User> ID_USER_MAP = new HashMap<>();

    public List<User> findAll() {
        return new ArrayList<>(ID_USER_MAP.values());
    }

    public User save(User user) {
        if (!checkIsEmailUnique(user)) {
            throw new AlreadyExistsException("Email is not unique.");
        } else {
            idUser++;
            user.setId(idUser);
            ID_USER_MAP.put(idUser, user);
            return user;
        }
    }

    public User update(Long userId, User user) {
        if (!checkIsEmailUnique(user)) {
            throw new AlreadyExistsException("Email is not unique.");
        }
        User savedUser = findUserById(userId);
        ID_USER_MAP.remove(userId);
        if (Objects.nonNull(user.getName())) {
            savedUser.setName(user.getName());
        }
        if (Objects.nonNull(user.getEmail()) && checkIsEmailUnique(user)) {
            savedUser.setEmail(user.getEmail());
        }
        ID_USER_MAP.put(userId, savedUser);
        return savedUser;
    }

    public void deleteUser(Long userId) {
        ID_USER_MAP.remove(userId);
    }

    public User findUserById(Long userId) {
        if (ID_USER_MAP.containsKey(userId)) {
            return ID_USER_MAP.get(userId);
        } else {
            throw new NotFoundException("User is not found");
        }
    }

    public boolean checkIsEmailUnique(User user) {
        for (User u: ID_USER_MAP.values()) {
            if (Objects.equals(user.getEmail(), u.getEmail()) &&
                    (!Objects.equals(user.getId(), u.getId()))) {
                return false;
            }
        }
        return true;
    }
}
