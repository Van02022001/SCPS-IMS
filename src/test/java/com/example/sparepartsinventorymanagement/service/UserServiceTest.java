package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CreateAccountForm;
import com.example.sparepartsinventorymanagement.entities.Role;
import com.example.sparepartsinventorymanagement.entities.User;
import com.example.sparepartsinventorymanagement.repository.RoleRepository;
import com.example.sparepartsinventorymanagement.repository.UserRepository;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import jakarta.validation.constraints.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void whenCreateAccountWithExistingEmail_thenReturnBadRequest(){
        CreateAccountForm form =  new CreateAccountForm();
        form.setEmail("nguyenhongkhanh@gmail.com");

        when(userRepository.findByEmail(form.getEmail())).thenReturn(Optional.of(new User()));

        ResponseEntity<?> response = userService.createAccount(form);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }



    @Test
    void whenCreateAccountWithExistingPhone_thenReturnBadRequest(){
        CreateAccountForm form =  new CreateAccountForm();
        form.setPhone("0915000386");

        when(userRepository.findByEmail(form.getEmail())).thenReturn(Optional.of(new User()));

        ResponseEntity<?> response = userService.createAccount(form);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void whenCreateAccountSuccessfully_thenOkStatus(){
        CreateAccountForm form = new CreateAccountForm();
        form.setEmail("quangvan@gmail.com");
        form.setPhone("0935182029");
        form.setRoleName("INVENTORY_STAFF");

        when(userRepository.findByEmail(form.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByPhone(form.getPhone())).thenReturn(Optional.empty());
        when(roleRepository.findByName(form.getRoleName())).thenReturn(Optional.of(new Role()));

        ResponseEntity<?> response = userService.createAccount(form);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(emailService, times(1)).sendAccountDetail(anyString(),anyString(), anyString());
    }

    @Test
    public void whenGetAllUsersSuccessfully_thenReturnListUser(){
        User user1 = new User();
        User user2 = new User();
        List<User> mockUsers = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(mockUsers);

        ResponseEntity<?> response = userService.getAllUsers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseObject);
        ResponseObject responseObject = (ResponseObject) response.getBody();
        assertEquals("Get list users successfully!", responseObject.getMessage());
        assertEquals(HttpStatus.OK.toString(), responseObject.getStatus());
        assertNotNull(responseObject.getData());
    }

    @Test
    public void whenGetAllUserAndNoUsersExist_thenReturnNoContent(){
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = userService.getAllUsers();
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(response.getBody() instanceof  ResponseObject);
        ResponseObject responseObject = (ResponseObject) response.getBody();
        assertEquals("List of users is empty!", responseObject.getMessage());
        assertEquals(HttpStatus.NO_CONTENT.toString(), responseObject.getStatus());
        assertNull(responseObject.getData());

    }
    @Test
    public void whenGetUserByIdSuccessfully_thenReturnUser(){
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.getUserById(userId)).thenReturn(user);
        ResponseEntity<?> response = userService.getUserById(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof  ResponseObject);
        ResponseObject responseObject = (ResponseObject) response.getBody();
        assertEquals("Get user successfully!", responseObject.getMessage());
        assertEquals(HttpStatus.OK.toString(), responseObject.getStatus());
        assertNotNull(responseObject.getData());
    }

    @Test
    public void whenGetUserByIdAndUserDoesNotExist_thenReturnNotFound(){
        Long userId = 3L;
        when(userRepository.getUserById(userId)).thenReturn(null);
        ResponseEntity<?> response = userService.getUserById(userId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseObject);
        ResponseObject responseObject = (ResponseObject) response.getBody();
        assertEquals("User is not founded!", responseObject.getMessage());
        assertEquals(HttpStatus.NOT_FOUND.toString(), responseObject.getStatus());
        assertNull(responseObject.getData());
    }
    @Test
    public void whenDeleteUserByIdSuccessfully_thenReturnOne(){
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.deleteUserById(userId)).thenReturn(1);
        ResponseEntity<?> response = userService.deleteUserById(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof  ResponseObject);
        ResponseObject responseObject = (ResponseObject) response.getBody();
        assertEquals("Delete user successfully!", responseObject.getMessage());
        assertEquals(HttpStatus.OK.toString(), responseObject.getStatus());
        assertNull(responseObject.getData());
    }

    @Test
    public void whenDeleteUserByIdAndUserDoesNotExist_thenReturnZero(){
        Long userId =1L;
        when(userRepository.deleteUserById(userId)).thenReturn(0);
        ResponseEntity<?> response = userService.deleteUserById(userId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof  ResponseObject);
        ResponseObject responseObject =(ResponseObject) response.getBody();
        assertEquals(HttpStatus.NOT_FOUND.toString(), responseObject.getStatus());
        assertEquals("User is not found!", responseObject.getMessage());
        assertNull(responseObject.getData());
    }


}