package com.services.wallet.resources.repositories;

import com.services.wallet.domain.entities.Transaction;
import com.services.wallet.fixtures.Fixtures;
import com.services.wallet.resources.exceptions.ResourceJpaException;
import com.services.wallet.resources.repositories.TransactionRepositoryGatewayImpl;
import com.services.wallet.resources.repositories.TransactionRepositoryGatewayJpa;
import com.services.wallet.resources.repositories.WalletRepositoryGatewayJpa;
import com.services.wallet.resources.repositories.entities.TransactionJpa;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionRepositoryGatewayImplTest {

    @Mock
    private TransactionRepositoryGatewayJpa transactionRepositoryGatewayJpa;

    @Mock
    private WalletRepositoryGatewayJpa walletRepositoryGatewayJpa;

    @InjectMocks
    private TransactionRepositoryGatewayImpl transactionRepositoryGatewayImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveTransaction_Success() {
        when(walletRepositoryGatewayJpa.findByClientDocumentNumber(Fixtures.credit.wallet().getClient().documentNumber()))
                .thenReturn(Fixtures.walletJpa);
        when(transactionRepositoryGatewayJpa.save(any(TransactionJpa.class))).thenReturn(Fixtures.creditJpa);

        assertDoesNotThrow(() -> transactionRepositoryGatewayImpl.saveTransaction(Fixtures.credit));
        verify(transactionRepositoryGatewayJpa, times(1)).save(any(TransactionJpa.class));
    }

    @Test
    void testSave_Exception() {
        doThrow(new RuntimeException("DB error")).when(transactionRepositoryGatewayJpa).save(any(TransactionJpa.class));

        ResourceJpaException thrownException = assertThrows(ResourceJpaException.class, () -> {
            transactionRepositoryGatewayImpl.saveTransaction(Fixtures.credit);
        });

        assertNotNull(thrownException);
        assertEquals("DB error", thrownException.getMessage());
    }


    @Test
    void testFindTransactionsBetween_Success() {
        when(transactionRepositoryGatewayJpa.findByWalletClientDocumentNumberAndCreatedAtBetween(
                anyString(), any(LocalDateTime.class), any(LocalDateTime.class))
        ).thenReturn(List.of(Fixtures.creditJpa));

        List<Transaction> transactions = assertDoesNotThrow(() -> transactionRepositoryGatewayImpl.findTransactionsBetween(
                Fixtures.CLIENT_DOCUMENT_NUMBER, LocalDateTime.now().minusDays(1), LocalDateTime.now())
        );

        assertFalse(transactions.isEmpty());
        verify(transactionRepositoryGatewayJpa, times(1)).findByWalletClientDocumentNumberAndCreatedAtBetween(
                anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testFindTransactionsBetween_Exception() {
        doThrow(new RuntimeException("DB error")).when(
                transactionRepositoryGatewayJpa).findByWalletClientDocumentNumberAndCreatedAtBetween(
                    anyString(), any(LocalDateTime.class), any(LocalDateTime.class)
                );

        ResourceJpaException thrownException = assertThrows(ResourceJpaException.class, () ->
            transactionRepositoryGatewayImpl.findTransactionsBetween(
                Fixtures.CLIENT_DOCUMENT_NUMBER, LocalDateTime.now().minusDays(1), LocalDateTime.now()
            )
        );

        assertNotNull(thrownException);
        assertEquals("DB error", thrownException.getMessage());
        verify(transactionRepositoryGatewayJpa, times(1)).findByWalletClientDocumentNumberAndCreatedAtBetween(
                anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
