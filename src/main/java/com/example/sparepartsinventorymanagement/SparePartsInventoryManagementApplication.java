package com.example.sparepartsinventorymanagement;

import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.repository.CompanyRepository;
import com.example.sparepartsinventorymanagement.repository.PermissionRepository;
import com.example.sparepartsinventorymanagement.repository.RoleRepository;
import com.example.sparepartsinventorymanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class SparePartsInventoryManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SparePartsInventoryManagementApplication.class, args);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private CompanyRepository companyRepository;
    @Bean
    public CommandLineRunner initData(UserRepository userRepository, RoleRepository roleRepository, PermissionRepository permissionRepository, CompanyRepository companyRepository){
        return (args) -> {
            Company company = new Company();
            company.setName("CÔNG TY TNHH SÀI GÒN KỸ THUẬT ĐIỀU KHIỂN");
            company.setEmail("phutungbombetongsaigon@gmail.com");
            company.setTaxCode("0311729807");
            company.setCode("SG-KT-DK");
            company.setAddress("528 Song Hành Xa Lộ Hà Nội, Phước Long A, Quận 9, Thành phố Hồ Chí Minh");
            company.setPhone("0979000386");
            companyRepository.save(company);


            Permission manageUser = new Permission();
            manageUser.setName("MANAGE_USER");
            manageUser.setDescription("Manage user in system");
            manageUser.setStatus(PermissionStatus.Active);
            manageUser.setCreatedAt(new Date());
            permissionRepository.save(manageUser);

            Permission manageRole = new Permission();
            manageRole.setName("MANAGE_ROLE");
            manageRole.setDescription("Manage role in system");
            manageRole.setStatus(PermissionStatus.Active);
            manageRole.setCreatedAt(new Date());
            permissionRepository.save(manageRole);

            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Admin role");
            adminRole.setStatus(RoleStatus.Active);
            adminRole.setCreatedAt(new Date());
            Set<Permission> permissionSet = new HashSet<>();
            permissionSet.add(manageRole);
            permissionSet.add(manageUser);
            adminRole.setPermissions(permissionSet);
            roleRepository.save(adminRole);


            User adminUser = new User();
            adminUser.setFirstName("Khanh");
            adminUser.setMiddleName("Hong");
            adminUser.setLastName("Nguyen");
            adminUser.setMobile("0915000386");
            adminUser.setEmail("nguyenhongkhanh@gmail.com");
            adminUser.setUsername("AD0001");
            adminUser.setPassword("Admin@123");
            adminUser.setRegisteredAt(new Date());
            adminUser.setRole(adminRole);
            userRepository.save(adminUser);


        };
    }

}
