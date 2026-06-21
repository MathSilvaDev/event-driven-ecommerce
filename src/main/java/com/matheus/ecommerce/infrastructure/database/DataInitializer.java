package com.matheus.ecommerce.infrastructure.database;

import com.matheus.ecommerce.domain.auth.entity.Role;
import com.matheus.ecommerce.domain.auth.enums.RoleName;
import com.matheus.ecommerce.domain.auth.repository.RoleRepository;
import com.matheus.ecommerce.domain.auth.entity.User;
import com.matheus.ecommerce.domain.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createRoles();
        createUserAdminForTests();
    }

    private void createRoles(){
        for (RoleName roleName : RoleName.values()){
            roleRepository.findByName(roleName)
                    .orElseGet(() -> roleRepository.save(new Role(roleName)));
        }
    }

    private void createUserAdminForTests(){
        userRepository.findByEmail("admin@admin.com")
                .orElseGet(() -> {
                    User user = new User(
                            "admin",
                            "admin@admin.com",
                            passwordEncoder.encode("admin123")
                    );


                    Role role = roleRepository.findByName(RoleName.ADMIN)
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND, "Role not found"
                            ));

                    user.addRoles(List.of(role));

                    userRepository.save(user);

                    return user;
                });
    }
}
