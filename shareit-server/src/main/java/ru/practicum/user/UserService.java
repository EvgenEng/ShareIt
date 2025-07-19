package ru.practicum.user;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.dto.UserResponseDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {

    UserResponseDto save(UserDto userDto);

    UserResponseDto update(Long userId, UserUpdateDto userUpdateDto);

    UserResponseDto getById(Long id);

    List<UserResponseDto> getAll(Integer from, Integer size); // Измененная сигнатура

    @Transactional(readOnly = true)
    List<UserResponseDto> getAll();

    void delete(Long id);
}
