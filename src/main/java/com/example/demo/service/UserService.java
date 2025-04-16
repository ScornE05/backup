package com.example.demo.service;
import com.example.demo.DTO.UserDTO;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(User user);

    User updateUser(Long id, UserDTO userDTO);

    Optional<User> getUserById(Long id);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);

    List<User> getAllUsers();

    Page<User> getAllUsers(Pageable pageable);

    Page<User> getUsersByRole(String role, Pageable pageable);

    void deleteUser(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    long countByRole(String role);

    boolean isPasswordValid(User user, String password);

    UserDetails loadUserByUsername(String username);
}
