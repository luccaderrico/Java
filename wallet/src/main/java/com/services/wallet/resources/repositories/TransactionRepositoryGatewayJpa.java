package com.services.wallet.resources.repositories;

import com.services.wallet.resources.repositories.entities.TransactionJpa;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.time.*;
import java.util.*;

@Repository
public interface TransactionRepositoryGatewayJpa extends JpaRepository<TransactionJpa, Long> {

    List<TransactionJpa> findByWalletClientDocumentNumberAndCreatedAtBetween(
            String clientDocumentNumber,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
