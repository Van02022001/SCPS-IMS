package com.example.sparepartsinventorymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO {
    private Long id;


    private String firstName;

    private String middleName;


    private String lastName;


    private String phone;

    private String email;


    private Date registeredAt;

    private Date lastLogin;


    private String rolName;

    private String intro;

    private String profile;


}
