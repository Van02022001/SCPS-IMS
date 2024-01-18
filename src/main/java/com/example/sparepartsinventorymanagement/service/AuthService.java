package com.example.sparepartsinventorymanagement.service;

import com.example.sparepartsinventorymanagement.dto.request.*;
import com.example.sparepartsinventorymanagement.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<?> login(LoginForm loginModel);
    ResponseEntity<?> logout(HttpServletRequest request, LogoutForm logoutModel);
    ResponseEntity<?> validateAccessToken();
    ResponseEntity<?> refreshAccessToken(HttpServletRequest request, RefreshTokenRequest refreshTokenModel);
    ResponseEntity<?> validateLoginForm(LoginForm loginModel);

    void changePassword(User user, String newPassword);

    boolean checkIfValidOldPassword(User user, String oldPassword);

    User findUserByName(String username);

    String changeUserPassword(ChangePasswordForm passwordModel);

    ResponseEntity<?> forgetPassword(ForgetPasswordForm form);
    ResponseEntity<?> resetPassword(String token);
}
