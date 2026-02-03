package com.vbooking.backend.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionTargetDTO {
    private Long targetId;
    private String targetType;
    private Long targetEntityId;
    private String targetName;
}
