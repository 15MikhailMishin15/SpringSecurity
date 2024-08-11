package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Map;

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
        Map<String, Object> data = userService.getUserListData(principal);
        model.addAllAttributes(data);
        return "user-list";
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute User user, @RequestParam("roles") List<Long> roles) {
        userService.createUser(user, roles);
        return "redirect:/admin/users";
    }

    @PostMapping("/users")
    public String updateUser(@ModelAttribute User user, @RequestParam List<Long> roles) {
        try {
            userService.updateUser(user, roles);
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/users?error=userNotFound";
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteById(id);
        return "redirect:/admin/users";
    }
}