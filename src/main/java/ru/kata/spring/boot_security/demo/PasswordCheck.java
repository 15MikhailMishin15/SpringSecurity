package ru.kata.spring.boot_security.demo;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordCheck {
    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        String rawPassword = "100";
        String encodedPassword = "$2a$10$4FRHMjKN3aFU2zM.R77MiuE3vfrpns47bVYt8iWrOhZLsVXX4wPMi"; // Хеш пароля из базы данных

        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        System.out.println("Password matches: " + matches);
    }
}
