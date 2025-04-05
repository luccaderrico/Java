package com.services.wallet.domain.services;

import com.services.wallet.domain.entities.Transaction;
import com.services.wallet.domain.entities.Wallet;
import com.services.wallet.domain.entities.enums.OperationType;
import com.services.wallet.domain.gateways.TransactionRepositoryGateway;
import com.services.wallet.domain.gateways.TransactionService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.math.*;
import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepositoryGateway transactionRepositoryGateway;

    @InjectMocks
    private TransactionService transactionService;

    private Wallet wallet;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        wallet = new Wallet(); // Assume necessary setup
        transaction = new Transaction(wallet, BigDecimal.TEN, OperationType.CREDIT);
    }

    @Test
    void testSaveTransaction_Success() {
        assertDoesNotThrow(() -> transactionService.saveTransaction(wallet, BigDecimal.TEN, OperationType.CREDIT));
        verify(transactionRepositoryGateway, times(1)).saveTransaction(any(Transaction.class));
    }

    @Test
    void testSaveTransaction_Exception() {
        doThrow(new RuntimeException("DB error"))
                .when(transactionRepositoryGateway).saveTransaction(any(Transaction.class));

        Exception exception = assertThrows(RuntimeException.class, () ->
                transactionService.saveTransaction(wallet, BigDecimal.TEN, OperationType.CREDIT));

        assertEquals("DB error", exception.getMessage());
    }

    @Test
    void testFindTransactionsBetween_Success() {
        when(transactionRepositoryGateway.findTransactionsBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(transaction));

        List<Transaction> transactions = transactionService.findTransactionsBetween("12345", LocalDateTime.now().minusDays(1), LocalDateTime.now());

        assertFalse(transactions.isEmpty());
        verify(transactionRepositoryGateway, times(1)).findTransactionsBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testFindTransactionsBetween_Exception() {
        when(transactionRepositoryGateway.findTransactionsBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("DB error"));

        Exception exception = assertThrows(RuntimeException.class, () ->
                transactionService.findTransactionsBetween("12345", LocalDateTime.now().minusDays(1), LocalDateTime.now()));

        assertEquals("DB error", exception.getMessage());
    }
}
