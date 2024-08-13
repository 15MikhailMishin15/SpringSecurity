package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return user != null ? new ResponseEntity<>(user, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody Map<String, Object> requestBody) {
        User user = convertMapToUser(requestBody);

        @SuppressWarnings("unchecked")
        List<String> rolesStr = (List<String>) requestBody.get("roles");
        List<Long> roles = rolesStr.stream().map(Long::parseLong).collect(Collectors.toList());

        User newUser = userService.createUser(user, roles);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> requestBody) {
        User existingUser = userService.findById(id);
        if (existingUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User user = convertMapToUser(requestBody);
        user.setId(id);

        @SuppressWarnings("unchecked")
        List<String> rolesStr = (List<String>) requestBody.get("roles");
        List<Long> roles = rolesStr.stream().map(Long::parseLong).collect(Collectors.toList());

        userService.updateUser(user, roles);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    private User convertMapToUser(Map<String, Object> map) {
        User user = new User();
        user.setUsername((String) map.get("username"));
        user.setEmail((String) map.get("email"));
        user.setPassword((String) map.get("password"));
        return user;
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.findById(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/user/profile")
    public ResponseEntity<Map<String, Object>> getUserProfileData(Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("email", user.getEmail());
        response.put("roles", user.getAuthorities().stream()
                .map(auth -> Map.of("name", auth.getAuthority()))
                .collect(Collectors.toList()));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/list")
    public ResponseEntity<Map<String, Object>> getUserListData(Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> data = userService.getUserListData(principal);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = userService.findAllRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }
}