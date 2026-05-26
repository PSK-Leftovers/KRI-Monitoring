package com.leftovers.kri.auth;

import com.leftovers.kri.auth.dto.LoginRequest;
import com.leftovers.kri.auth.dto.LoginResponse;
import com.leftovers.kri.logging.Audited;
import com.leftovers.kri.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Audited(action = "USER_LOGIN")
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest http) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession session = http.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        String email = authentication.getName();

        userRepository.findByEmail(email).ifPresent(user -> {
            user.setLastLogin(Instant.now());
            userRepository.save(user);
        });

        String role = Objects.requireNonNull(authentication.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority())
                .replace("ROLE_", "");

        return new LoginResponse(email, role);
    }
}
