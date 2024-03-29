package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CreateAccountForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateUserForm;
import com.example.sparepartsinventorymanagement.dto.response.UserDetailDTO;
import com.example.sparepartsinventorymanagement.entities.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    ResponseEntity<?> createAccount(CreateAccountForm form);

    ResponseEntity<?> getAllUsers();

    ResponseEntity<?> getUserById(Long id);

    ResponseEntity<?> deleteUserById(Long id);

    ResponseEntity<?> updateUser(UpdateUserForm form);






}
