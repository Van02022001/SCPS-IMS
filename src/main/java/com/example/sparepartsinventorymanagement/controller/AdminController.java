package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.CreateAccountForm;
import com.example.sparepartsinventorymanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    UserService userService;

    @Operation(summary = "For creating accounts")
    @PostMapping(value = "/accounts",  produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountForm form){
        return userService.createAccount(form);
    }


    @Operation(summary = "For getting all users")
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<?> getAllUsers(){
        return userService.getAllUsers();
    }

    @Operation(summary = "For getting user by id")
    @GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<?> getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }

    @Operation(summary = "For deleting user by id")
    @DeleteMapping(value = "/users/{id}")
    private ResponseEntity<?> deleteUserById(@PathVariable Long id){
        return userService.deleteUserById(id);
    }

}
