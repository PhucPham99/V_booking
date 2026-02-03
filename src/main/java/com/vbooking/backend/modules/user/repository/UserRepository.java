package com.vbooking.backend.modules.user.repository;

import com.vbooking.backend.modules.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // Tìm user theo email (Dùng cho Login & LoadUserByUsername)
    Optional<UserEntity> findByEmail(String email);

    // Kiểm tra tồn tại (Dùng cho Register để tránh trùng lặp)
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

}