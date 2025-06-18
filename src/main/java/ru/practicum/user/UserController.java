package ru.practicum.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ErrorResponse;

import jakarta.validation.Valid;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserResponseDto;
import ru.practicum.user.dto.UserUpdateDto;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto create(@Valid @RequestBody UserDto userDto) {
        User user = userMapper.toUser(userDto);
        User createdUser = userService.save(user);
        return userMapper.toUserResponseDto(createdUser);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateDto userUpdateDto) {

        User currentUser = userService.getById(userId);

        if (userUpdateDto.getName() != null) {
            currentUser.setName(userUpdateDto.getName());
        }

        if (userUpdateDto.getEmail() != null) {
            currentUser.setEmail(userUpdateDto.getEmail());
        }

        try {
            User updatedUser = userService.update(currentUser);
            return ResponseEntity.ok(userMapper.toUserResponseDto(updatedUser));
        } catch (ConflictException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    public UserResponseDto getById(@PathVariable Long userId) {
        User user = userService.getById(userId);
        return userMapper.toUserResponseDto(user);
    }

    @GetMapping
    public List<UserResponseDto> getAll() {
        return userService.getAll().stream()
                .map(userMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
