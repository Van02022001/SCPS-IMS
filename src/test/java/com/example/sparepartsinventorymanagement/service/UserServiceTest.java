package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.CreateAccountForm;
import com.example.sparepartsinventorymanagement.entities.Company;
import com.example.sparepartsinventorymanagement.entities.Role;
import com.example.sparepartsinventorymanagement.entities.User;
import com.example.sparepartsinventorymanagement.repository.CompanyRepository;
import com.example.sparepartsinventorymanagement.repository.RoleRepository;
import com.example.sparepartsinventorymanagement.repository.UserRepository;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

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
    @MockBean
    private CompanyRepository companyRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void whenCreateAccountWithExistingEmail_thenReturnBadRequest(){
        CreateAccountForm form =  new CreateAccountForm();
        form.setEmail("nguyenhongkhanh@gmail.com");

        User existingUser = new User();
        existingUser.setEmail("nguyenhongkhanh@gmail.com");
        when(userRepository.findByEmail(form.getEmail())).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = userService.createAccount(form);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }



    @Test
    void whenCreateAccountWithExistingPhone_thenReturnBadRequest(){
        CreateAccountForm form = new CreateAccountForm();
        form.setPhone("0915000386");

        User existingUser = new User();
        existingUser.setPhone("0915000386");
        when(userRepository.findByPhone(form.getPhone())).thenReturn(Arrays.asList(existingUser)); // Return a list containing the existing user.

        ResponseEntity<?> response = userService.createAccount(form);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

//    @Test
//    public void whenCreateAccountSuccessfully_thenOkStatus(){
//        CreateAccountForm form = new CreateAccountForm();
//        form.setEmail("quangvan@gmail.com");
//        form.setPhone("0935182029");
//        form.setRoleName("INVENTORY_STAFF");
//
//        // Mock checks for the email and phone number.
//        when(userRepository.findByEmail(form.getEmail())).thenReturn(Collections.emptyList());
//        when(userRepository.findByPhone(form.getPhone())).thenReturn(Collections.emptyList());
//
//        // Mock fetching of the role.
//        Role mockedRole = new Role();
//        mockedRole.setName(form.getRoleName());
//        mockedRole.setDescription("Some description for the role"); // Assuming roles have a description
//        when(roleRepository.findByName(form.getRoleName())).thenReturn(Optional.of(mockedRole));
//
//        // Mock the email sending service.
//        doNothing().when(emailService).sendAccountDetail(anyString(), anyString(), anyString());
//
//        // Mock fetching the company.
//        Company mockedCompany = new Company();
//        when(companyRepository.findCompanyByName("CÔNG TY TNHH SÀI GÒN KỸ THUẬT ĐIỀU KHIỂN")).thenReturn(mockedCompany);
//
//        // Mock password encoding.
//        String rawPassword = "dummyPassword";
//        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
//
//        // Mock saving the user.
//        User mockedUser = new User();
//        when(userRepository.save(any(User.class))).thenReturn(mockedUser);
//
//        // Call the service method.
//        ResponseEntity<?> response = userService.createAccount(form);
//
//        // Validate the outcomes.
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//
//        // Verify the email was sent once.
//        verify(emailService, times(1)).sendAccountDetail(anyString(), anyString(), anyString());
//    }


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

        Role userRole = new Role();
        userRole.setName("ADMIN");

        User user = new User();
        user.setId(userId);
        user.setRole(userRole);

        when(userRepository.getUserById(userId)).thenReturn(user);
        ResponseEntity<?> response = userService.getUserById(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseObject);
        ResponseObject responseObject = (ResponseObject) response.getBody();
        assertEquals("Get user successfully!", responseObject.getMessage());
        assertEquals(HttpStatus.OK.toString(), responseObject.getStatus());
        assertNotNull(responseObject.getData());
    }

    @Test
    public void whenGetUserByIdAndUserDoesNotExist_thenReturnNotFound(){
        CreateAccountForm form = new CreateAccountForm();
        form.setPhone("0915000386");

        User existingUser = new User();
        existingUser.setPhone("0915000386");
        when(userRepository.findByPhone(form.getPhone())).thenReturn(Arrays.asList(existingUser)); // Return a list containing the existing user.

        ResponseEntity<?> response = userService.createAccount(form);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    public void whenDeleteUserByIdSuccessfully_thenReturnOne(){
        Long userId = 1L;

        // Mocking the existence check to return true (user exists).
        when(userRepository.existsById(userId)).thenReturn(true);

        // Mocking the deletion action.
        doNothing().when(userRepository).deleteById(userId);

        // Running the actual service method.
        ResponseEntity<?> response = userService.deleteUserById(userId);

        // Validating the outcomes.
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseObject);
        ResponseObject responseObject = (ResponseObject) response.getBody();
        assertEquals("Deleted user successfully!", responseObject.getMessage());
        assertEquals(HttpStatus.OK.toString(), responseObject.getStatus());
        assertNull(responseObject.getData());
    }

    @Test
    public void whenDeleteUserByIdAndUserDoesNotExist_thenReturnZero(){
        Long userId =1L;
        doNothing().when(userRepository).deleteUserById(userId);
        ResponseEntity<?> response = userService.deleteUserById(userId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof  ResponseObject);
        ResponseObject responseObject =(ResponseObject) response.getBody();
        assertEquals(HttpStatus.NOT_FOUND.toString(), responseObject.getStatus());
        assertEquals("User is not found!", responseObject.getMessage());
        assertNull(responseObject.getData());
    }


}