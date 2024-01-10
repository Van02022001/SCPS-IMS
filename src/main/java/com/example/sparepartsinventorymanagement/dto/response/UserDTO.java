package com.example.sparepartsinventorymanagement.dto.response;

import com.example.sparepartsinventorymanagement.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {


    private Long id;


    private String firstName;

    private String middleName;


    private String lastName;


    private String phone;

    private String email;

    private String image;

    private Date registeredAt;

    private Date lastLogin;


    private Role role;


}
