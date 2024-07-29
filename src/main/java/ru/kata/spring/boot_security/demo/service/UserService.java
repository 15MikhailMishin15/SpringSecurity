package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.entity.User;

import java.util.List;

public interface UserService extends UserDetailsService {
    User findById(Long id);
    User saveUser(User user);
    void deleteById(Long id);
    List<User> findAll();
    User findByUsername(String username);
}