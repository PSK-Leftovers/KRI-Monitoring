package com.leftovers.kri.user;

import com.leftovers.kri.exception.EntityAlreadyExistsException;
import com.leftovers.kri.user.dto.CreateUserRequest;
import com.leftovers.kri.user.dto.UpdateUserRequest;
import com.leftovers.kri.user.dto.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
/**
 * Singleton service with no mutable per-user state.
 *
 * The Spring container keeps one instance of this bean, but the service only uses
 * method-local variables and injected collaborators. That keeps RAM usage lean because
 * no user-specific data is retained between calls, and it supports multi-tab concurrency
 * because concurrent requests never share mutable instance state. Once a method finishes,
 * its temporary objects become eligible for garbage collection.
 */
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByRole(String role) {
        return userRepository.findAllByRole(role);
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EntityAlreadyExistsException("User with email " + request.email() + " already exists");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));

        return userMapper.toResponse(userRepository.save(user));
    }

    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for update id={}", id);
                    return new EntityNotFoundException("User not found with id: " + id);
                });

        userRepository.findByEmail(request.email())
                .filter(foundUser -> !foundUser.getId().equals(id))
                .ifPresent(foundUser -> {
                    throw new EntityAlreadyExistsException("User with email " + request.email() + " already exists");
                });

        userMapper.updateUser(request, user);

        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            log.warn("User not found for deletion id={}", id);
            throw new EntityNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
    }
}
