package ru.practicum.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ErrorResponse;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        return userService.save(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @RequestBody Map<String, String> updates) {

        User currentUser = userService.getById(userId);

        if (updates.containsKey("name")) {
            currentUser.setName(updates.get("name"));
        }

        if (updates.containsKey("email")) {
            currentUser.setEmail(updates.get("email"));
        }

        try {
            User updatedUser = userService.update(currentUser);
            return ResponseEntity.ok(updatedUser);
        } catch (ConflictException e) {
            // Возвращаем статус 409 с сообщением об ошибке
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    public User getById(@PathVariable Long userId) {
        return userService.getById(userId);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
