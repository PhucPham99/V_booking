package com.vbooking.backend.modules.auth.repository;

import com.vbooking.backend.modules.auth.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

        // 1. Dùng cho Stateful Check & Refresh Token (Vẫn dùng Magic Method là đủ
        // nhanh)
        Optional<RefreshTokenEntity> findByToken(String token);

        // 2. TỐI ƯU HÓA LOGOUT: Tự viết Query để Revoke luôn (Chỉ tốn 1 lần gọi DB)
        @Modifying // Báo hiệu đây là câu lệnh thay đổi dữ liệu (Update/Delete)
        @Query("UPDATE RefreshTokenEntity r SET r.isRevoked = true WHERE r.token = :token")
        void revokeByToken(@Param("token") String token);

        // 3. LOGIC NGẦM: Kiểm tra nhanh xem token còn sống không (Trả về boolean)
        // Query này tối ưu hơn findByToken vì nó chỉ check true/false, không cần map ra
        // cả object
        @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
                        "FROM RefreshTokenEntity r WHERE r.token = :token AND r.isRevoked = false")
        boolean isTokenActive(@Param("token") String token);

        // 4. SESSION MANAGEMENT: Lấy tất cả sessions đang active của user
        @Query("SELECT r FROM RefreshTokenEntity r WHERE r.userId = :userId AND r.isRevoked = false " +
                        "ORDER BY r.createdAt DESC")
        List<RefreshTokenEntity> findActiveSessionsByUserId(@Param("userId") Long userId);

        // 5. SESSION MANAGEMENT: Revoke tất cả sessions của user (Logout all devices)
        @Modifying
        @Query("UPDATE RefreshTokenEntity r SET r.isRevoked = true WHERE r.userId = :userId")
        void revokeAllByUserId(@Param("userId") Long userId);

        // 6. ANALYTICS: Đếm số lượng session đã từng tạo của User
        // Dùng để kiểm tra First-Time Login logic (<= 1)
        // Hiệu năng: Cực nhanh nhờ Index trên user_id (V7 migration)
        long countByUserId(Long userId);
}