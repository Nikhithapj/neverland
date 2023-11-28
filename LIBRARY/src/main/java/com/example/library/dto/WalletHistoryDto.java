package com.example.library.dto;


import com.example.library.enums.WalletTransactionType;
import com.example.library.model.Wallet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletHistoryDto {

    private Long id;

    @NotBlank
    @NotEmpty
    private double amount;

    private WalletTransactionType type;

    @NotBlank
    @NotEmpty
    private String transactionStatus;

    private Wallet wallet;

    private LocalDate transactionDate;

    private Long orderId;

}
