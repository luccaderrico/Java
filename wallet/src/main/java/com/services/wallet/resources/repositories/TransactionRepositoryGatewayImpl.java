package com.services.wallet.resources.repositories;

import com.services.wallet.domain.entities.Transaction;
import com.services.wallet.domain.gateways.TransactionRepositoryGateway;
import com.services.wallet.resources.exceptions.ResourceJpaException;
import com.services.wallet.resources.repositories.entities.TransactionJpa;
import com.services.wallet.resources.repositories.entities.WalletJpa;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.time.*;
import java.util.*;

@Component
@Slf4j
public class TransactionRepositoryGatewayImpl implements TransactionRepositoryGateway {

    @Autowired
    TransactionRepositoryGatewayJpa transactionRepositoryGatewayJpa;

    @Autowired
    WalletRepositoryGatewayJpa walletRepositoryGatewayJpa;

    @Override
    public void saveTransaction(Transaction transaction) {
        try {
            String clientDocumentNumber = transaction.wallet().getClient().documentNumber();
            WalletJpa wallet = walletRepositoryGatewayJpa.findByClientDocumentNumber(clientDocumentNumber);
            transactionRepositoryGatewayJpa.save(TransactionJpa.toTransactionJpa(wallet, transaction));
        } catch (Exception exc){
            log.error("Error while saving transaction {}", exc.getMessage());
            throw new ResourceJpaException(exc.getMessage());
        }
    }

    @Override
    public List<Transaction> findTransactionsBetween(
            String clientDocumentNumber,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        try {
            return transactionRepositoryGatewayJpa.findByWalletClientDocumentNumberAndCreatedAtBetween(clientDocumentNumber, startDate, endDate)
                    .stream()
                    .map(TransactionJpa::toTransaction)
                    .toList();
        } catch (Exception exc) {
            log.error("Error searching for transactions for given interval: {}", exc.getLocalizedMessage());
            throw new ResourceJpaException(exc.getLocalizedMessage());
        }
    }
}
