package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void saveUser(User user) {
        String password = user.getPassword();

        if (password != null && !password.isEmpty() && !isPasswordEncoded(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }

        userRepository.save(user);
    }

    private boolean isPasswordEncoded(String password) {
        return password != null && password.length() == 60 &&
                (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public User getCurrentUser(String username) {
        return findByUsername(username);
    }

    @Override
    public String getUserRoles(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(", "));
    }

    @Override
    public void updateUser(User user, List<Long> roles) {
        User existingUser = findById(user.getId());
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        }

        user.setRoles(findRolesByIds(roles));
        saveUser(user);
    }

    @Override
    public Set<Role> findRolesByIds(List<Long> ids) {
        return ids.stream()
                .map(roleRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<String, Object> getUserListData(Principal principal) {
        Map<String, Object> data = new HashMap<>();
        List<User> users = findAllUsers();
        data.put("users", users);

        if (principal != null) {
            User currentUser = getCurrentUser(principal.getName());
            data.put("user", currentUser);

            List<Role> roles = findAllRoles();
            data.put("roles", roles);
        }

        return data;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    @Override
    public Map<String, Object> getUserProfileData(String username) {
        Map<String, Object> data = new HashMap<>();
        User user = getCurrentUser(username);
        String roles = getUserRoles(user);

        data.put("user", user);
        data.put("roles", roles);

        return data;
    }

    @Override
    public User createUser(User user, List<Long> roles) {
        user.setRoles(findRolesByIds(roles));
        return userRepository.save(user); // Обновлено, чтобы возвращать сохраненного пользователя
    }
}