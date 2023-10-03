package com.example.sparepartsinventorymanagement.controller;

import com.example.sparepartsinventorymanagement.dto.request.ChangePasswordForm;
import com.example.sparepartsinventorymanagement.dto.request.LoginForm;
import com.example.sparepartsinventorymanagement.dto.request.LogoutForm;
import com.example.sparepartsinventorymanagement.dto.request.RefreshTokenRequest;
import com.example.sparepartsinventorymanagement.entities.User;
import com.example.sparepartsinventorymanagement.exception.AuthenticationsException;
import com.example.sparepartsinventorymanagement.exception.InvalidPasswordException;
import com.example.sparepartsinventorymanagement.exception.PasswordMismatchException;
import com.example.sparepartsinventorymanagement.service.AuthService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/password-change")
    @Operation(summary = "For changing password ")
    public ResponseEntity<?> changPassword(@RequestBody ChangePasswordForm passwordModel){
        try{
            String result = authService.changeUserPassword(passwordModel);
            return ResponseEntity.ok(result);
        } catch (AuthenticationsException authenticationsException){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authenticationsException.getMessage());
        } catch (InvalidPasswordException invalidPasswordException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(invalidPasswordException.getMessage());
        } catch (PasswordMismatchException passwordMismatchException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(passwordMismatchException.getMessage());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }



    @PostMapping(value = "/validation", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "For getting user information after login")
    public ResponseEntity<?> reloadUserByJWT() {
        return authService.validateAccessToken();
    }

    @PostMapping(value = "/accessToken", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "For getting new access token by refresh token after it expired")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, @Valid @RequestBody RefreshTokenRequest refreshTokenForm) {
        if(refreshTokenForm.getRefreshToken() == null || refreshTokenForm.getRefreshToken().isEmpty() || refreshTokenForm.getRefreshToken().isBlank()){
            return new ResponseEntity<ResponseObject>(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Please input refresh token",  null), HttpStatus.BAD_REQUEST);
        }
        return authService.refreshAccessToken(request, refreshTokenForm);
    }

    @PostMapping(value = "/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "For logout")
    public ResponseEntity<?> logout(HttpServletRequest request, @Valid @RequestBody LogoutForm logoutForm) {
        if(logoutForm.getRefreshToken() == null || logoutForm.getRefreshToken().isEmpty() || logoutForm.getRefreshToken().isBlank()){
            return new ResponseEntity<ResponseObject>(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Please input refresh token",  null), HttpStatus.BAD_REQUEST);
        }
        return authService.logout(request, logoutForm);
    }

    @Operation(summary = "For login")
    @PostMapping(value="/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirements
    public ResponseEntity<?> login(@Valid @RequestBody LoginForm loginForm) {
        if(loginForm.getUsername().isEmpty() || loginForm.getUsername().isBlank() || loginForm.getUsername() == null){
            return new ResponseEntity<ResponseObject>(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Please input username",  null), HttpStatus.BAD_REQUEST);
        }
        if(loginForm.getPassword().isEmpty() || loginForm.getPassword().isBlank() || loginForm.getPassword() == null){
            return new ResponseEntity<ResponseObject>(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Please input password", null), HttpStatus.BAD_REQUEST);
        }
        return authService.login(loginForm);
    }


}
