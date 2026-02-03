package com.vbooking.backend.infrastructure.security;

import com.vbooking.backend.modules.user.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class UserPrincipal implements UserDetails {

    private Long id;
    private String email;
    private String fullName; // Thêm fullName
    private String role; // Thêm role (raw string: guest, admin, partner, staff)
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean active;

    public static UserPrincipal create(UserEntity user) {
        // Convert Role từ String sang GrantedAuthority
        // Ví dụ: DB lưu "admin" -> Spring hiểu là "ROLE_ADMIN"
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()));

        return UserPrincipal.builder()
                .id(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .active(user.getIsActive())
                .build();
    }

    @Override
    public String getUsername() {
        return email; // Dùng email làm username đăng nhập
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}