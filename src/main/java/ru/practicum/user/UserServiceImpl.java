package ru.practicum.user;

import org.springframework.stereotype.Service;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getEmail() != null) {
            userRepository.findByEmail(user.getEmail())
                    .ifPresent(otherUser -> {
                        if (!otherUser.getId().equals(user.getId())) {
                            throw new ConflictException(
                                    String.format("Email '%s' already used by user %d",
                                            user.getEmail(), otherUser.getId())
                            );
                        }
                    });
            existingUser.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }

        return userRepository.save(existingUser);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        userRepository.delete(id);
    }
}
