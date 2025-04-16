package com.example.demo.Controller;

import com.example.demo.DTO.UserDTO;
import com.example.demo.service.UserService;
import com.example.demo.DTO.ApiResponse;
import com.example.demo.DTO.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.Security.CurrentUser;
import com.example.demo.Security.UserPrincipal;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        User user = userService.getUserById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUser.getId()));

        return ResponseEntity.ok(modelMapper.map(user, UserDTO.class));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id, principal)")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return ResponseEntity.ok(modelMapper.map(user, UserDTO.class));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        Page<User> users = userService.getAllUsers(pageable);
        Page<UserDTO> userDTOs = users.map(user -> modelMapper.map(user, UserDTO.class));

        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getUsersByRole(@PathVariable String role, Pageable pageable) {
        Page<User> users = userService.getUsersByRole(role, pageable);
        Page<UserDTO> userDTOs = users.map(user -> modelMapper.map(user, UserDTO.class));

        return ResponseEntity.ok(userDTOs);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id, principal)")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        User updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(modelMapper.map(updatedUser, UserDTO.class));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully"));
    }

    @GetMapping("/count/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> countUsersByRole(@PathVariable String role) {
        long count = userService.countByRole(role);
        return ResponseEntity.ok(count);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        // Check if username is already taken
        if (userService.existsByUsername(userDTO.getUsername())) {
            return new ResponseEntity<>(new UserDTO(), HttpStatus.BAD_REQUEST);
        }

        // Check if email is already in use
        if (userService.existsByEmail(userDTO.getEmail())) {
            return new ResponseEntity<>(new UserDTO(), HttpStatus.BAD_REQUEST);
        }

        // Create new user
        User user = modelMapper.map(userDTO, User.class);
        User result = userService.createUser(user);

        return new ResponseEntity<>(modelMapper.map(result, UserDTO.class), HttpStatus.CREATED);
    }
}
