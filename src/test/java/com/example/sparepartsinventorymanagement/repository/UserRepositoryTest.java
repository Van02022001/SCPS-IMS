package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Role;
import com.example.sparepartsinventorymanagement.entities.RoleStatus;
import com.example.sparepartsinventorymanagement.entities.User;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;



    @Autowired
    private TestEntityManager testEntityManager;


    private User users;
    @BeforeEach
    void setUp() {

        Role role = Role.builder()
                    .name("ADMIN")
                    .description("Admin role")
                    .status(RoleStatus.Active)
                    .createdAt(new Date())
                    .build();
        testEntityManager.persistAndFlush(role);

        User user = User.builder()
                .email("nguyenhongkhanh@gmail.com")
                .phone("0915000386")
                .username("AD0001")
                .password("Admin@123")
                .role(role)
                .registeredAt(new Date())
                .build();
        testEntityManager.persistAndFlush(user);

        users = user;
    }
    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }
    @Test
    public  void whenCheckExistsEmail_thenReturnTrue(){
        List<User> existEmail = userRepository.findByEmail(users.getEmail());
        assertThat(existEmail).isNotNull();
    }

    @Test
    public void whenCheckNonExistsEmail_thenReturnFalse(){
        List<User>  existEmail = userRepository.findByEmail("van@gmail.com");
        assertThat(existEmail).isNotNull();
    }


    @Test
    public  void whenCheckExistsPhone_thenReturnPresent(){
        List<User> existPhone = userRepository.findByPhone(users.getPhone());
        assertThat(existPhone).isNotNull();

    }

    @Test
    public void whenCheckNonExistsPhone_thenReturnNotPresent(){
        List<User> existsPhone= userRepository.findByPhone("0935182029");

        assertTrue(existsPhone.isEmpty());
    }


    @Test
    public void whenGetAllUsersSuccessfully_thenReturnListUser() {
        List<User> user = userRepository.findAll();
        assertThat(user).isNotNull();
        assertThat(user.size()).isEqualTo(1);
        assertThat(user.get(0).getEmail()).isEqualTo("nguyenhongkhanh@gmail.com");
    }

    @Test
    public void whenGetAllUserAndNoUsersExist_thenReturnEmptyList(){
        userRepository.deleteAll();

        List<User> users = userRepository.findAll();

        assertThat(users).isNotNull();

    }
    @Test
    public void whenDeleteExistingUserById_thenUserShouldBeDeleted(){
        userRepository.deleteById(users.getId());

        boolean exist = userRepository.existsById(users.getId());
        assertThat(exist).isFalse();
    }

    @Test
    public void whenDeleteUserByIdAndUserDoesNotExist_thenShouldNotThrowException(){
        Long nonExistingUserId = 2L;

        assertDoesNotThrow(() -> userRepository.deleteUserById(nonExistingUserId));
    }

}