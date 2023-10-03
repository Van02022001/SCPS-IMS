package com.example.sparepartsinventorymanagement.jwt.userprincipal;

import com.example.sparepartsinventorymanagement.entities.User;
import com.example.sparepartsinventorymanagement.exception.NotFoundException;
import com.example.sparepartsinventorymanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Cacheable(value = "userDetails")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(()->
                new NotFoundException( "Username is not found"));
        if (user == null) {
            throw new NotFoundException( "Username is not found");
        }


        return Principal.build(user);

    }
}
