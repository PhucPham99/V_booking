package com.vbooking.backend.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.vbooking.backend.modules.auth.entity.RefreshTokenEntity;
import com.vbooking.backend.modules.auth.repository.RefreshTokenRepository;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    // Inject thêm Repository để check DB
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

            // --- LOGIC BỔ SUNG CỦA BẠN TẠI ĐÂY ---

            // 1. Lấy tokenId từ trong Access Token
            String tokenId = jwtTokenProvider.getTokenIdFromJwt(token);

            // 2. Query DB xem cái Refresh Token tương ứng (tokenId) có bị revoke không?
            // (Giả sử bạn dùng tokenId làm key để tìm trong bảng refresh_tokens)
            var isRevoked = refreshTokenRepository.findByToken(tokenId)
                    .map(RefreshTokenEntity::getIsRevoked)
                    .orElse(true); // Nếu không tìm thấy trong DB -> Coi như đã revoke

            // 3. Nếu đã Revoke -> Chặn ngay lập tức
            if (isRevoked) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token has been revoked");
                return; // Dừng lại, không cho đi tiếp
            }

            // -------------------------------------

            // 4. Set Authentication vào Security Context
            String username = jwtTokenProvider.getUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Kiểm tra xem header có chứa chữ "Bearer " không
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Cắt bỏ 7 ký tự đầu ("Bearer ") để lấy đúng chuỗi Token
        }

        return null;
    }
}