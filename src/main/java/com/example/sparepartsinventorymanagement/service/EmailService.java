package com.example.sparepartsinventorymanagement.service;

public interface EmailService {
    void sendAccountDetail(String email, String username, String password);

    void sendPasswordResetEmail(String email, String token);

}
