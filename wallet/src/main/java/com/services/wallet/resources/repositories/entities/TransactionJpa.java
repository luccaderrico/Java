package com.services.wallet.resources.repositories.entities;

import com.services.wallet.domain.entities.Transaction;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;

import java.math.*;
import java.time.*;

import static com.services.wallet.domain.entities.enums.OperationType.*;
import static jakarta.persistence.GenerationType.*;

@Entity
@Table(name = "transaction")
@AllArgsConstructor
@NoArgsConstructor @Getter @Setter
public class TransactionJpa {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "transaction_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private WalletJpa wallet;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "type")
    private String operationType;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public TransactionJpa(WalletJpa wallet, BigDecimal amount, String operationType) {
        this.wallet = wallet;
        this.amount = amount;
        this.operationType = operationType;
    }

    public static TransactionJpa toTransactionJpa(WalletJpa wallet, Transaction transaction) {
        return new TransactionJpa(
                wallet,
                transaction.amount(),
                transaction.type().name()
        );
    }

    public static Transaction toTransaction(TransactionJpa transactionJpa) {
        return new Transaction(
                WalletJpa.toWallet(transactionJpa.wallet),
                transactionJpa.amount,
                toOperationType(transactionJpa.operationType),
                transactionJpa.createdAt
        );
    }
}
