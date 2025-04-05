package com.services.wallet.domain.gateways;

import com.services.wallet.domain.entities.Wallet;

public interface WalletRepositoryGateway {
    Wallet saveWallet(Wallet wallet);

    Wallet findWalletByClientDocumentNumber(String documentNumber);
}
