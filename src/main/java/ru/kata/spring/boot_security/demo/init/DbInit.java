package ru.kata.spring.boot_security.demo.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;


@Component
@Transactional
public class DbInit {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DbInit(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        // Создание ролей
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        Role userRole = new Role();
        userRole.setName("ROLE_USER");

        roleRepository.save(adminRole);
        roleRepository.save(userRole);

        // Создание пользователей
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@admin.com");
        admin.setPassword(passwordEncoder.encode("100"));
        admin.getRoles().add(adminRole);
        admin.getRoles().add(userRole);

        User user = new User();
        user.setUsername("user");
        user.setEmail("user@user.com");
        user.setPassword(passwordEncoder.encode("100"));
        user.getRoles().add(userRole);

        userRepository.save(admin);
        userRepository.save(user);
    }
}