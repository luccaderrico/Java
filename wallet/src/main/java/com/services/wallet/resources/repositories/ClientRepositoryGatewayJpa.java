package com.services.wallet.resources.repositories;

import com.services.wallet.resources.repositories.entities.ClientJpa;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface ClientRepositoryGatewayJpa extends JpaRepository<ClientJpa, String> {
    @Query("SELECT c.documentNumber FROM ClientJpa c")
    List<String> findAllClientDocumentNumber();
}
