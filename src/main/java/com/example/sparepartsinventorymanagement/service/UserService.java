package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CreateAccountForm;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<?> createAccount(CreateAccountForm form);

    ResponseEntity<?> getAllUsers();

    ResponseEntity<?> getUserById(Long id);

    ResponseEntity<?> deleteUserById(Long id);
}
