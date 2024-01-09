package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.CreateAccountForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateUserForm;
import com.example.sparepartsinventorymanagement.exception.ResourceNotFoundException;
import com.example.sparepartsinventorymanagement.service.UserService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "admin")
public class AdminController {


    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "For creating accounts")
    @PostMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountForm form) {

            return userService.createAccount(form);

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "For getting all users")
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllUsers() {

            return userService.getAllUsers();

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "For getting user by id")
    @GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserById(@PathVariable Long id) {

            return userService.getUserById(id);

    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "For deleting user by id")
    @DeleteMapping(value = "/users/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {

            return userService.deleteUserById(id);

    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "For updating user details")
    @PutMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserForm form) {

            return userService.updateUser(form);

    }
}
