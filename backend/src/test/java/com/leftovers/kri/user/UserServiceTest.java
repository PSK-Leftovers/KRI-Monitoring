package com.leftovers.kri.user;

import com.leftovers.kri.exception.EntityAlreadyExistsException;
import com.leftovers.kri.user.dto.CreateUserRequest;
import com.leftovers.kri.user.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    UserMapper userMapper;
    @InjectMocks
    UserService userService;

    @Test
    void createUser_savesWithEncodedPassword() {
        CreateUserRequest request = new CreateUserRequest("Jonas", "jonas@example.com", "password123", "ANALYST");
        User saved = user(1L, "Jonas", "jonas@example.com", "hashed", "ANALYST");
        UserResponse response = response(1L, "Jonas", "jonas@example.com", "ANALYST");

        when(userRepository.findByEmail("jonas@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(response);

        UserResponse result = userService.createUser(request);

        assertThat(result).isEqualTo(response);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(argThat(u -> u.getPassword().equals("hashed")));
    }

    @Test
    void createUser_duplicateEmail_throwsEntityAlreadyExistsException() {
        CreateUserRequest request = new CreateUserRequest("Jonas", "jonas@example.com", "password123", "ANALYST");

        when(userRepository.findByEmail("jonas@example.com")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining("jonas@example.com");

        verify(userRepository, never()).save(any());
    }

    @Test
    void listUsers_returnsMappedList() {
        User u1 = user(1L, "Jonas", "jonas@example.com", "hashed", "ANALYST");
        User u2 = user(2L, "Mantas", "mantas@example.com", "hashed", "DIRECTOR");
        UserResponse r1 = response(1L, "Jonas", "jonas@example.com", "ANALYST");
        UserResponse r2 = response(2L, "Mantas", "mantas@example.com", "DIRECTOR");

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));
        when(userMapper.toResponse(u1)).thenReturn(r1);
        when(userMapper.toResponse(u2)).thenReturn(r2);

        assertThat(userService.listUsers()).containsExactly(r1, r2);
    }

    @Test
    void listUsers_emptyRepository_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        assertThat(userService.listUsers()).isEmpty();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private User user(Long id, String name, String email, String password, String role) {
        User u = new User();
        u.setId(id);
        u.setName(name);
        u.setEmail(email);
        u.setPassword(password);
        u.setRole(role);
        return u;
    }

    private UserResponse response(Long id, String name, String email, String role) {
        return new UserResponse(id, name, email, role, Instant.now(), null);
    }
}
