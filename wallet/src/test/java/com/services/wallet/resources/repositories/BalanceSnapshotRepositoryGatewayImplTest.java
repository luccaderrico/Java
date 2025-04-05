package com.services.wallet.resources.repositories;

import com.services.wallet.domain.entities.BalanceSnapshot;
import com.services.wallet.resources.exceptions.ResourceJpaException;
import com.services.wallet.resources.repositories.BalanceSnapshotRepositoryGatewayImpl;
import com.services.wallet.resources.repositories.BalanceSnapshotRepositoryGatewayJpa;
import com.services.wallet.resources.repositories.entities.BalanceSnapshotJpa;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.util.*;

import static com.services.wallet.fixtures.Fixtures.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceSnapshotRepositoryGatewayImplTest {

    @Mock
    private BalanceSnapshotRepositoryGatewayJpa balanceSnapshotRepositoryGatewayJpa;

    @InjectMocks
    private BalanceSnapshotRepositoryGatewayImpl balanceSnapshotRepositoryGatewayImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindLastSnapshot_Success() {
        when(balanceSnapshotRepositoryGatewayJpa.findTopByClientDocumentNumberOrderByCreatedAtDesc("12345"))
                .thenReturn(Optional.of(balanceSnapshotJpa));

        Optional<BalanceSnapshot> result = balanceSnapshotRepositoryGatewayImpl.findLastSnapshot("12345");

        assertTrue(result.isPresent());
        verify(balanceSnapshotRepositoryGatewayJpa, times(1)).findTopByClientDocumentNumberOrderByCreatedAtDesc("12345");
    }

    @Test
    void testFindLastSnapshot_NotFound() {
        when(balanceSnapshotRepositoryGatewayJpa.findTopByClientDocumentNumberOrderByCreatedAtDesc("12345"))
                .thenReturn(Optional.empty());

        Optional<BalanceSnapshot> result = balanceSnapshotRepositoryGatewayImpl.findLastSnapshot("12345");

        assertFalse(result.isPresent());
        verify(balanceSnapshotRepositoryGatewayJpa, times(1)).findTopByClientDocumentNumberOrderByCreatedAtDesc("12345");
    }

    @Test
    void testFindLastSnapshot_Exception() {
        when(balanceSnapshotRepositoryGatewayJpa.findTopByClientDocumentNumberOrderByCreatedAtDesc("12345"))
                .thenThrow(new RuntimeException("DB error"));

        ResourceJpaException thrownException = assertThrows(ResourceJpaException.class, () -> {
            balanceSnapshotRepositoryGatewayImpl.findLastSnapshot("12345");
        });

        assertNotNull(thrownException);
        assertEquals("DB error", thrownException.getMessage());
    }

    @Test
    void testSave_Success() {
        when(balanceSnapshotRepositoryGatewayJpa.save(any(BalanceSnapshotJpa.class))).thenReturn(balanceSnapshotJpa);

        assertDoesNotThrow(() -> balanceSnapshotRepositoryGatewayImpl.save(balanceSnapshot));

        verify(balanceSnapshotRepositoryGatewayJpa, times(1)).save(any(BalanceSnapshotJpa.class));
    }

    @Test
    void testSave_Exception() {
        doThrow(new RuntimeException("DB error")).when(balanceSnapshotRepositoryGatewayJpa).save(any(BalanceSnapshotJpa.class));

        ResourceJpaException thrownException = assertThrows(ResourceJpaException.class, () -> {
            balanceSnapshotRepositoryGatewayImpl.save(balanceSnapshot);
        });

        assertNotNull(thrownException);
        assertEquals("DB error", thrownException.getMessage());
    }
}
