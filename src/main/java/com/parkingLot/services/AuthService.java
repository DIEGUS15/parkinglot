package com.parkingLot.services;

import com.parkingLot.dto.AuthResponse;
import com.parkingLot.dto.LoginRequest;
import com.parkingLot.dto.RegisterRequest;
import com.parkingLot.entities.Role;
import com.parkingLot.entities.User;
import com.parkingLot.exceptions.BadRequestException;
import com.parkingLot.repositories.RoleRepository;
import com.parkingLot.repositories.UserRepository;
import com.parkingLot.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Email o contraseña incorrectos"));

        if (!user.isActive()) {
            throw new BadRequestException("El usuario está inactivo");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().getNombre());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .nombre(user.getNombre())
                .role(user.getRole().getNombre())
                .message("Login exitoso")
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("Debe estar autenticado para registrar usuarios");
        }

        User adminUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        if (!adminUser.getRole().getNombre().equals("ADMIN")) {
            throw new BadRequestException("Solo los administradores pueden registrar usuarios");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }

        Role socioRole = roleRepository.findByNombre("SOCIO")
                .orElseThrow(() -> new BadRequestException("Rol SOCIO no encontrado"));

        User newUser = User.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(socioRole)
                .active(true)
                .build();

        userRepository.save(newUser);

        String token = jwtUtil.generateToken(newUser.getEmail(), newUser.getRole().getNombre());

        return AuthResponse.builder()
                .token(token)
                .email(newUser.getEmail())
                .nombre(newUser.getNombre())
                .role(newUser.getRole().getNombre())
                .message("Usuario registrado exitosamente")
                .build();
    }

    @Transactional
    public AuthResponse logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BadRequestException("No hay una sesión activa");
        }

        SecurityContextHolder.clearContext();

        return AuthResponse.builder()
                .message("Logout exitoso")
                .build();
    }
}
