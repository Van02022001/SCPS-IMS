package com.example.sparepartsinventorymanagement.service.impl;

import com.example.sparepartsinventorymanagement.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public void sendAccountDetail(String email, String username, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("qvanwork@outlook.com.vn");
        message.setTo(email);
        message.setSubject("Thông tin tài khoản mới của bạn");
        message.setText("Tai khoản của bạn là : " +
                     "\n username: " + username +
                     "\n password: " + password +
                     "\n Vui lòng đăng nhập và thay đổi mật khẩu.");
        emailSender.send(message);
    }
}
