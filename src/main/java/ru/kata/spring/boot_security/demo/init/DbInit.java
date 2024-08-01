package ru.kata.spring.boot_security.demo.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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

    @Autowired
    public DbInit(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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
        admin.setPassword("admin"); // Замените на закодированный пароль
        admin.getRoles().add(adminRole);

        User user = new User();
        user.setUsername("user");
        user.setPassword("user"); // Замените на закодированный пароль
        user.getRoles().add(userRole);

        userRepository.save(admin);
        userRepository.save(user);
    }
}