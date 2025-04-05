package com.services.wallet.domain.services;

import com.services.wallet.domain.entities.Balance;
import com.services.wallet.domain.entities.Wallet;
import com.services.wallet.domain.exceptions.business.InsufficientFundsException;
import com.services.wallet.domain.exceptions.business.InvalidAmountException;
import com.services.wallet.domain.gateways.BalanceSnapshotService;
import com.services.wallet.domain.gateways.TransactionService;
import com.services.wallet.domain.gateways.WalletRepositoryGateway;
import com.services.wallet.domain.gateways.WalletService;
import com.services.wallet.fixtures.Fixtures;
import com.services.wallet.resources.exceptions.ResourceJpaException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.services.wallet.domain.entities.enums.Currency.BRL;
import static com.services.wallet.domain.entities.enums.OperationType.CREDIT;
import static com.services.wallet.domain.entities.enums.OperationType.DEBIT;
import static com.services.wallet.domain.exceptions.business.InsufficientFundsException.insufficientFundsExceptionMessage;
import static com.services.wallet.domain.exceptions.business.InvalidAmountException.invalidAmountExceptionMessage;
import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepositoryGateway walletRepositoryGateway;

    @Mock
    private BalanceSnapshotService balanceSnapshotService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Fixtures.wallet = new Wallet(Fixtures.client, BRL,  ZERO);
        Fixtures.walletFull = new Wallet(Fixtures.client, BRL, Fixtures.ONE_HUNDRED);
    }

    @AfterEach
    void afterEach(){
        Mockito.reset(walletRepositoryGateway, balanceSnapshotService, transactionService);
    }

    @Test
    void testSaveWallet_Success() {
        when(walletRepositoryGateway.saveWallet(any(Wallet.class))).thenReturn(Fixtures.wallet);

        Wallet savedWallet = walletService.saveWallet(Fixtures.wallet);

        assertNotNull(savedWallet);
        Assertions.assertEquals(Fixtures.wallet.getClient().documentNumber(), savedWallet.getClient().documentNumber());
        verify(walletRepositoryGateway, times(1)).saveWallet(Fixtures.wallet);
    }

    @Test
    void testSaveWallet_Exception() {
        when(walletRepositoryGateway.saveWallet(Fixtures.wallet)).thenThrow(new RuntimeException("DB error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> walletService.saveWallet(Fixtures.wallet));

        assertEquals("DB error", exception.getMessage());
    }

    @Test
    void testGetCurrentBalance_Success() {
        when(walletRepositoryGateway.findWalletByClientDocumentNumber(Fixtures.wallet.getClient().documentNumber())).thenReturn(Fixtures.wallet);

        Balance balance = walletService.getCurrentBalance(Fixtures.wallet.getClient().documentNumber());

        assertNotNull(balance);
        Assertions.assertEquals(Fixtures.wallet.getBalance(), balance.amount());
    }

    @Test
    void testGetCurrentBalance_Exception() {
        when(walletRepositoryGateway.findWalletByClientDocumentNumber(Fixtures.CLIENT_DOCUMENT_NUMBER))
                .thenThrow(Fixtures.internalErrorResourceJpaException);

        ResourceJpaException exception = assertThrows(ResourceJpaException.class, () ->
                walletService.getCurrentBalance(Fixtures.CLIENT_DOCUMENT_NUMBER));

        Assertions.assertEquals(Fixtures.INVALID_CLIENT_EXCEPTION, exception.getMessage());
    }

    @Test
    void testGetBalanceForDate_Success() {
        when(balanceSnapshotService.getBalanceSnapshotForDate(
                Fixtures.balanceSnapshot.clientDocumentNumber(), Fixtures.balanceSnapshot.snapshotDate())
        ).thenReturn(Fixtures.balanceSnapshot);

        Balance balance = walletService.getBalanceForDate(Fixtures.balanceSnapshot.clientDocumentNumber(), Fixtures.balanceSnapshot.snapshotDate());

        assertNotNull(balance);
        Assertions.assertEquals(Fixtures.balanceSnapshot.balance(), balance.amount());
        Assertions.assertEquals(Fixtures.balanceSnapshot.snapshotDate(), balance.date());
    }

    @Test
    void testGetBalanceForDate_Exception() {
        when(balanceSnapshotService.getBalanceSnapshotForDate(
                Fixtures.balanceSnapshot.clientDocumentNumber(), Fixtures.balanceSnapshot.snapshotDate())
        ).thenThrow(new ResourceJpaException("DB Error"));

        ResourceJpaException exception = assertThrows(ResourceJpaException.class, () ->
            walletService.getBalanceForDate(Fixtures.balanceSnapshot.clientDocumentNumber(), Fixtures.balanceSnapshot.snapshotDate())
        );

        assertEquals("DB Error", exception.getMessage());
    }


    @Test
    void testDebit_Success() {
        when(walletRepositoryGateway.findWalletByClientDocumentNumber(Fixtures.debitRequest.documentNumber())).thenReturn(Fixtures.walletFull);

        assertDoesNotThrow(() -> walletService.debit(Fixtures.debitRequest));

        verify(walletRepositoryGateway, times(1)).saveWallet(Fixtures.walletFull);
        verify(transactionService, times(1)).saveTransaction(Fixtures.walletFull, Fixtures.walletFull.getBalance(), DEBIT);
    }

    @Test
    void testDebit_InvalidAmount() {
        when(walletRepositoryGateway.findWalletByClientDocumentNumber(Fixtures.invalidDebitRequest.documentNumber())).thenReturn(Fixtures.wallet);

        InvalidAmountException exception = assertThrows(InvalidAmountException.class, () ->
                walletService.debit(Fixtures.invalidDebitRequest)
        );

        assertEquals(invalidAmountExceptionMessage, exception.getMessage());
    }

    @Test
    void testDebit_InsufficientFunds() {
        when(walletRepositoryGateway.findWalletByClientDocumentNumber(Fixtures.debitRequest.documentNumber())).thenReturn(Fixtures.wallet);

        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () ->
                walletService.debit(Fixtures.debitRequest)
        );

        assertEquals(insufficientFundsExceptionMessage, exception.getMessage());
    }

    @Test
    void testDebit_Exception() {;
        when(walletRepositoryGateway.findWalletByClientDocumentNumber(Fixtures.debitRequest.documentNumber()))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                walletService.debit(Fixtures.debitRequest));

        assertEquals("DB error", exception.getMessage());
    }

    @Test
    void testCredit_Success() {
        when(walletRepositoryGateway.findWalletByClientDocumentNumber(Fixtures.creditRequest.documentNumber())).thenReturn(Fixtures.wallet);

        assertDoesNotThrow(() -> walletService.credit(Fixtures.creditRequest));

        verify(walletRepositoryGateway, times(1)).saveWallet(Fixtures.wallet);
        verify(transactionService, times(1)).saveTransaction(Fixtures.wallet, Fixtures.wallet.getBalance(), CREDIT);
    }

    @Test
    void testCredit_InvalidAmount() {
        when(walletRepositoryGateway.findWalletByClientDocumentNumber(Fixtures.invalidCreditRequest.documentNumber())).thenReturn(Fixtures.wallet);

        InvalidAmountException exception = assertThrows(InvalidAmountException.class, () ->
                walletService.credit(Fixtures.invalidCreditRequest)
        );

        assertEquals(invalidAmountExceptionMessage, exception.getMessage());
    }

    @Test
    void testCredit_Exception() {
        when(walletRepositoryGateway.findWalletByClientDocumentNumber(Fixtures.creditRequest.documentNumber()))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                walletService.credit(Fixtures.creditRequest));

        assertEquals("DB error", exception.getMessage());
    }

    @Test
    void testTransfer_Success() {
        when(walletRepositoryGateway.findWalletByClientDocumentNumber(Fixtures.senderDebitRequest.documentNumber())).thenReturn(Fixtures.walletFull);
        when(walletRepositoryGateway.findWalletByClientDocumentNumber(Fixtures.recipientCreditRequest.documentNumber())).thenReturn(Fixtures.walletPj);

        assertDoesNotThrow(() -> walletService.transfer(Fixtures.transferRequest));

        verify(walletRepositoryGateway, times(1)).findWalletByClientDocumentNumber(Fixtures.senderDebitRequest.documentNumber());
        verify(walletRepositoryGateway, times(1)).findWalletByClientDocumentNumber(Fixtures.recipientCreditRequest.documentNumber());
    }

    @Test
    void testTransfer_TransferFailure_InsufficientFundsException() {
        when(walletRepositoryGateway.findWalletByClientDocumentNumber(Fixtures.senderDebitRequest.documentNumber())).thenReturn(Fixtures.wallet);
        when(walletRepositoryGateway.findWalletByClientDocumentNumber(Fixtures.recipientCreditRequest.documentNumber())).thenReturn(Fixtures.walletPj);

        assertThrows(InsufficientFundsException.class, () -> walletService.transfer(Fixtures.transferRequest));

        verify(walletRepositoryGateway, times(1)).findWalletByClientDocumentNumber(Fixtures.senderDebitRequest.documentNumber());
        verify(walletRepositoryGateway, times(0)).findWalletByClientDocumentNumber(Fixtures.recipientCreditRequest.documentNumber());
    }

    @Test
    void testTransfer_TransferFailure_InvalidAmountException() {
        when(walletRepositoryGateway.findWalletByClientDocumentNumber(Fixtures.senderDebitRequest.documentNumber())).thenReturn(Fixtures.walletFull);
        when(walletRepositoryGateway.findWalletByClientDocumentNumber(Fixtures.recipientCreditRequest.documentNumber())).thenReturn(Fixtures.walletPj);

        assertThrows(InvalidAmountException.class, () -> walletService.transfer(Fixtures.invalidTransferRequest));

        verify(walletRepositoryGateway, times(1)).findWalletByClientDocumentNumber(Fixtures.senderDebitRequest.documentNumber());
        verify(walletRepositoryGateway, times(0)).findWalletByClientDocumentNumber(Fixtures.recipientCreditRequest.documentNumber());
    }
}
