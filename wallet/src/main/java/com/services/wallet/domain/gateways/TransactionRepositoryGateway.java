package com.services.wallet.domain.gateways;

import com.services.wallet.domain.entities.Transaction;

import java.time.*;
import java.util.*;

public interface TransactionRepositoryGateway {
    void saveTransaction(Transaction transaction);

    List<Transaction> findTransactionsBetween(String clientDocumentNumber, LocalDateTime startDate, LocalDateTime endDate);
}
