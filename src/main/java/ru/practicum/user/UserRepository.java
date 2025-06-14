package ru.practicum.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    List<User> findAll();

    void delete(Long id);

    boolean existsByEmail(String email);

    boolean existsById(Long id);

    Optional<User> findByEmail(String email);
}
