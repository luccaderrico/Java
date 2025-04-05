package com.services.wallet.application.web.controllers;

import com.services.wallet.application.web.annotations.CPF_CNPJ;
import com.services.wallet.application.web.dtos.BalanceDto;
import com.services.wallet.application.web.dtos.CreditRequestDto;
import com.services.wallet.application.web.dtos.DebitRequestDto;
import com.services.wallet.application.web.dtos.TransferRequestDto;
import com.services.wallet.application.web.dtos.WalletDto;
import com.services.wallet.domain.entities.Balance;
import com.services.wallet.domain.entities.Wallet;
import com.services.wallet.domain.gateways.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static com.services.wallet.application.web.dtos.BalanceDto.toBalanceDto;
import static com.services.wallet.application.web.dtos.CreditRequestDto.toCreditRequest;
import static com.services.wallet.application.web.dtos.DebitRequestDto.toDebitRequest;
import static com.services.wallet.application.web.dtos.TransferRequestDto.toTransferRequest;
import static com.services.wallet.application.web.dtos.WalletDto.toWallet;
import static com.services.wallet.application.web.dtos.WalletDto.toWalletDto;
import static com.services.wallet.application.web.utils.DocumentNumberValidator.formatDocumentNumber;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Validated
@RestController
@RequestMapping("/api/v1/wallet")
@Slf4j
public class WalletController {

    @Autowired
    private WalletService walletService;

    private final String CONTENT_TYPE_CREATE_WALLET = "application/json";

    @Operation(
        summary = "Create a new wallet",
        description = "This endpoint creates a new wallet for the client",
        responses = {
            @ApiResponse(responseCode = "200", description = "Wallet created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
        }
    )
    @PostMapping(value = "/create", consumes = CONTENT_TYPE_CREATE_WALLET)
    public ResponseEntity<WalletDto> createWallet(
            @Valid @RequestBody WalletDto createWalletRequest
    ) {
         try {
            log.info("Wallet creation request received: {}", createWalletRequest);
            Wallet createdWallet = walletService.saveWallet(toWallet(createWalletRequest));
            log.info("Wallet created successfully");
            return ResponseEntity.ok().body(toWalletDto(createdWallet));
         } catch (Exception exc) {
             log.error("Error creating wallet: {}", exc.getMessage());
             throw exc;
         }
    }

    @Operation(
        summary = "Get wallet balance",
        description = "Retrieve the balance of a wallet by client document number",
        responses = {
            @ApiResponse(responseCode = "200", description = "Balance retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid document number")
        }
    )
    @GetMapping("/{documentNumber}/balance")
    public ResponseEntity<BalanceDto> getBalance(
            @Valid  @PathVariable @CPF_CNPJ String documentNumber,
            @RequestParam(value="date", required = false) @DateTimeFormat(iso = DATE_TIME) LocalDateTime date
    ) {
        Balance balance;

        log.info("Retrieve wallet`s balance request received for client {}", documentNumber);

        try {
            documentNumber = formatDocumentNumber(documentNumber);

            if (date == null) {
                balance = walletService.getCurrentBalance(documentNumber);
            } else {
                balance = walletService.getBalanceForDate(documentNumber, date);
            }

            BalanceDto balanceDto = toBalanceDto(balance);
            return ResponseEntity.ok(balanceDto);
        } catch (Exception exc) {
            log.error("Error retrieving wallet`s balance: {}", exc.getMessage());
            throw exc;
        }
    }

    @Operation(
        summary = "Debit an amount from wallet",
        description = "Process a debit operation from a client’s wallet",
        responses = {
            @ApiResponse(responseCode = "200", description = "Debit completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid debit request")
        }
    )
    @PostMapping(value = "/debit", consumes = CONTENT_TYPE_CREATE_WALLET)
    public ResponseEntity<String> debit(
            @Valid @RequestBody DebitRequestDto debitRequestDto
    ) {
        try {
            log.info("Debit request received {}", debitRequestDto);
            walletService.debit(toDebitRequest(debitRequestDto));
            return ResponseEntity.ok().body("Debit complete successfully");
        } catch (Exception exc) {
            log.error("Error occurred while processing debit operation {}", exc.getMessage());
            throw exc;
        }
    }

    @Operation(
        summary = "Credit an amount to wallet",
        description = "Process a credit operation to a client’s wallet",
        responses = {
            @ApiResponse(responseCode = "200", description = "Credit completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid credit request")
        }
    )
    @PostMapping(value = "/credit", consumes = CONTENT_TYPE_CREATE_WALLET)
    public ResponseEntity<String> credit(
            @Valid @RequestBody CreditRequestDto creditRequestDto
    ) {
        try {
            log.info("Credit request received {}", creditRequestDto);
            walletService.credit(toCreditRequest(creditRequestDto));
            return ResponseEntity.ok().body("Credit complete successfully");
        } catch (Exception exc) {
            log.error("Error occurred while processing credit operation {}", exc.getMessage());
            throw exc;
        }
    }

    @Operation(
            summary = "Transfer an amount between wallets",
            description = "Process a credit operation to a client’s wallet",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transfer completed successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid transfer request")
            }
    )
    @PostMapping(value = "/transfer", consumes = CONTENT_TYPE_CREATE_WALLET)
    public ResponseEntity<String> transfer(
            @Valid @RequestBody TransferRequestDto transferRequestDto
    ) {
        try {
            log.info("Transfer request received {}", transferRequestDto);
            walletService.transfer(toTransferRequest(transferRequestDto));
            return ResponseEntity.ok().body("Transfer complete successfully");
        } catch (Exception exc) {
            log.error("Error occurred while processing transfer operation {}", exc.getMessage());
            throw exc;
        }
    }
}
