package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CreateAccountForm;
import com.example.sparepartsinventorymanagement.entities.Role;
import com.example.sparepartsinventorymanagement.repository.RoleRepository;
import com.example.sparepartsinventorymanagement.repository.UserRepository;
import jakarta.validation.constraints.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

        when(userRepository.existsByEmail(form.getEmail())).thenReturn(true);

        ResponseEntity<?> response = userService.createAccount(form);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void whenCreateAccountWithExistingPhone_thenReturnBadRequest(){
        CreateAccountForm form =  new CreateAccountForm();
        form.setPhone("0915000386");

        when(userRepository.existsByEmail(form.getEmail())).thenReturn(true);

        ResponseEntity<?> response = userService.createAccount(form);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void whenCreateAccountSuccessfully_thenOkStatus(){
        CreateAccountForm form = new CreateAccountForm();
        form.setEmail("quangvan@gmail.com");
        form.setPhone("0935182029");
        form.setRoleName("INVENTORY_STAFF");

        when(userRepository.existsByEmail(form.getEmail())).thenReturn(false);
        when(userRepository.existsByPhone(form.getPhone())).thenReturn(false);
        when(roleRepository.findByName(form.getRoleName())).thenReturn(Optional.of(new Role()));

        ResponseEntity<?> response = userService.createAccount(form);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(emailService, times(1)).sendAccountDetail(anyString(),anyString(), anyString());
    }


}