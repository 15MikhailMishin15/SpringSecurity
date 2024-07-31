package ru.kata.spring.boot_security.demo.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

@Component
public class DbInit {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public DbInit(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    @Transactional
    public void createUsersIfNotExists() {
        // Проверяем наличие ролей
        if (roleRepository.count() == 0) {
            // Создание и сохранение ролей
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(adminRole);
            roleRepository.save(userRole);

            // Создание пользователей
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin");
            admin.setEmail("admin@example.com");

            User user = new User();
            user.setUsername("user");
            user.setPassword("user");
            user.setEmail("user@example.com");

            // Присваиваем роли пользователям
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(userRole);
            admin.setRoles(adminRoles);

            Set<Role> userRoles = new HashSet<>();
            userRoles.add(userRole);
            user.setRoles(userRoles);

            // Сохранение пользователей
            userRepository.save(admin);
            userRepository.save(user);
        }
    }
}