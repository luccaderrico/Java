package com.services.wallet.resources.repositories;

import com.services.wallet.fixtures.Fixtures;
import com.services.wallet.resources.exceptions.ResourceJpaException;
import com.services.wallet.resources.repositories.ClientRepositoryGatewayImpl;
import com.services.wallet.resources.repositories.ClientRepositoryGatewayJpa;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientRepositoryGatewayImplTest {

    @Mock
    ClientRepositoryGatewayJpa clientRepositoryGatewayJpa;

    @InjectMocks
    ClientRepositoryGatewayImpl clientRepositoryGatewayImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllClientDocumentNumbers_Success() {
        List<String> clientsDocuments = List.of(Fixtures.CLIENT_DOCUMENT_NUMBER);
        when(clientRepositoryGatewayJpa.findAllClientDocumentNumber()).thenReturn(clientsDocuments);

        List<String> result = clientRepositoryGatewayImpl.findAllClientDocumentNumbers();

        assertFalse(result.isEmpty());
        assertTrue(result.contains(Fixtures.CLIENT_DOCUMENT_NUMBER));

        verify(clientRepositoryGatewayJpa, times(1)).findAllClientDocumentNumber();
    }

    @Test
    void testFindAllClientDocumentNumbers_Exception() {
        when(clientRepositoryGatewayJpa.findAllClientDocumentNumber()).thenThrow(Fixtures.internalErrorResourceJpaException);

        ResourceJpaException thrownException = assertThrows(
            ResourceJpaException.class, () -> clientRepositoryGatewayImpl.findAllClientDocumentNumbers()
        );

        assertNotNull(thrownException);

        verify(clientRepositoryGatewayJpa, times(1)).findAllClientDocumentNumber();
    }
}
