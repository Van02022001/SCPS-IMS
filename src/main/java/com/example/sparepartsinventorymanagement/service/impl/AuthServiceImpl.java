package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.dto.request.ChangePasswordForm;
import com.example.sparepartsinventorymanagement.dto.request.LoginForm;
import com.example.sparepartsinventorymanagement.dto.request.LogoutForm;
import com.example.sparepartsinventorymanagement.dto.request.RefreshTokenRequest;
import com.example.sparepartsinventorymanagement.dto.response.PrincipalDTO;
import com.example.sparepartsinventorymanagement.dto.response.RefreshTokenResponse;
import com.example.sparepartsinventorymanagement.entities.User;
import com.example.sparepartsinventorymanagement.exception.AuthenticationsException;
import com.example.sparepartsinventorymanagement.exception.InvalidPasswordException;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.exception.PasswordMismatchException;
import com.example.sparepartsinventorymanagement.jwt.*;
import com.example.sparepartsinventorymanagement.jwt.userprincipal.Principal;
//import com.example.sparepartsinventorymanagement.repository.PasswordResetTokenRepository;
import com.example.sparepartsinventorymanagement.repository.UserRepository;
import com.example.sparepartsinventorymanagement.service.AuthService;
import com.example.sparepartsinventorymanagement.service.EmailService;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

   // private PasswordResetTokenRepository passwordResetTokenRepository;

    private final String companyEmail = "qvanwork@outlook.com.vn";
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private RefreshTokenProvider refreshTokenProvider;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private  AuthenticationManager authenticationManager;
    @Autowired
    CacheManager cacheManager;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public ResponseEntity<?> login(LoginForm loginModel) {
        ResponseEntity<?> responseEntity = this.validateLoginForm(loginModel);
        if (responseEntity != null) {
            return responseEntity;
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginModel.getUsername(), loginModel.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Cập nhật lastLogin sau khi đăng nhập thành công
            Optional<User> optionalUser = userRepository.findByUsername(loginModel.getUsername());
            if(optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setLastLogin(new Date());  // Đảm bảo rằng bạn có trường lastLogin là kiểu Instant hoặc LocalDateTime
                userRepository.save(user);
            }

            Principal userPrinciple = (Principal) authentication.getPrincipal();
            String accessToken = jwtProvider.createToken(userPrinciple);
            String refreshToken = refreshTokenProvider.createRefreshToken(loginModel.getUsername()).getToken();
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseObject(HttpStatus.ACCEPTED.toString(), "Login success!", new JwtResponse(accessToken, refreshToken)));
        } catch (AuthenticationException e) {
            if (e instanceof DisabledException) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(), "Account has been locked. Please contact " + companyEmail + " for more information", null));
            }
            if(e instanceof AccountExpiredException){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(), "The account has expired. Please contact " + companyEmail + " for more information",null));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(), "Invalid email or password. Please try again.", null));
        }
    }

    @Override
    public ResponseEntity<?> logout(HttpServletRequest request, LogoutForm logoutModel) {
        //delete refresh token
        refreshTokenProvider.deleteByToken(logoutModel.getRefreshToken());
        //set access token into black list to prevent reused.
        String accessToken = jwtProvider.getJwt(request);
        Instant expiredTime = jwtProvider.getAccessTokenExpiredTime(accessToken).toInstant();


        //For cache
        this.clearRefreshTokenCache(logoutModel.getRefreshToken());
        this.clearUserDetailsCache(jwtProvider.getUsernameFromToken(accessToken));

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseObject(HttpStatus.ACCEPTED.toString(), "Logout success!", new RefreshTokenResponse(null, null)));

    }

    @Override
    public ResponseEntity<?> validateAccessToken() {
        Principal userPrinciple = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PrincipalDTO principalDTO = mapper.map(userPrinciple, PrincipalDTO.class);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseObject(HttpStatus.ACCEPTED.toString(), "Validate access token success!", principalDTO));
    }

    @Override
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, RefreshTokenRequest refreshTokenModel) {
        String accessToken = jwtProvider.getJwt(request);
        try {
            jwtProvider.validateTokenThrowException(accessToken);
        } catch (Exception e) {
            if (e instanceof ExpiredJwtException) {
                return refreshTokenProvider.findByToken(DigestUtils.sha3_256Hex(refreshTokenModel.getRefreshToken()))
                        .map(refreshTokenProvider::verifyExpiration)
                        .map(refreshToken -> (User) refreshToken.getUser())
                        .map(user -> {
                            Principal principal = Principal.build(user);
                            String newAccessToken = jwtProvider.createToken(principal);
                            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseObject(HttpStatus.ACCEPTED.toString(), "Refresh token success!",  new RefreshTokenResponse(newAccessToken, refreshTokenModel.getRefreshToken())));
                        })
                        .orElseThrow(() -> new RefreshTokenException("Refresh token is not in database!"));
            }
            throw new JwtTokenException("Error -> Unauthorized");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject(HttpStatus.UNAUTHORIZED.toString(), "Cannot create new access token -> Old access token is not expired", null));
    }

    @Override
    public ResponseEntity<?> validateLoginForm(LoginForm loginModel) {
        if ((loginModel.getUsername().isEmpty() || loginModel.getUsername().isBlank()) && (loginModel.getPassword().isEmpty() || loginModel.getPassword().isBlank())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Empty input field!",  null));
        } else if (loginModel.getUsername().isEmpty() || loginModel.getUsername().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Empty username!",  null));
        } else if (loginModel.getPassword().isEmpty() || loginModel.getPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Empty password!",  null));
        }
        return null;
    }    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }
    @Override
    public User findUserByName(String username) {
        return userRepository.findByUsername(username).orElseThrow(()->
                new NotFoundException( "User is not found"));
    }

    @Override
    public String changeUserPassword(ChangePasswordForm passwordModel) {
        User user =getCurrentAuthenticatedUser();
        if(user == null){
            throw new AuthenticationsException("User not authenticated");

        }
        if(!checkIfValidOldPassword(user, passwordModel.getOldPassword())){
            throw new InvalidPasswordException("invalid old password.");
        }
        if(!passwordModel.getNewPassword().equals(passwordModel.getConfirmNewPassword())){
            throw new PasswordMismatchException("New password and confirm password do not match");
        }
        changePassword(user, passwordModel.getNewPassword());
        return "Password changed successfully";
    }

