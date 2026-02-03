package com.vbooking.backend.dto.partner;

import com.vbooking.backend.dto.wallet.WalletDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerDTO {
    private Long partnerId;
    private Long userId;
    private String companyName;
    private String taxCode;
    private String email;
    private String phone;
    private String address;
    private String status;
    private LocalDateTime createdAt;
    private WalletDTO wallet;
}
