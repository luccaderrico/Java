package com.services.wallet.application.web.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.services.wallet.domain.entities.Wallet;
import jakarta.validation.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.*;

import java.math.*;

import static com.services.wallet.application.web.dtos.ClientDto.*;

@JsonNaming(SnakeCaseStrategy.class)
public record WalletDto(

        @Valid
        @NotNull(message = "client is required")
        ClientDto client,
        @Nullable
        String currency,
        @Nullable
        BigDecimal balance
) {
    public static Wallet toWallet(WalletDto walletDto) {
        return new Wallet(
            toClient(walletDto.client)
        );
    }

    public static WalletDto toWalletDto(Wallet wallet) {
        return new WalletDto(
            toClientDto(wallet.getClient()),
            wallet.getCurrency().name(),
            wallet.getBalance()
        );
    }
}
