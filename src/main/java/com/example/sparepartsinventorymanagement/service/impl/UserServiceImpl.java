package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.CreateAccountForm;
import com.example.sparepartsinventorymanagement.dto.request.UpdateUserForm;
import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.*;
import com.example.sparepartsinventorymanagement.service.EmailService;
import com.example.sparepartsinventorymanagement.service.UserService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service

public class UserServiceImpl implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public ResponseEntity<?> createAccount(CreateAccountForm form) {
        try {
            System.out.println("Checking email: " + form.getEmail());
            Optional<User> existUserByEmail = userRepository.findByEmail(form.getEmail());
            System.out.println("Found user by email: " + existUserByEmail.isPresent());
            // Kiểm tra email
            if (existUserByEmail.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                        HttpStatus.BAD_REQUEST.toString(), "Email already exists.", null
                ));
            }
            System.out.println("Checking email: " + form.getPhone());
            // Kiểm tra số điện thoại
            Optional<User> existingUserByPhone = userRepository.findByPhone(form.getPhone());
            System.out.println("Found user by email: " + existingUserByPhone.isPresent());
            if (existingUserByPhone.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                        HttpStatus.BAD_REQUEST.toString(), "Phone already exists.", null
                ));
            }

            // Lấy thông tin công ty
            Company company = companyRepository.findById(1L)
                    .orElseThrow(() -> new NotFoundException("Company is not found!"));

            // Khởi tạo thông tin user
            User user = new User();
            user.setFirstName(form.getFirstName());
            user.setMiddleName(form.getMiddleName());
            user.setLastName(form.getLastName());
            user.setEmail(form.getEmail());
            user.setPhone(form.getPhone());
            user.setIntro("New Staff");
            user.setRegisteredAt(new Date());
            user.setUsername(generateUsername(form));
            String rawPassword = generatePassword();
            user.setPassword(passwordEncoder.encode(rawPassword));

            // Lấy thông tin Role
            Role userRole = roleRepository.findByName(form.getRoleName())
                    .orElseThrow(() -> new RuntimeException("Role not found"));

            user.setRole(userRole);
            user.setProfile(userRole.getDescription());
            user.setCompany(company);

            // Lưu thông tin user
            userRepository.save(user);

            // Gửi email thông tin tài khoản
            emailService.sendAccountDetail(user.getEmail(), user.getUsername(), rawPassword);

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
                    HttpStatus.OK.toString(), "Account created successfully", user
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseObject(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), "An error occurred while processing your request.", e.getMessage()
            ));
        }

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

    @Override
    public ResponseEntity<?> updateUser(UpdateUserForm form) {
        User user = userRepository.findById(form.getUserId()).
                orElseThrow(() -> new  NotFoundException("User is not found !"));

        if(form.getRoleName() == null){
            List<Role> roles = roleRepository.findAll();
            return ResponseEntity.ok(new ResponseObject(null, "Please select a role",roles));
        } else {
            Role role = roleRepository.findByName(form.getRoleName())
                    .orElseThrow(() -> new NotFoundException("Role is not found"));
            user.setRole(role);
        }

        if(form.getPermissions() == null){
            List<Permission> permissions = permissionRepository.findAll();
            return ResponseEntity.ok(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Please select permission",permissions));
        } else {
            Set<Permission> selectedPermission =  permissionRepository.findPermissionsByNameIn(form.getPermissions());
            user.getRole().setPermissions(selectedPermission);
        }

        if(user.getRole().getName().equals("INVENTORY_STAFF") && form.getWarehouseId() == null){
            List<Warehouse> warehouses = warehouseRepository.findAll();
            return ResponseEntity.ok(new ResponseObject(HttpStatus.BAD_REQUEST.toString()," Please select a warehouse for INVENTORY_STAFF role", warehouses));

        } else if(user.getRole().getName().equals("INVENTORY_STAFF")){
            Warehouse warehouse = warehouseRepository.findById(form.getWarehouseId())
                    .orElseThrow(() -> new NotFoundException("Warehouse is not found"));
            user.setWarehouse(warehouse);
        }
        userRepository.save(user);

        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.toString(), "User updated successfully!", user));
    }

    private String generateUsername(CreateAccountForm form) {
        String roleName = form.getRoleName();

        // Map roles to their desired prefixes
        Map<String, String> rolePrefixes = new HashMap<>();
        rolePrefixes.put("INVENTORY_STAFF", "IS");
        rolePrefixes.put("MANAGER", "M");
        rolePrefixes.put("SALE_STAFF", "SS");
        rolePrefixes.put("ADMIN", "AD");

        // Check if roleName is valid
        if (!rolePrefixes.containsKey(roleName)) {
            throw new IllegalArgumentException("Invalid role name: " + roleName);
        }

        // Generate the random number between 0 and 99
        int randomNumber = (int) (Math.random() * 100);

        // Construct the username using the desired format
        return rolePrefixes.get(roleName) + "000" + String.format("%02d", randomNumber);
    }


    private String generatePassword() {
        // Your logic for generating password, for example:
        return UUID.randomUUID().toString().substring(0, 8);
    }

}
