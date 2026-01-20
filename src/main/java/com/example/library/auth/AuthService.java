
package com.example.library.auth;

import com.example.library.auth.dto.AuthLoginRequest;
import com.example.library.auth.dto.AuthRegisterRequest;
import com.example.library.security.JwtService;
import com.example.library.security.Role;
import com.example.library.user.User;
import com.example.library.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public String registerMember(AuthRegisterRequest req) {
        String email = req.getEmail().toLowerCase().trim();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        User u = new User();
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRole(Role.MEMBER);
        u = userRepository.save(u);
        return jwtService.generateToken(u.getId(), u.getEmail(), u.getRole());
    }

    @Transactional(readOnly = true)
    public String login(AuthLoginRequest req) {
        String email = req.getEmail().toLowerCase().trim();
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), u.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return jwtService.generateToken(u.getId(), u.getEmail(), u.getRole());
    }
}
