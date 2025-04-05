package com.services.wallet.domain.gateways;

import com.services.wallet.domain.entities.BalanceSnapshot;

import java.util.*;

public interface BalanceSnapshotRepositoryGateway {
    Optional<BalanceSnapshot> findLastSnapshot(String clientDocumentNumber);

    void save(BalanceSnapshot balanceSnapshot);
}
