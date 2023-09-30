package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CreateAccountForm;
import com.example.sparepartsinventorymanagement.entities.Role;
import com.example.sparepartsinventorymanagement.entities.User;
import com.example.sparepartsinventorymanagement.repository.RoleRepository;
import com.example.sparepartsinventorymanagement.repository.UserRepository;
import com.example.sparepartsinventorymanagement.service.EmailService;
import com.example.sparepartsinventorymanagement.service.UserService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;
    @Override
    public ResponseEntity<?> createAccount(CreateAccountForm form) {
        if(userRepository.existsByEmail(form.getEmail())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Email already exists.", null
            ));
        }
        if(userRepository.existsByPhone(form.getPhone())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.toString(), "Phone already exists.", null
            ));
        }

        User user = new User();
        user.setFirstName(form.getFirstName());
        user.setMiddleName(form.getMiddleName());
        user.setLastName(form.getLastName());
        user.setEmail(form.getEmail());
        user.setPhone(form.getPhone());
        user.setUsername(generateUsername(form));
        user.setPassword(generatePassword());

        Role userRole = roleRepository.findByName(form.getRoleName())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(userRole);
        userRepository.save(user);


        emailService.sendAccountDetail(user.getEmail(), user.getUsername(), user.getPassword());

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Account created successfully", user
        ));

    }

    @Override
    public ResponseEntity<?> getAllUsers() {
        List<User> users =userRepository.findAll();
        if(!users.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(), "Get list users successfully!", users
            ));
        }

        return  ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ResponseObject(
                HttpStatus.NO_CONTENT.toString(), "List of users is empty!", null
        ));
    }

    @Override
    public ResponseEntity<?> getUserById(Long id) {
        User user = userRepository.getUserById(id );
        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
               HttpStatus.NOT_FOUND.toString(), "User is not founded!", null
            ));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Get user successfully!", user
        ));

    }

    @Override
    public ResponseEntity<?> deleteUserById(Long id) {
        int result = userRepository.deleteUserById(id);

        if(result==0){
             return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.toString(), "User is not found!", null
            ));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                HttpStatus.OK.toString(), "Delete user successfully!", null
        ));
    }

    private String generateUsername(CreateAccountForm form) {
        // Your logic for generating username
        String roleName = form.getRoleName();
        String[] parts = roleName.split("-");

        StringBuilder stringBuilder = new StringBuilder();
        for(String part: parts){
            if(!part.isEmpty()){
                stringBuilder.append(Character.toUpperCase(part.charAt(0)));
            }
        }

        int randomNumber = (int) (Math.random() *900 +100);
        stringBuilder.append(randomNumber);
        return stringBuilder.toString();
    }

    private String generatePassword() {
        // Your logic for generating password, for example:
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
