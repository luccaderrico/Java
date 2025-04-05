package com.services.wallet.resources.repositories;

import com.services.wallet.resources.repositories.entities.WalletJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepositoryGatewayJpa extends JpaRepository<WalletJpa, Long> {
    WalletJpa findByClientDocumentNumber(String client);
}
