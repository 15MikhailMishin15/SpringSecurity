package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService extends UserDetailsService {
    User findById(Long id);
    void saveUser(User user);
    void deleteById(Long id);
    List<User> findAllUsers();
    User findByUsername(String username);
    List<Role> findAllRoles();
    Set<Role> findRolesByIds(List<Long> ids);
    User getCurrentUser(String username);
    String getUserRoles(User user);
    void updateUser(User user, List<Long> roles);
    Map<String, Object> getUserListData(Principal principal);
    Map<String, Object> getUserProfileData(String username);
    void createUser(User user, List<Long> roles);
}