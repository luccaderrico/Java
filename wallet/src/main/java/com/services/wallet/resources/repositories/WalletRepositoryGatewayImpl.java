package com.services.wallet.resources.repositories;

import com.services.wallet.domain.entities.Wallet;
import com.services.wallet.domain.gateways.WalletRepositoryGateway;
import com.services.wallet.resources.exceptions.ResourceJpaException;
import com.services.wallet.resources.repositories.entities.WalletJpa;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

import static com.services.wallet.domain.exceptions.BusinessErrorType.NOT_FOUND;
import static org.springframework.http.HttpStatus.*;

@Component
@Slf4j
public class WalletRepositoryGatewayImpl implements WalletRepositoryGateway {

    @Autowired
    WalletRepositoryGatewayJpa walletRepositoryGatewayJpa;

    @Override
    public Wallet saveWallet(Wallet wallet) {
        try {
            Optional<WalletJpa> existingWalletJpaOpt = Optional
                    .ofNullable(walletRepositoryGatewayJpa.findByClientDocumentNumber(wallet.getClient().documentNumber()));

            WalletJpa walletJpa;

            if(existingWalletJpaOpt.isPresent()) {
                WalletJpa existingWalletJpa = existingWalletJpaOpt.get();
                existingWalletJpa.setBalance(wallet.getBalance());
                walletJpa = walletRepositoryGatewayJpa.save(existingWalletJpa);
            } else {
                walletJpa = WalletJpa.toWalletJpa(wallet);
                walletJpa = walletRepositoryGatewayJpa.save(walletJpa);
            }

            return WalletJpa.toWallet(walletJpa);
        } catch (Exception exc) {
            log.error("Error trying to save wallet: {}", exc.getMessage());
            throw new ResourceJpaException(exc.getLocalizedMessage());
        }
    }

    @Override
    public Wallet findWalletByClientDocumentNumber(String documentNumber) {
        try {
            WalletJpa walletJpa = walletRepositoryGatewayJpa.findByClientDocumentNumber(documentNumber);

            if (walletJpa == null) throw new ResourceJpaException("Invalid Client", INTERNAL_SERVER_ERROR, NOT_FOUND);

            return WalletJpa.toWallet(walletJpa);
        } catch (Exception exc) {
            log.error("Error searching for client`s wallet: {}", exc.getMessage());

            if (exc.getClass().equals(ResourceJpaException.class)) throw exc;

            throw new ResourceJpaException(exc.getLocalizedMessage());
        }
    }
}
