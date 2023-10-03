package com.example.sparepartsinventorymanagement.dto.response;

import com.example.sparepartsinventorymanagement.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PrincipalDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;


    private Boolean status;
    private Role role;

    public Role getRole() {
        return role;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }


    public String getEmail() {
        return email;
    }
    public String getUserName() {
        return username;
    }

    public Boolean getStatus() {
        return status;
    }
}
