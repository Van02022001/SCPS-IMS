package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.CreateAccountForm;
import com.example.sparepartsinventorymanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    UserService userService;

    @Operation(summary = "For creating accounts")
    @PostMapping(value = "/accounts",  produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<?> CreateAccount(@Valid @RequestBody CreateAccountForm form){
        return userService.createAccount(form);
    }
}
