package com.services.wallet.domain.gateways;

import com.services.wallet.domain.entities.BalanceSnapshot;
import com.services.wallet.domain.entities.Transaction;
import jakarta.transaction.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

import java.math.*;
import java.time.*;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceSnapshotService {

    @Autowired
    private BalanceSnapshotRepositoryGateway balanceSnapshotRepositoryGateway;

    @Autowired
    private ClientRepositoryGateway clientRepositoryGateway;

    @Autowired
    TransactionService transactionService;

    public BalanceSnapshot getBalanceSnapshotForDate(String clientDocumentNumber, LocalDateTime inputDate) {
        BalanceSnapshot lastSnapshot = findLastSnapshot(clientDocumentNumber);

        List<Transaction> transactions = transactionService.findTransactionsBetween(
                clientDocumentNumber, lastSnapshot.snapshotDate(), inputDate
        );

        return lastSnapshot.calculateNewSnapshot(transactions, inputDate);
    }

    @Transactional
    public BalanceSnapshot findLastSnapshot(String clientDocumentNumber) {
        return balanceSnapshotRepositoryGateway.findLastSnapshot(clientDocumentNumber)
                .orElse(
                    new BalanceSnapshot(
                        clientDocumentNumber,
                        BigDecimal.ZERO,
                        LocalDateTime.of(1970, 1, 1, 0, 0) // should be the client.createdAt
                    )
                );
    }
    @Scheduled(cron = "${api.snapshot.cron}")
    public void generateBalanceSnapshot() {
        log.info("Starting daily balance snapshot generation...");
        LocalDateTime now = LocalDateTime.now();

        try {
            List<String> clientDocumentNumbers = clientRepositoryGateway.findAllClientDocumentNumbers();

            clientDocumentNumbers.forEach(clientDocumentNumber -> {
                BalanceSnapshot snapshot = getBalanceSnapshotForDate(clientDocumentNumber, now);
                balanceSnapshotRepositoryGateway.save(snapshot);
                log.info("Snapshot updated for client: {} | Balance: {}", clientDocumentNumber, snapshot.balance());
            });
        } catch (Exception exc) {
            log.error("Failed to update daily balance snapshots: {}", exc.getLocalizedMessage());
        }


        log.info("Daily balance snapshot generation completed.");
    }
}
