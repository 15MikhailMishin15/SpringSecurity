package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
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

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute User user, @RequestParam("roles") List<Long> roles) {
        user.setRoles(roleService.findByIds(roles));
        userService.saveUser(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/users")
    public String updateUser(@ModelAttribute User user, @RequestParam List<Long> roles) {
        // Найти существующего пользователя
        User existingUser = userService.findById(user.getId());
        if (existingUser == null) {
            return "redirect:/admin/users?error=userNotFound";
        }

        // Если пароль пустой, сохраняем существующий пароль
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            System.out.println("User password" + user.getPassword());
            user.setPassword(existingUser.getPassword());
        }
        System.out.println("User password вне if" + user.getPassword());
        // Установка id и ролей
        user.setRoles(roleService.findByIds(roles));

        // Сохранение обновленного пользователя
        userService.saveUser(user);

        // Перенаправление на страницу с пользователями
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam("id") Long id) {
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