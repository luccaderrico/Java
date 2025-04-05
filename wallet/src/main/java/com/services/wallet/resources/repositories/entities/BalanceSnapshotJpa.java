package com.services.wallet.resources.repositories.entities;

import com.services.wallet.domain.entities.BalanceSnapshot;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;

import java.math.*;
import java.time.*;

import static jakarta.persistence.GenerationType.*;

@Entity
@Table(name = "balance_snapshot")
@AllArgsConstructor
@NoArgsConstructor @Getter @Setter
public class BalanceSnapshotJpa {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "SNAPSHOT_ID")
    private Long id;

    @Column(name = "CLIENT_DOCUMENT_NUMBER", nullable = false)
    private String clientDocumentNumber;

    @Column(name = "BALANCE", nullable = false)
    private BigDecimal balance;

    @Column(name = "CREATED_AT", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public BalanceSnapshotJpa(String clientDocumentNumber, BigDecimal balance) {
        this.clientDocumentNumber = clientDocumentNumber;
        this.balance = balance;
    }

    public static BalanceSnapshot toBalanceSnapshot(BalanceSnapshotJpa balanceSnapshotJpa) {
        return new BalanceSnapshot(
                balanceSnapshotJpa.getClientDocumentNumber(),
                balanceSnapshotJpa.getBalance(),
                balanceSnapshotJpa.getCreatedAt()
        );
    }

    public static BalanceSnapshotJpa toBalanceSnapshotJpa(BalanceSnapshot balanceSnapshot) {
        return new BalanceSnapshotJpa(
                balanceSnapshot.clientDocumentNumber(),
                balanceSnapshot.balance()
        );
    }
}
