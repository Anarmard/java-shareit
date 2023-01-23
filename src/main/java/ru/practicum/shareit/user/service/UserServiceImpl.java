package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public User getUserById(Long userId) {
        return repository.findById(userId).
                orElseThrow(() -> new NotFoundException("User is not found"));
    }

    @Override
    public User saveUser(User user) {
        return repository.save(user);
    }

    @Override
    public User updateUser(Long userId, User user) {
        if (!checkIsEmailUnique(user)) {
            throw new AlreadyExistsException("Email is not unique.");
        }

        User userFromDB = getUserById(userId);

        if (Objects.nonNull(user.getName())) {
            userFromDB.setName(user.getName());
        }
        if (Objects.nonNull(user.getEmail()) && checkIsEmailUnique(user)) {
            userFromDB.setEmail(user.getEmail());
        }
        repository.save(userFromDB);
        return userFromDB;
    }

    @Override
    public void deleteUser(Long userId) {
        repository.deleteById(userId);
    }

    public boolean checkIsEmailUnique(User user) {
        for (User u: getAllUsers()) {
            if (Objects.equals(user.getEmail(), u.getEmail()) &&
                    (!Objects.equals(user.getId(), u.getId()))) {
                return false;
            }
        }
        return true;
    }

}
