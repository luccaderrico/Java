package com.services.wallet.resources.repositories;

import com.services.wallet.resources.repositories.entities.BalanceSnapshotJpa;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface BalanceSnapshotRepositoryGatewayJpa extends JpaRepository<BalanceSnapshotJpa, Long> {
    Optional<BalanceSnapshotJpa> findTopByClientDocumentNumberOrderByCreatedAtDesc(String clientDocumentNumber);
}
