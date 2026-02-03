package com.vbooking.backend.modules.auth.service;

import com.vbooking.backend.infrastructure.security.UserPrincipal;
import com.vbooking.backend.modules.user.entity.UserEntity;
import com.vbooking.backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Tìm user theo email
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Convert sang UserPrincipal (Spring Security hiểu)
        return UserPrincipal.create(user);
    }
}