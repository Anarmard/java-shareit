package ru.practicum.shareitserver.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareitserver.exception.AlreadyExistsException;
import ru.practicum.shareitserver.exception.NotFoundException;
import ru.practicum.shareitserver.user.dto.UserCreateRequestDto;
import ru.practicum.shareitserver.user.dto.UserResponseDto;
import ru.practicum.shareitserver.user.mapper.UserMapper;
import ru.practicum.shareitserver.user.model.User;
import ru.practicum.shareitserver.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userMapper.toListUserDto(repository.findAll());
    }

    @Override
    public UserResponseDto getUserDtoById(Long userId) {
        return userMapper.toUserDto(getUserById(userId));
    }

    private User getUserById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User is not found"));
    }

    @Override
    public UserResponseDto saveUser(UserCreateRequestDto userCreateRequestDto) {
        User user = userMapper.toUser(userCreateRequestDto);
        repository.save(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserResponseDto updateUser(Long userId, UserResponseDto userResponseDto) {
        User user = userMapper.toUser(userResponseDto, userId);

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
        return userMapper.toUserDto(userFromDB);
    }

    @Override
    public void deleteUser(Long userId) {
        repository.deleteById(userId);
    }

    public boolean checkIsEmailUnique(User user) {
        User u = repository.findUserByEmail(user.getEmail());
        if (u == null) {
            // не нашли юзера с таким email
            return true;
        } else {
            // нашли юзера с таким email, но теперь проверяем id - если одинаковые, значит это один и тот же user
            return Objects.equals(user.getId(), u.getId());
        }
    }
}
