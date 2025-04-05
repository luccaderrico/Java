package com.services.wallet.domain.services;

import com.services.wallet.domain.entities.BalanceSnapshot;
import com.services.wallet.domain.gateways.BalanceSnapshotRepositoryGateway;
import com.services.wallet.domain.gateways.BalanceSnapshotService;
import com.services.wallet.domain.gateways.ClientRepositoryGateway;
import com.services.wallet.domain.gateways.TransactionService;
import com.services.wallet.fixtures.Fixtures;
import com.services.wallet.resources.exceptions.ResourceJpaException;
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
class BalanceSnapshotServiceTest {

    @Mock
    private BalanceSnapshotRepositoryGateway balanceSnapshotRepositoryGateway;

    @Mock
    private ClientRepositoryGateway clientRepositoryGateway;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private BalanceSnapshotService balanceSnapshotService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Fixtures.balanceSnapshot = new BalanceSnapshot(
                Fixtures.CLIENT_DOCUMENT_NUMBER, Fixtures.balanceNow.amount(), Fixtures.balanceNow.date()
        );
    }

    @AfterEach
    void afterEach(){
        Mockito.reset(balanceSnapshotRepositoryGateway, clientRepositoryGateway, transactionService);
    }

    @Test
    void testGetBalanceSnapshotForDate_Success() {
        when(balanceSnapshotRepositoryGateway.findLastSnapshot(Fixtures.balanceSnapshot.clientDocumentNumber())).thenReturn(Optional.of(Fixtures.balanceSnapshot));
        when(transactionService.findTransactionsBetween(
                Fixtures.balanceSnapshot.clientDocumentNumber(), Fixtures.balanceSnapshot.snapshotDate(), Fixtures.balanceSnapshot.snapshotDate())
        ).thenReturn(List.of(Fixtures.credit, Fixtures.debit));

        BalanceSnapshot result = balanceSnapshotService.getBalanceSnapshotForDate(
                Fixtures.balanceSnapshot.clientDocumentNumber(),
                Fixtures.balanceSnapshot.snapshotDate()
        );

        assertNotNull(result);
        Assertions.assertEquals(Fixtures.ONE_HUNDRED_TWENTY_FIVE, result.balance());
        verify(balanceSnapshotRepositoryGateway, times(1)).findLastSnapshot(Fixtures.balanceSnapshot.clientDocumentNumber());
        verify(transactionService, times(1)).
                findTransactionsBetween(Fixtures.balanceSnapshot.clientDocumentNumber(), Fixtures.balanceSnapshot.snapshotDate(), Fixtures.balanceSnapshot.snapshotDate());
    }

    @Test
    void testGetBalanceSnapshotForDate_ClientSearchException() {
        when(balanceSnapshotRepositoryGateway.findLastSnapshot(Fixtures.balanceSnapshot.clientDocumentNumber()))
                .thenThrow(Fixtures.internalErrorResourceJpaException);

        ResourceJpaException thrownException = assertThrows(ResourceJpaException.class, () ->
                balanceSnapshotService.getBalanceSnapshotForDate(
                        Fixtures.balanceSnapshot.clientDocumentNumber(), Fixtures.balanceSnapshot.snapshotDate())
        );

        assertNotNull(thrownException);
        Assertions.assertEquals(Fixtures.internalErrorResourceJpaException.getMessage(), thrownException.getMessage());
        verify(balanceSnapshotRepositoryGateway, times(1)).findLastSnapshot(Fixtures.balanceSnapshot.clientDocumentNumber());
        verify(transactionService, times(0))
                .findTransactionsBetween(any(String.class), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testGetBalanceSnapshotForDate_TransactionSearchException() {
        when(balanceSnapshotRepositoryGateway.findLastSnapshot(Fixtures.balanceSnapshot.clientDocumentNumber())).thenReturn(Optional.of(Fixtures.balanceSnapshot));
        when(transactionService.findTransactionsBetween(
                Fixtures.balanceSnapshot.clientDocumentNumber(), Fixtures.balanceSnapshot.snapshotDate(), Fixtures.balanceSnapshot.snapshotDate())
        ).thenThrow(Fixtures.internalErrorResourceJpaException);

        ResourceJpaException thrownException = assertThrows(ResourceJpaException.class, () ->
                balanceSnapshotService.getBalanceSnapshotForDate(
                    Fixtures.balanceSnapshot.clientDocumentNumber(), Fixtures.balanceSnapshot.snapshotDate())
        );

        assertNotNull(thrownException);
        Assertions.assertEquals(Fixtures.internalErrorResourceJpaException.getMessage(), thrownException.getMessage());
        verify(balanceSnapshotRepositoryGateway, times(1)).findLastSnapshot(Fixtures.balanceSnapshot.clientDocumentNumber());
        verify(transactionService, times(1))
                .findTransactionsBetween(Fixtures.balanceSnapshot.clientDocumentNumber(), Fixtures.balanceSnapshot.snapshotDate(), Fixtures.balanceSnapshot.snapshotDate());
    }

    @Test
    void testFindLastSnapshot_Found() {
        when(balanceSnapshotRepositoryGateway.findLastSnapshot(Fixtures.balanceSnapshot.clientDocumentNumber())).thenReturn(Optional.of(Fixtures.balanceSnapshot));

        BalanceSnapshot result = balanceSnapshotService.findLastSnapshot(Fixtures.balanceSnapshot.clientDocumentNumber());

        Assertions.assertEquals(Fixtures.balanceSnapshot, result);
        verify(balanceSnapshotRepositoryGateway, times(1)).findLastSnapshot(Fixtures.balanceSnapshot.clientDocumentNumber());
    }

    @Test
    void testFindLastSnapshot_NotFound() {
        when(balanceSnapshotRepositoryGateway.findLastSnapshot(Fixtures.balanceSnapshot.clientDocumentNumber())).thenReturn(Optional.empty());

        BalanceSnapshot result = balanceSnapshotService.findLastSnapshot(Fixtures.balanceSnapshot.clientDocumentNumber());

        Assertions.assertEquals(Fixtures.balanceSnapshot.clientDocumentNumber(), result.clientDocumentNumber());
        assertEquals(BigDecimal.ZERO, result.balance());
        assertNotNull(result.snapshotDate());
    }

    @Test
    void testGenerateBalanceSnapshot_Success() {
        List<String> clientDocuments = List.of(
            Fixtures.balanceSnapshot.clientDocumentNumber()
        );

        when(clientRepositoryGateway.findAllClientDocumentNumbers()).thenReturn(clientDocuments);
        when(balanceSnapshotRepositoryGateway.findLastSnapshot(
                Fixtures.balanceSnapshot.clientDocumentNumber())
        ).thenReturn(Optional.of(Fixtures.balanceSnapshot));
        when(transactionService.findTransactionsBetween(
                ArgumentMatchers.eq(Fixtures.balanceSnapshot.clientDocumentNumber()), ArgumentMatchers.eq(Fixtures.balanceSnapshot.snapshotDate()), any(LocalDateTime.class))
        ).thenReturn(Collections.emptyList());
        doNothing().when(balanceSnapshotRepositoryGateway).save(Fixtures.balanceSnapshot);

        assertDoesNotThrow(() -> balanceSnapshotService.generateBalanceSnapshot());

        verify(clientRepositoryGateway, times(1)).findAllClientDocumentNumbers();
        verify(balanceSnapshotRepositoryGateway, times(1)).findLastSnapshot(Fixtures.balanceSnapshot.clientDocumentNumber());
        verify(transactionService, times(1)).findTransactionsBetween(
                ArgumentMatchers.eq(Fixtures.balanceSnapshot.clientDocumentNumber()), ArgumentMatchers.eq(Fixtures.balanceSnapshot.snapshotDate()), any(LocalDateTime.class));
        verify(balanceSnapshotRepositoryGateway, times(1)).save(any(BalanceSnapshot.class));
    }

    @Test
    void testGenerateBalanceSnapshot_SuccessMultipleClients() {
        List<String> clientDocuments = List.of(
                Fixtures.balanceSnapshot.clientDocumentNumber(),
                Fixtures.balanceSnapshotPj.clientDocumentNumber()
        );

        when(clientRepositoryGateway.findAllClientDocumentNumbers()).thenReturn(clientDocuments);

        when(balanceSnapshotRepositoryGateway.findLastSnapshot(Fixtures.balanceSnapshot.clientDocumentNumber()))
                .thenReturn((Optional.of(Fixtures.balanceSnapshot)));

        when(balanceSnapshotRepositoryGateway.findLastSnapshot(Fixtures.balanceSnapshotPj.clientDocumentNumber()))
                .thenReturn(Optional.of(Fixtures.balanceSnapshotPj));

        when(transactionService.findTransactionsBetween(
            argThat(doc ->
                doc.equals(Fixtures.balanceSnapshot.clientDocumentNumber()) || doc.equals(Fixtures.balanceSnapshotPj.clientDocumentNumber())
            ),
            ArgumentMatchers.eq(Fixtures.balanceSnapshot.snapshotDate()),
            any(LocalDateTime.class)
        )).thenReturn(Collections.emptyList());

        doNothing().when(balanceSnapshotRepositoryGateway).save(any(BalanceSnapshot.class));

        assertDoesNotThrow(() -> balanceSnapshotService.generateBalanceSnapshot());

        verify(clientRepositoryGateway, times(1)).findAllClientDocumentNumbers();

        verify(balanceSnapshotRepositoryGateway, times(1))
                .findLastSnapshot(Fixtures.balanceSnapshot.clientDocumentNumber());

        verify(balanceSnapshotRepositoryGateway, times(1))
                .findLastSnapshot(Fixtures.balanceSnapshotPj.clientDocumentNumber());

        verify(transactionService, times(2)).findTransactionsBetween(
                anyString(), any(LocalDateTime.class), any(LocalDateTime.class));

        verify(balanceSnapshotRepositoryGateway, times(2)).save(any(BalanceSnapshot.class));
    }

    @Test
    void testGenerateBalanceSnapshot_NoClientFailure() {
        when(clientRepositoryGateway.findAllClientDocumentNumbers()).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> balanceSnapshotService.generateBalanceSnapshot());
        verify(balanceSnapshotRepositoryGateway, never()).save(any(BalanceSnapshot.class));
    }

    @Test
    void testGenerateBalanceSnapshot_FindAllClientsFailure() {
        when(clientRepositoryGateway.findAllClientDocumentNumbers()).thenThrow(Fixtures.internalErrorResourceJpaException);

        assertDoesNotThrow(() -> balanceSnapshotService.generateBalanceSnapshot());
        verify(balanceSnapshotRepositoryGateway, never()).save(any(BalanceSnapshot.class));
    }

    @Test
    void testGenerateBalanceSnapshot_FindLastSnapshotFailure() {
        List<String> clientDocuments = List.of(Fixtures.balanceSnapshot.clientDocumentNumber());

        when(clientRepositoryGateway.findAllClientDocumentNumbers()).thenReturn(clientDocuments);
        when(balanceSnapshotRepositoryGateway.findLastSnapshot(Fixtures.balanceSnapshot.clientDocumentNumber()))
                .thenThrow(Fixtures.internalErrorResourceJpaException);

        assertDoesNotThrow(() -> balanceSnapshotService.generateBalanceSnapshot());

        verify(balanceSnapshotRepositoryGateway, times(1)).findLastSnapshot(Fixtures.balanceSnapshot.clientDocumentNumber());
        verify(balanceSnapshotRepositoryGateway, never()).save(any(BalanceSnapshot.class));
        verify(transactionService, never()).findTransactionsBetween(any(String.class), any( LocalDateTime.class), any( LocalDateTime.class));
    }

    @Test
    void testGenerateBalanceSnapshot_SaveSnapshotFailure() {
        List<String> clientDocuments = List.of(
                Fixtures.balanceSnapshot.clientDocumentNumber()
        );

        when(clientRepositoryGateway.findAllClientDocumentNumbers()).thenReturn(clientDocuments);
        when(balanceSnapshotRepositoryGateway.findLastSnapshot(Fixtures.balanceSnapshot.clientDocumentNumber()))
                .thenReturn(Optional.of(Fixtures.balanceSnapshot));
        when(transactionService.findTransactionsBetween(
                ArgumentMatchers.eq(Fixtures.balanceSnapshot.clientDocumentNumber()), ArgumentMatchers.eq(Fixtures.balanceSnapshot.snapshotDate()), any(LocalDateTime.class))
        ).thenReturn(Collections.emptyList());
        Mockito.doThrow(Fixtures.internalErrorResourceJpaException).when(balanceSnapshotRepositoryGateway).save(Fixtures.balanceSnapshot);

        assertDoesNotThrow(() -> balanceSnapshotService.generateBalanceSnapshot());

        verify(clientRepositoryGateway, times(1)).findAllClientDocumentNumbers();
        verify(balanceSnapshotRepositoryGateway, times(1)).findLastSnapshot(Fixtures.balanceSnapshot.clientDocumentNumber());
        verify(transactionService, times(1)).findTransactionsBetween(
                ArgumentMatchers.eq(Fixtures.balanceSnapshot.clientDocumentNumber()), ArgumentMatchers.eq(Fixtures.balanceSnapshot.snapshotDate()), any(LocalDateTime.class));
        verify(balanceSnapshotRepositoryGateway, times(1)).save(any(BalanceSnapshot.class));
    }
}
