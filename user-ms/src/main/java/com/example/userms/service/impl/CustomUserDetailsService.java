package com.example.userms.service.impl;

import com.example.userms.model.entity.User;
import com.example.userms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByUsernameOrEmail(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username or email: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                user.get().getUsername(),
                user.get().getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.get().getRole().getName().name()))
        );
    }
}

