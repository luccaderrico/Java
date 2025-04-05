package com.services.wallet.resources.repositories;

import com.services.wallet.domain.gateways.ClientRepositoryGateway;
import com.services.wallet.resources.exceptions.ResourceJpaException;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Component
@Slf4j
public class ClientRepositoryGatewayImpl implements ClientRepositoryGateway {

    @Autowired
    ClientRepositoryGatewayJpa clientRepositoryGatewayJpa;

    @Override
    public List<String> findAllClientDocumentNumbers() {
        try {
            return clientRepositoryGatewayJpa.findAllClientDocumentNumber();
        } catch (Exception exc) {
            log.error("Error searching for client`s document: {}", exc.getLocalizedMessage());
            throw new ResourceJpaException(exc.getLocalizedMessage());
        }
    }
}
