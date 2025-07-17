package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserUpdateDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/gateway/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @PathVariable long userId,
            @Valid @RequestBody UserUpdateDto userUpdateDto) {
        return userClient.update(userId, userUpdateDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        return userClient.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers(
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        return userClient.getAll(from, size);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable long userId) {
        userClient.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
