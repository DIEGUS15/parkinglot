package com.parkingLot.config;

import com.parkingLot.entities.Role;
import com.parkingLot.entities.User;
import com.parkingLot.repositories.RoleRepository;
import com.parkingLot.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Crea roles si no existen
        Role adminRole = roleRepository.findByNombre("ADMIN")
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .nombre("ADMIN")
                            .descripcion("Administrador del sistema")
                            .build();
                    roleRepository.save(role);
                    log.info("Rol ADMIN creado exitosamente");
                    return role;
                });

        Role socioRole = roleRepository.findByNombre("SOCIO")
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .nombre("SOCIO")
                            .descripcion("Socio del parqueadero")
                            .build();
                    roleRepository.save(role);
                    log.info("Rol SOCIO creado exitosamente");
                    return role;
                });

        // Crea usuario admin si no existe
        if (!userRepository.existsByEmail("admin@mail.com")) {
            User admin = User.builder()
                    .nombre("Administrador")
                    .email("admin@mail.com")
                    .password(passwordEncoder.encode("admin"))
                    .role(adminRole)
                    .active(true)
                    .build();

            userRepository.save(admin);
            log.info("Usuario administrador creado exitosamente");
            log.info("Email: admin@mail.com");
            log.info("Password: admin");
        } else {
            log.info("Usuario administrador ya existe en la base de datos");
        }
    }
}
