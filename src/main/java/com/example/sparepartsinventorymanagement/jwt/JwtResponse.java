package com.example.sparepartsinventorymanagement.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class JwtResponse {
    private String accessToken;
    private String refreshToken;
}
