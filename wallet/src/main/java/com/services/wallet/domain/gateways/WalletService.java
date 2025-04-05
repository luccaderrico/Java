package com.services.wallet.domain.gateways;

import com.services.wallet.domain.entities.Balance;
import com.services.wallet.domain.entities.BalanceSnapshot;
import com.services.wallet.domain.entities.CreditRequest;
import com.services.wallet.domain.entities.DebitRequest;
import com.services.wallet.domain.entities.TransferRequest;
import com.services.wallet.domain.entities.Wallet;
import jakarta.transaction.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.math.*;
import java.time.*;

import static com.services.wallet.domain.entities.enums.OperationType.*;

@Service
@Slf4j
public class WalletService {

    @Autowired
    WalletRepositoryGateway walletRepositoryGateway;

    @Autowired
    BalanceSnapshotService balanceSnapshotService;

    @Autowired
    TransactionService transactionService;

    public WalletService(WalletRepositoryGateway walletRepositoryGateway) {
        this.walletRepositoryGateway = walletRepositoryGateway;
    }

    @Transactional
    public Wallet saveWallet(Wallet wallet) {
        log.info("Saving wallet from client {}", wallet.getClient().name());
        Wallet createdWallet = walletRepositoryGateway.saveWallet(wallet);
        log.info("Wallet saved successfully");
        return createdWallet;
    }

    @Transactional
    public Balance getCurrentBalance(String documentNumber) {
       BigDecimal balance = walletRepositoryGateway.findWalletByClientDocumentNumber(documentNumber).getBalance();
       return new Balance(balance, LocalDateTime.now());
    }

    @Transactional
    public Balance getBalanceForDate(String clientDocumentNumber, LocalDateTime inputDate) {
        BalanceSnapshot balanceSnapshot = balanceSnapshotService.getBalanceSnapshotForDate(clientDocumentNumber, inputDate);
        return new Balance(balanceSnapshot.balance(), inputDate);
    }

    @Transactional
    public void debit(DebitRequest debitRequest) {
        try {
            log.info("Starting debit process to: {}", debitRequest.documentNumber());
            Wallet wallet = walletRepositoryGateway.findWalletByClientDocumentNumber(debitRequest.documentNumber());

            debitRequest.isValid(wallet.getBalance());

            BigDecimal updatedBalance = wallet.getBalance().subtract(debitRequest.amount());
            wallet.setBalance(updatedBalance);
            walletRepositoryGateway.saveWallet(wallet);

            log.info("Debit completed successfully for {}", debitRequest.documentNumber());
            transactionService.saveTransaction(wallet, debitRequest.amount(), DEBIT);
        } catch (Exception exc) {
            log.error("Error trying to complete debit: {}", exc.getMessage());
            throw exc;
        }
    }

    @Transactional
    public void credit(CreditRequest creditRequest) {
        try {
            Wallet wallet = walletRepositoryGateway.findWalletByClientDocumentNumber(creditRequest.documentNumber());;
            log.info("Starting credit process to {}.", creditRequest.documentNumber());

            creditRequest.isValid();

            BigDecimal updatedAmount = wallet.getBalance().add(creditRequest.amount());
            wallet.setBalance(updatedAmount);
            walletRepositoryGateway.saveWallet(wallet);

            log.info("Credit completed successfully for {}", creditRequest.documentNumber());
            transactionService.saveTransaction(wallet, creditRequest.amount(), CREDIT);
        } catch (Exception exc) {
            log.error("Error trying to complete credit: {}", exc.getMessage());
            throw exc;
        }
    }

    @Transactional
    public void transfer(TransferRequest transferRequest) {
        log.info("Starting transfer from {} to {}", transferRequest.sender(), transferRequest.sender());

        try {
            DebitRequest debitRequest = new DebitRequest(transferRequest.sender(), transferRequest.amount());
            debit(debitRequest);

            CreditRequest creditRequest = new CreditRequest(transferRequest.recipient(), transferRequest.amount());
            credit(creditRequest);
        } catch (Exception exc) {
            log.error("Error while making transfer {}", transferRequest.sender());
            throw exc;
        }
    }
}
