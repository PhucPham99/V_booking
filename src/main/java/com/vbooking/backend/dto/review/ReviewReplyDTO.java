package com.vbooking.backend.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReplyDTO {
    private String partnerName;
    private String replyText;
    private LocalDateTime replyAt;
}
