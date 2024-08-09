package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    public String listUsers(Model model, Principal principal) {
        // Получаем список всех пользователей
        List<User> users = userService.findAll();
        model.addAttribute("users", users);

        // Добавляем данные о текущем пользователе
        if (principal != null) {
            User currentUser = userService.findByUsername(principal.getName());
            model.addAttribute("user", currentUser);

            // Добавляем список всех ролей для использования в выпадающем списке
            List<Role> roles = roleService.findAll();
            model.addAttribute("roles", roles);
        }

        return "user-list";
    }

    @GetMapping("/users/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.findAll());
        return "user-create";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute User user, @RequestParam("roles") List<Long> roles) {
        user.setRoles(roleService.findByIds(roles));
        userService.saveUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.findAll());
        return "user-update";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user, @RequestParam List<Long> roles) {
        // Найти существующего пользователя
        User existingUser = userService.findById(id);
        if (existingUser == null) {
            // Обработка случая, когда пользователь не найден
            return "redirect:/admin/users?error=userNotFound";
        }

        // Проверка пароля
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existingUser.getPassword()); // Сохраняем существующий пароль
        }

        // Установка id и ролей
        user.setId(id);
        user.setRoles(roleService.findByIds(roles));

        // Сохранение обновленного пользователя
        userService.saveUser(user);

        // Перенаправление на страницу с пользователями
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/user")
    public String userProfile(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        // Преобразуем список ролей в строку, разделенную запятыми
        String roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.joining(", "));

        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "user";
    }
}