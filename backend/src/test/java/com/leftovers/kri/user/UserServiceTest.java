package com.leftovers.kri.user;

import com.leftovers.kri.exception.EntityAlreadyExistsException;
import com.leftovers.kri.user.dto.CreateUserRequest;
import com.leftovers.kri.user.dto.UpdateUserRequest;
import com.leftovers.kri.user.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;

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
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("hashed");
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

    @Test
    void updateUser_updatesRoleAndPassword() {
        UpdateUserRequest request = new UpdateUserRequest("Jonas Updated", "jonas.updated@example.com", "newpassword", "DIRECTOR");
        User user = User.builder().id(1L).name("Jonas").email("jonas@example.com").password("old").role("ADMIN").build();
        User savedUser = User.builder().id(1L).name("Jonas Updated").email("jonas.updated@example.com").password("hashed").role("DIRECTOR").build();
        UserResponse expectedResponse = buildUserResponse(1L, "Jonas Updated", "jonas.updated@example.com", "DIRECTOR");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("jonas.updated@example.com")).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            UpdateUserRequest mappedRequest = invocation.getArgument(0);
            User mappedUser = invocation.getArgument(1);
            mappedUser.setName(mappedRequest.name());
            mappedUser.setEmail(mappedRequest.email());
            mappedUser.setRole(mappedRequest.role());
            return null;
        }).when(userMapper).updateUser(request, user);
        when(passwordEncoder.encode("newpassword")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        UserResponse result = userService.updateUser(1L, request);

        assertThat(result).isEqualTo(expectedResponse);
        verify(passwordEncoder).encode("newpassword");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getName()).isEqualTo("Jonas Updated");
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("jonas.updated@example.com");
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("hashed");
        assertThat(userCaptor.getValue().getRole()).isEqualTo("DIRECTOR");
    }

    @Test
    void updateUser_missingUser_throwsEntityNotFoundException() {
        UpdateUserRequest request = new UpdateUserRequest("Jonas Updated", "jonas.updated@example.com", "newpassword", "DIRECTOR");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(1L, request))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class)
                .hasMessageContaining("1");

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_duplicateEmail_throwsEntityAlreadyExistsException() {
        UpdateUserRequest request = new UpdateUserRequest("Jonas Updated", "mantas@example.com", "newpassword", "DIRECTOR");
        User user = User.builder().id(1L).name("Jonas").email("jonas@example.com").password("old").role("ADMIN").build();
        User conflictingUser = User.builder().id(2L).email("mantas@example.com").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("mantas@example.com")).thenReturn(Optional.of(conflictingUser));

        assertThatThrownBy(() -> userService.updateUser(1L, request))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining("mantas@example.com");

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_withoutPassword_keepsExistingPassword() {
        UpdateUserRequest request = new UpdateUserRequest("Jonas Updated", "jonas.updated@example.com", null, "DIRECTOR");
        User user = User.builder().id(1L).name("Jonas").email("jonas@example.com").password("old").role("ADMIN").build();
        User savedUser = User.builder().id(1L).name("Jonas Updated").email("jonas.updated@example.com").password("old").role("DIRECTOR").build();
        UserResponse expectedResponse = buildUserResponse(1L, "Jonas Updated", "jonas.updated@example.com", "DIRECTOR");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("jonas.updated@example.com")).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            UpdateUserRequest mappedRequest = invocation.getArgument(0);
            User mappedUser = invocation.getArgument(1);
            mappedUser.setName(mappedRequest.name());
            mappedUser.setEmail(mappedRequest.email());
            mappedUser.setRole(mappedRequest.role());
            return null;
        }).when(userMapper).updateUser(request, user);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        UserResponse result = userService.updateUser(1L, request);

        assertThat(result).isEqualTo(expectedResponse);
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void deleteUser_deletesExistingUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_missingUser_throwsEntityNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class)
                .hasMessageContaining("1");

        verify(userRepository, never()).deleteById(any());
    }

    private UserResponse buildUserResponse(Long id, String name, String email, String role) {
        return new UserResponse(id, name, email, role, Instant.now(), null);
    }
}
