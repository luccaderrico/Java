package com.services.wallet.resources.repositories;

import com.services.wallet.domain.entities.BalanceSnapshot;
import com.services.wallet.domain.gateways.BalanceSnapshotRepositoryGateway;
import com.services.wallet.resources.exceptions.ResourceJpaException;
import com.services.wallet.resources.repositories.entities.BalanceSnapshotJpa;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Component
@Slf4j
public class BalanceSnapshotRepositoryGatewayImpl implements BalanceSnapshotRepositoryGateway {

    @Autowired
    private BalanceSnapshotRepositoryGatewayJpa balanceSnapshotRepositoryGatewayJpa;

    @Override
    public Optional<BalanceSnapshot> findLastSnapshot(String clientDocumentNumber) {
        try {
            return balanceSnapshotRepositoryGatewayJpa.findTopByClientDocumentNumberOrderByCreatedAtDesc(clientDocumentNumber)
                    .map(BalanceSnapshotJpa::toBalanceSnapshot);
        } catch (Exception exc) {
            log.error("Error searching for the last snapshot: {}", exc.getLocalizedMessage());
            throw new ResourceJpaException(exc.getLocalizedMessage());
        }
    }

    @Override
    public void save(BalanceSnapshot balanceSnapshot) {
        try {
            balanceSnapshotRepositoryGatewayJpa.save(BalanceSnapshotJpa.toBalanceSnapshotJpa(balanceSnapshot));
        } catch (Exception exc) {
            log.error("Error saving balance snapshot");
            throw new ResourceJpaException(exc.getLocalizedMessage());
        }
    }
}
