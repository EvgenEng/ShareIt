package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserResponseDto;
import ru.practicum.user.dto.UserUpdateDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserServerController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@Valid @RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateDto userUpdateDto) {
        return userService.update(userId, userUpdateDto);
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserById(@PathVariable Long userId) {
        return userService.getById(userId);
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers(
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        return userService.getAll(from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
