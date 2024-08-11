package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping({"/admin", "/user"})
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({ "", "/user" })
    public String userProfile(Model model, Principal principal, Authentication authentication) {
        Map<String, Object> userProfileData = userService.getUserProfileData(principal.getName());
        model.addAllAttributes(userProfileData);
        return "user";
    }
}