//    @Override
//    public ResponseEntity<?> forgetPassword(ForgetPasswordForm form) {
//        List<User> users = userRepository.findByEmail(form.getEmail());
//
//        ForgetPasswordDTO response = new ForgetPasswordDTO();
//        response.setEmail(form.getEmail());
//
//        // If no users are found with the given email
//        if(users.isEmpty()){
//            response.setMessage("Email not found");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
//                    HttpStatus.NOT_FOUND.toString(), "Email not found", null
//            ));
//        }
//
//        User user = users.get(0);
//
//        Optional<PasswordResetToken> existingTokenOpt = passwordResetTokenRepository.findByUser(user);
//        if (existingTokenOpt.isPresent()) {
//            PasswordResetToken existingToken = existingTokenOpt.get();
//            passwordResetTokenRepository.delete(existingToken);
//            passwordResetTokenRepository.flush();
//
//        }
//
//        String token = UUID.randomUUID().toString();
//        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
//        // Optionally, set the expiry date for the token here
//
//        passwordResetTokenRepository.save(passwordResetToken);
//        emailService.sendPasswordResetEmail(user.getEmail(), token);
//
//        response.setMessage("If this email address is registered, a password reset link has been sent.");
//        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(
//                HttpStatus.OK.toString(), response.getMessage(), response
//        ));
//    }




    private Date calculateExpiryDate(int expiryTimeMinutes){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, expiryTimeMinutes);
        return new Date(calendar.getTime().getTime());
    }

    private void clearRefreshTokenCache(String refreshToken) {
        boolean result = cacheManager.getCache("refreshToken").evictIfPresent(refreshToken);
        if (result) {
            log.info("Clear refresh token " + refreshToken + " from cache");
        } else {
            log.error("Fail clear refresh token " + refreshToken + " from cache");
        }
    }
    private void clearUserDetailsCache(String username) {
        boolean result = cacheManager.getCache("userDetails").evictIfPresent(username);
        if (result) {
            log.info("Clear account " + username + " from cache");
        } else {
            log.error("Fail clear account " + username + " from cache");
        }
    }
    public User getCurrentAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return findUserByName(username); // Assuming you have this method in your repo
        }

        return null; // Return null or throw an exception if the user is not authenticated
    }
}
