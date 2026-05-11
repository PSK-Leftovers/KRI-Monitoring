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
        User mappedUser = User.builder().name("Jonas").email("jonas@example.com").role("ANALYST").build();
        User savedUser = User.builder().id(1L).name("Jonas").email("jonas@example.com").password("hashed").role("ANALYST").build();
        UserResponse expectedResponse = buildUserResponse(1L, "Jonas", "jonas@example.com", "ANALYST");

        when(userRepository.findByEmail("jonas@example.com")).thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(mappedUser);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        UserResponse result = userService.createUser(request);

        assertThat(result).isEqualTo(expectedResponse);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(argThat(u -> "hashed".equals(u.getPassword())));
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
        User jonas = User.builder().id(1L).name("Jonas").email("jonas@example.com").role("ANALYST").build();
        User mantas = User.builder().id(2L).name("Mantas").email("mantas@example.com").role("DIRECTOR").build();
        UserResponse jonasResponse = buildUserResponse(1L, "Jonas", "jonas@example.com", "ANALYST");
        UserResponse mantasResponse = buildUserResponse(2L, "Mantas", "mantas@example.com", "DIRECTOR");

        when(userRepository.findAll()).thenReturn(List.of(jonas, mantas));
        when(userMapper.toResponse(jonas)).thenReturn(jonasResponse);
        when(userMapper.toResponse(mantas)).thenReturn(mantasResponse);

        assertThat(userService.listUsers()).containsExactly(jonasResponse, mantasResponse);
    }

    @Test
    void listUsers_emptyRepository_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        assertThat(userService.listUsers()).isEmpty();
    }

    private UserResponse buildUserResponse(Long id, String name, String email, String role) {
        return new UserResponse(id, name, email, role, Instant.now(), null);
    }
}
