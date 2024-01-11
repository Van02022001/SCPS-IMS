package com.example.sparepartsinventorymanagement.jwt.userprincipal;

import com.example.sparepartsinventorymanagement.entities.Role;
import com.example.sparepartsinventorymanagement.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Principal implements UserDetails {

    private Long id;

    private String firstName;
    private String middleName;
    private String lastName;
    @JsonIgnore
    private String password;

    private String email;
    private String username;

    private String image;
    private String name;

    private Boolean status;
    private Role role;
    @JsonIgnore
    private Collection<? extends GrantedAuthority> grantedAuthorities;
    private Map<String, Object> attributes;
    public static Principal build(User user) {
        List<GrantedAuthority> grantedAuthorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().toUpperCase()));

        return Principal.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .image(user.getImage())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .name(user.getRole().getName())
                .grantedAuthorities(grantedAuthorities)
                .build();
    }

    public static Principal create(User user, Map<String, Object> attributes) {
        Principal userPrincipal = Principal.build(user);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }
    public Principal(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.grantedAuthorities = authorities;
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

    public String getUserName() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }
    public String getName() {
        return name;
    }



    public Boolean getStatus() {
        return status;
    }

    public Role getRole() {
        return role;
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }




}
