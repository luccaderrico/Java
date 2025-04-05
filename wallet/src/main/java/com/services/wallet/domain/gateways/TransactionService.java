package com.services.wallet.domain.gateways;

import com.services.wallet.domain.entities.Transaction;
import com.services.wallet.domain.entities.Wallet;
import com.services.wallet.domain.entities.enums.OperationType;
import jakarta.transaction.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.math.*;
import java.time.*;
import java.util.*;

@Service
@Slf4j
public class TransactionService {

    @Autowired
    TransactionRepositoryGateway transactionRepositoryGateway;

    @Transactional
    public void saveTransaction(Wallet wallet, BigDecimal amount, OperationType operationType) {
        log.info("Starting process to save transaction");
        try {
            Transaction newTransaction = new Transaction(wallet, amount, operationType);
            transactionRepositoryGateway.saveTransaction(newTransaction);
        } catch (Exception exc) {
            log.error("Error while processing transaction {}", exc.getMessage());
            throw exc;
        }
    }

    @Transactional
    public List<Transaction> findTransactionsBetween(
        String clientDocumentNumber,
        LocalDateTime startDate,
        LocalDateTime endDate
    ) {
        log.info("Listing transactions between {} {}", startDate, endDate);
        try {
            return transactionRepositoryGateway.findTransactionsBetween(clientDocumentNumber, startDate, endDate);
        } catch (Exception exc) {
            log.error("Error while listing transactions {}", exc.getMessage());
            throw exc;
        }
    }
}
