package ru.practicum.user;

import org.springframework.stereotype.Service;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Set<String> emailSet = new HashSet<>();

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        initializeEmailSet();
    }

    private void initializeEmailSet() {
        userRepository.findAll().stream()
                .map(User::getEmail)
                .forEach(emailSet::add);
    }

    @Override
    public User save(User user) {
        if (emailSet.contains(user.getEmail())) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
        User savedUser = userRepository.save(user);
        emailSet.add(savedUser.getEmail());
        return savedUser;
    }

    @Override
    public User update(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            if (emailSet.contains(user.getEmail())) {
                throw new ConflictException(
                        String.format("Email '%s' уже используется другим пользователем", user.getEmail())
                );
            }
            emailSet.remove(existingUser.getEmail());
            existingUser.setEmail(user.getEmail());
            emailSet.add(user.getEmail());
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        emailSet.remove(user.getEmail());
        userRepository.delete(id);
    }
}
