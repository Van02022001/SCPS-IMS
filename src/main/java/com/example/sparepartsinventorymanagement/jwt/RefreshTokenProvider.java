package com.example.sparepartsinventorymanagement.jwt;

import com.example.sparepartsinventorymanagement.entities.RefreshToken;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.RefreshTokenRepository;
import com.example.sparepartsinventorymanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class RefreshTokenProvider {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    CacheManager cacheManager;
    @Value("${app.refreshTokenDurationMs}")
    long refreshTokenDurationMs;

    @Cacheable("refreshToken")
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }


    public RefreshToken createRefreshToken(String username) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findByUsername(username).orElseThrow(()->
                new NotFoundException( " Username is not found")));

        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        RefreshToken refreshTokenEncrypt = new RefreshToken(refreshToken);
        refreshTokenEncrypt.setToken(DigestUtils.sha3_256Hex(refreshToken.getToken()));


        cacheManager.getCache("refreshToken").put(DigestUtils.sha3_256Hex(refreshToken.getToken()), refreshToken );

        refreshTokenRepository.save(refreshTokenEncrypt);
        return refreshToken;
    }


    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException("Refresh token was expired");
        }
        return token;
    }



    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
