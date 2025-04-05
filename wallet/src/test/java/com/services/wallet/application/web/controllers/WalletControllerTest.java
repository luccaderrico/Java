package com.services.wallet.application.web.controllers;

import com.services.wallet.application.web.controllers.WalletController;
import com.services.wallet.application.web.dtos.BalanceDto;
import com.services.wallet.application.web.dtos.WalletDto;
import com.services.wallet.domain.entities.Wallet;
import com.services.wallet.domain.gateways.WalletService;
import com.services.wallet.fixtures.Fixtures;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WalletControllerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateWallet() {
        when(walletService.saveWallet(any(Wallet.class))).thenReturn(Fixtures.wallet);

        ResponseEntity<WalletDto> response = walletController.createWallet(Fixtures.requestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(Fixtures.clientRequestDto.documentNumber(), response.getBody().client().documentNumber());
        Assertions.assertEquals(Fixtures.clientRequestDto.name(), response.getBody().client().name());

        verify(walletService, times(1)).saveWallet(any(Wallet.class));
    }

    @Test
    void testCreateWallet_Exception() {
        when(walletService.saveWallet(any(Wallet.class))).thenThrow(new RuntimeException("Service error"));

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            walletController.createWallet(Fixtures.requestDto);
        });

        assertNotNull(thrownException);
        assertEquals("Service error", thrownException.getMessage());

        verify(walletService, times(1)).saveWallet(any(Wallet.class));
    }

    @Test
    void testGetBalance() {
        when(walletService.getCurrentBalance(Fixtures.CLIENT_DOCUMENT_NUMBER)).thenReturn(Fixtures.balanceNow);

        ResponseEntity<BalanceDto> response = walletController.getBalance(Fixtures.CLIENT_DOCUMENT_NUMBER, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(Fixtures.ONE_HUNDRED, response.getBody().amount());

        verify(walletService, times(1)).getCurrentBalance(Fixtures.CLIENT_DOCUMENT_NUMBER);
    }

    @Test
    void testGetBalance_Exception() {
        when(walletService.getCurrentBalance(Fixtures.CLIENT_DOCUMENT_NUMBER)).thenThrow(new RuntimeException("Service error"));

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            walletController.getBalance(Fixtures.CLIENT_DOCUMENT_NUMBER, null);
        });

        assertNotNull(thrownException);
        assertEquals("Service error", thrownException.getMessage());
        verify(walletService, times(1)).getCurrentBalance(Fixtures.CLIENT_DOCUMENT_NUMBER);
    }

    @Test
    void testGetBalanceForDate() {
        when(walletService.getBalanceForDate(Fixtures.CLIENT_DOCUMENT_NUMBER, Fixtures.marchThirtieth)).thenReturn(Fixtures.balanceMarchThirtieth);

        ResponseEntity<BalanceDto> response = walletController.getBalance(Fixtures.CLIENT_DOCUMENT_NUMBER, Fixtures.marchThirtieth);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(Fixtures.ONE_HUNDRED_AND_FIFTH, response.getBody().amount());

        verify(walletService, times(1)).getBalanceForDate(Fixtures.CLIENT_DOCUMENT_NUMBER, Fixtures.marchThirtieth);
    }

    @Test
    void testGetBalanceForDate_Exception() {
        when(walletService.getBalanceForDate(Fixtures.CLIENT_DOCUMENT_NUMBER, Fixtures.marchThirtieth)).thenThrow(new RuntimeException("Service error"));

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            walletController.getBalance(Fixtures.CLIENT_DOCUMENT_NUMBER, Fixtures.marchThirtieth);
        });

        assertNotNull(thrownException);
        assertEquals("Service error", thrownException.getMessage());
        verify(walletService, times(1)).getBalanceForDate(Fixtures.CLIENT_DOCUMENT_NUMBER, Fixtures.marchThirtieth);
    }

    @Test
    void testDebit() {
        doNothing().when(walletService).debit(any());

        ResponseEntity<String> response = walletController.debit(Fixtures.debitRequestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Debit complete successfully", response.getBody());

        verify(walletService, times(1)).debit(any());
    }

    @Test
    void testDebit_Exception() {
        doThrow(new RuntimeException("Service error")).when(walletService).debit(any());

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            walletController.debit(Fixtures.debitRequestDto);
        });

        assertNotNull(thrownException);
        assertEquals("Service error", thrownException.getMessage());

        verify(walletService, times(1)).debit(any());
    }

    @Test
    void testCredit() {
        doNothing().when(walletService).credit(any());

        ResponseEntity<String> response = walletController.credit(Fixtures.creditRequestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Credit complete successfully", response.getBody());

        verify(walletService, times(1)).credit(any());
    }

    @Test
    void testCredit_Exception() {
        doThrow(new RuntimeException("Service error")).when(walletService).credit(any());

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            walletController.credit(Fixtures.creditRequestDto);
        });

        assertNotNull(thrownException);
        assertEquals("Service error", thrownException.getMessage());

        verify(walletService, times(1)).credit(any());
    }

    @Test
    void testTransfer() {
        doNothing().when(walletService).transfer(Fixtures.transferRequest);

        ResponseEntity<String> response = walletController.transfer(Fixtures.transferRequestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Transfer complete successfully", response.getBody());

        verify(walletService, times(1)).transfer(any());
    }

    @Test
    void testTransfer_Exception() {
        doThrow(new RuntimeException("Service error")).when(walletService).transfer(Fixtures.transferRequest);

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            walletController.transfer(Fixtures.transferRequestDto);
        });

        assertNotNull(thrownException);
        assertEquals("Service error", thrownException.getMessage());

        verify(walletService, times(1)).transfer(any());
    }
}
