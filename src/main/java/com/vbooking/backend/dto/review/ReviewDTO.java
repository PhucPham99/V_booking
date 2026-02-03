package com.vbooking.backend.dto.review;

import com.vbooking.backend.dto.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long reviewId;
    private Long bookingId;
    private Long hotelId;
    private String hotelName;
    private UserDTO user;
    private Integer rating;
    private String comment;
    private List<String> images;
    private List<ReviewReplyDTO> replies;
    private String status;
    private LocalDateTime createdAt;
}
