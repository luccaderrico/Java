package com.services.wallet.resources.repositories.entities;

import com.services.wallet.domain.entities.Wallet;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;

import java.math.*;
import java.time.*;
import java.util.*;

import static com.services.wallet.domain.entities.enums.Currency.*;
import static com.services.wallet.domain.entities.enums.Status.*;
import static com.services.wallet.resources.repositories.entities.ClientJpa.*;
import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.GenerationType.*;

@Entity
@Table(name = "wallet")
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class WalletJpa {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "wallet_id")
    private Long id;

    @OneToOne(cascade = ALL)
    @JoinColumn(name = "client_document_number", referencedColumnName = "DOCUMENT_NUMBER")
    private ClientJpa client;

    @Column(name = "currency")
    private String currency;

    @Column(name = "balance")
    private BigDecimal balance;

    @OneToMany(mappedBy = "wallet", cascade = REMOVE, orphanRemoval = true)
    private List<TransactionJpa> transactions;

    @Column(name = "status")
    private String status = ACTIVE.name();

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public WalletJpa(ClientJpa client, String currency, BigDecimal balance) {
        this.client = client;
        this.currency = currency;
        this.balance = balance;
    }
    public static WalletJpa toWalletJpa(Wallet wallet) {
        return new WalletJpa(
                toClientJpa(wallet.getClient()),
                wallet.getCurrency().name(),
                wallet.getBalance()
        );
    }

    public static Wallet toWallet(WalletJpa walletJpa) {
        return new Wallet(
                toClient(walletJpa.client),
                toCurrency(walletJpa.currency),
                walletJpa.balance
        );
    }
}
