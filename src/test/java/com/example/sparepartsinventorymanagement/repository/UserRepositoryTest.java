package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Role;
import com.example.sparepartsinventorymanagement.entities.RoleStatus;
import com.example.sparepartsinventorymanagement.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


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
        Boolean existEmail = userRepository.existsByEmail(users.getEmail());
        assertThat(existEmail).isTrue();
    }

    @Test
    public void whenCheckNonExistsEmail_thenReturnFalse(){
        Boolean existEmail = userRepository.existsByEmail("van@gmail.com");
        assertThat(existEmail).isFalse();
    }


    @Test
    public  void whenCheckExistsPhone_thenReturnTrue(){
        Boolean existPhone = userRepository.existsByPhone(users.getPhone());
        assertThat(existPhone).isTrue();

    }

    @Test
    public void whenCheckNonExistsPhone_thenReturnFalse(){
        Boolean existsPhone= userRepository.existsByPhone("0935182029");
        assertThat(existsPhone).isFalse();
    }


}