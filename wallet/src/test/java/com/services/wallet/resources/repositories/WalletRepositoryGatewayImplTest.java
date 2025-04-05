package com.services.wallet.resources.repositories;

import com.services.wallet.domain.entities.Wallet;
import com.services.wallet.domain.exceptions.BusinessErrorType;
import com.services.wallet.fixtures.Fixtures;
import com.services.wallet.resources.exceptions.ResourceJpaException;
import com.services.wallet.resources.repositories.WalletRepositoryGatewayImpl;
import com.services.wallet.resources.repositories.WalletRepositoryGatewayJpa;
import com.services.wallet.resources.repositories.entities.WalletJpa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ExtendWith(MockitoExtension.class)
class WalletRepositoryGatewayImplTest {

    @Mock
    private WalletRepositoryGatewayJpa walletRepositoryGatewayJpa;

    @InjectMocks
    private WalletRepositoryGatewayImpl walletRepositoryGatewayImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveWallet_NewWallet() {
        when(walletRepositoryGatewayJpa.findByClientDocumentNumber(Fixtures.wallet.getClient().documentNumber())).thenReturn(null);
        when(walletRepositoryGatewayJpa.save(any(WalletJpa.class))).thenReturn(Fixtures.walletJpa);

        Wallet savedWallet = walletRepositoryGatewayImpl.saveWallet(Fixtures.wallet);

        assertNotNull(savedWallet);
        verify(walletRepositoryGatewayJpa, times(1)).save(any(WalletJpa.class));
    }

    @Test
    void testSaveWallet_ExistingWallet() {
        when(walletRepositoryGatewayJpa.findByClientDocumentNumber(Fixtures.wallet.getClient().documentNumber())).thenReturn(Fixtures.walletJpa);
        when(walletRepositoryGatewayJpa.save(Fixtures.walletJpa)).thenReturn(Fixtures.walletJpa);

        Wallet savedWallet = walletRepositoryGatewayImpl.saveWallet(Fixtures.wallet);

        assertNotNull(savedWallet);
        verify(walletRepositoryGatewayJpa, times(1)).save(Fixtures.walletJpa);
    }

    @Test
    void testSaveWallet_Exception() {
        when(walletRepositoryGatewayJpa.findByClientDocumentNumber(Fixtures.wallet.getClient().documentNumber())).thenThrow(new RuntimeException("DB error"));

        ResourceJpaException thrownException = assertThrows(ResourceJpaException.class, () -> {
            walletRepositoryGatewayImpl.saveWallet(Fixtures.wallet);
        });

        assertNotNull(thrownException);
        assertEquals("DB error", thrownException.getMessage());
    }

    @Test
    void testFindWalletByClientDocumentNumber_Success() {
        when(walletRepositoryGatewayJpa.findByClientDocumentNumber("12345")).thenReturn(Fixtures.walletJpa);

        Wallet foundWallet = walletRepositoryGatewayImpl.findWalletByClientDocumentNumber("12345");

        assertNotNull(foundWallet);
        verify(walletRepositoryGatewayJpa, times(1)).findByClientDocumentNumber("12345");
    }

    @Test
    void testFindWalletByClientDocumentNumber_NotFound() {
        when(walletRepositoryGatewayJpa.findByClientDocumentNumber("12345")).thenReturn(null);

        ResourceJpaException thrownException = assertThrows(ResourceJpaException.class, () -> {
            walletRepositoryGatewayImpl.findWalletByClientDocumentNumber("12345");
        });

        assertNotNull(thrownException);
        assertEquals("Invalid Client", thrownException.getMessage());
        assertEquals(INTERNAL_SERVER_ERROR, thrownException.getHttpStatus());
        assertEquals(BusinessErrorType.NOT_FOUND, thrownException.getErrorType());
    }

    @Test
    void testFindWalletByClientDocumentNumber_Exception() {
        when(walletRepositoryGatewayJpa.findByClientDocumentNumber("12345")).thenThrow(new RuntimeException("DB error"));

        ResourceJpaException thrownException = assertThrows(ResourceJpaException.class, () -> {
            walletRepositoryGatewayImpl.findWalletByClientDocumentNumber("12345");
        });

        assertNotNull(thrownException);
        assertEquals("DB error", thrownException.getMessage());
    }
}
