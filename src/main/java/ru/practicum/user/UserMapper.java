package ru.practicum.user;

import org.springframework.stereotype.Component;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserResponseDto;

@Component
public class UserMapper {
    public User toUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public UserResponseDto toUserResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
