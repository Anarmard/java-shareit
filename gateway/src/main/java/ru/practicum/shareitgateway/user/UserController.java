package ru.practicum.shareitgateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.user.dto.UserRequestDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("get all users");
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("get userId={}", userId);
        return userClient.getById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> saveNewUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        log.info("save new user {}", userRequestDto);
        return userClient.save(userRequestDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserRequestDto userRequestDto,
                                      @PathVariable Long userId) {
        log.info("update userId={} by user {}", userId, userRequestDto);
        return userClient.update(userId, userRequestDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("delete userId={}", userId);
        return userClient.deleteById(userId);
    }
}
