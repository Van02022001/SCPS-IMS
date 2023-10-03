package com.example.sparepartsinventorymanagement;

import com.example.sparepartsinventorymanagement.entities.*;
import com.example.sparepartsinventorymanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class SparePartsInventoryManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SparePartsInventoryManagementApplication.class, args);
    }

//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private PermissionRepository permissionRepository;
//
//    @Autowired
//    private CompanyRepository companyRepository;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//    @Bean
//    public CommandLineRunner initData(UserRepository userRepository, RoleRepository roleRepository, PermissionRepository permissionRepository, CompanyRepository companyRepository, PasswordEncoder passwordEncoder,
//                                      WarehouseRepository warehouseRepository){
//        return (args) -> {
//            Company company = new Company();
//            company.setName("CÔNG TY TNHH SÀI GÒN KỸ THUẬT ĐIỀU KHIỂN");
//            company.setEmail("phutungbombetongsaigon@gmail.com");
//            company.setTaxCode("0311729807");
//            company.setCode("SG-KT-DK");
//            company.setAddress("528 Song Hành Xa Lộ Hà Nội, Phước Long A, Quận 9, Thành phố Hồ Chí Minh");
//            company.setPhone("0979000386");
//            companyRepository.save(company);
//
//
//            Warehouse warehouse1 = new Warehouse();
//            warehouse1.setName("Kho 1");
//            warehouse1.setStatus(WarehouseStatus.Active);
//            warehouse1.setCreatedAt(new Date());
//            warehouse1.setAddress("528 Song Hành Xa Lộ Hà Nội, Phước Long A, Quận 9, Thành phố Hồ Chí Minh");
//            warehouseRepository.save(warehouse1);
//
//
//            Warehouse warehouse2 = new Warehouse();
//            warehouse2.setName("Kho 2");
//            warehouse2.setStatus(WarehouseStatus.Active);
//            warehouse2.setCreatedAt(new Date());
//            warehouse2.setAddress("Kho 18C-Kho Thủ Đức, Đường số 1, Trường Thọ, Thủ Đức");
//            warehouseRepository.save(warehouse2);
//
//            Permission manageUser = new Permission();
//            manageUser.setName("MANAGE_USER");
//            manageUser.setDescription("Manage user in system");
//            manageUser.setStatus(PermissionStatus.Active);
//            manageUser.setCreatedAt(new Date());
//            permissionRepository.save(manageUser);
//
//            Permission sale = new Permission();
//            sale.setName("SALE");
//            sale.setDescription("Sale product");
//            sale.setStatus(PermissionStatus.Active);
//            sale.setCreatedAt(new Date());
//            permissionRepository.save(sale);
//
//            Permission manageWarehouse = new Permission();
//            manageWarehouse.setName("MANAGE_WAREHOUSE");
//            manageWarehouse.setDescription("Manage warehouse in system");
//            manageWarehouse.setStatus(PermissionStatus.Active);
//            manageWarehouse.setCreatedAt(new Date());
//            permissionRepository.save(manageWarehouse);
//
//            Permission manageRole = new Permission();
//            manageRole.setName("MANAGE_ROLE");
//            manageRole.setDescription("Manage role in system");
//            manageRole.setStatus(PermissionStatus.Active);
//            manageRole.setCreatedAt(new Date());
//            permissionRepository.save(manageRole);
//
//            Permission manageProduct = new Permission();
//            manageProduct.setName("MANAGE_PRODUCT");
//            manageProduct.setDescription("Manage products and some thing else");
//            manageProduct.setStatus(PermissionStatus.Active);
//            manageProduct.setCreatedAt(new Date());
//            permissionRepository.save(manageProduct);
//
//            Role saleRole = new Role();
//            saleRole.setName("SALE_STAFF");
//            saleRole.setDescription("sale role");
//            saleRole.setStatus(RoleStatus.Active);
//            saleRole.setCreatedAt(new Date());
//            Set<Permission> permissionSets = new HashSet<>();
//            permissionSets.add(sale);
//            saleRole.setPermissions(permissionSets);
//            roleRepository.save(saleRole);
//
//            Role managerRole = new Role();
//            managerRole.setName("MANAGER");
//            managerRole.setDescription("manager role");
//            managerRole.setStatus(RoleStatus.Active);
//            managerRole.setCreatedAt(new Date());
//            Set<Permission> managerPermission = new HashSet<>();
//            permissionSets.add(manageProduct);
//            managerRole.setPermissions(managerPermission);
//            roleRepository.save(managerRole);
//
//            Role inventoryRole = new Role();
//            inventoryRole.setName("INVENTORY_STAFF");
//            inventoryRole.setDescription("inventory role");
//            inventoryRole.setStatus(RoleStatus.Active);
//            inventoryRole.setCreatedAt(new Date());
//            Set<Permission> inventoryPermissions = new HashSet<>();
//            inventoryPermissions.add(manageWarehouse);
//            inventoryRole.setPermissions(inventoryPermissions);
//            roleRepository.save(inventoryRole);
//
//
//            Permission manageUser = new Permission();
//            manageUser.setName("MANAGE_USER");
//            manageUser.setDescription("Manage user in system");
//            manageUser.setStatus(PermissionStatus.Active);
//            manageUser.setCreatedAt(new Date());
//            permissionRepository.save(manageUser);
//
//            Permission manageRole = new Permission();
//            manageRole.setName("MANAGE_ROLE");
//            manageRole.setDescription("Manage role in system");
//            manageRole.setStatus(PermissionStatus.Active);
//            manageRole.setCreatedAt(new Date());
//            permissionRepository.save(manageRole);
//
//
//
//
//
//            Role adminRole = new Role();
//            adminRole.setName("ADMIN");
//            adminRole.setDescription("Admin role");
//            adminRole.setStatus(RoleStatus.Active);
//            adminRole.setCreatedAt(new Date());
//            Set<Permission> permissionAdmin = new HashSet<>();
//            permissionAdmin.add(manageRole);
//            permissionAdmin.add(manageUser);
//            adminRole.setPermissions(permissionAdmin);
//            roleRepository.save(adminRole);
//
//
//            User adminUser = new User();
//            adminUser.setFirstName("Khanh");
//            adminUser.setMiddleName("Hong");
//            adminUser.setLastName("Nguyen");
//            adminUser.setPhone("0915000386");
//            adminUser.setEmail("nguyenhongkhanh@gmail.com");
//            adminUser.setUsername("AD0001");
//            adminUser.setPassword(passwordEncoder.encode("Admin@123"));
//            adminUser.setRegisteredAt(new Date());
//            adminUser.setCompany(company);
//            adminUser.setRole(adminRole);
//            userRepository.save(adminUser);
//
//
//        };
//    }

}
