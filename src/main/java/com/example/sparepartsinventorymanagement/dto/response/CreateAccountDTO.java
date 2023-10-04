package com.example.sparepartsinventorymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountDTO {

    private String firstName;
    private String middleName;
    private String lastName;
    private String phone;
    private String email;
    private String roleName;
}
