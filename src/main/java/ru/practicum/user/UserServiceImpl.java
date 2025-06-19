package ru.practicum.user;

import org.springframework.stereotype.Service;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserResponseDto;
import ru.practicum.user.dto.UserUpdateDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Set<String> emailSet = new HashSet<>();

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        initializeEmailSet();
    }

    @Override
    public UserResponseDto save(UserDto userDto) {
        if (emailSet.contains(userDto.getEmail())) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
        User user = userMapper.toUser(userDto);
        User savedUser = userRepository.save(user);
        emailSet.add(savedUser.getEmail());
        return userMapper.toUserResponseDto(savedUser);
    }

    @Override
    public UserResponseDto update(Long userId, UserUpdateDto userUpdateDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (userUpdateDto.getName() != null) {
            existingUser.setName(userUpdateDto.getName());
        }

        if (userUpdateDto.getEmail() != null && !userUpdateDto.getEmail().equals(existingUser.getEmail())) {
            if (emailSet.contains(userUpdateDto.getEmail())) {
                throw new ConflictException(
                        String.format("Email '%s' уже используется другим пользователем", userUpdateDto.getEmail())
                );
            }
            emailSet.remove(existingUser.getEmail());
            existingUser.setEmail(userUpdateDto.getEmail());
            emailSet.add(userUpdateDto.getEmail());
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toUserResponseDto(updatedUser);
    }

    @Override
    public UserResponseDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        emailSet.remove(user.getEmail());
        userRepository.delete(id);
    }

    private void initializeEmailSet() {
        userRepository.findAll().stream()
                .map(User::getEmail)
                .forEach(emailSet::add);
    }
}
