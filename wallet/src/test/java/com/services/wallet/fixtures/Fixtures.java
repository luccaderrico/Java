package com.services.wallet.fixtures;

import com.services.wallet.application.web.dtos.ClientDto;
import com.services.wallet.application.web.dtos.CreditRequestDto;
import com.services.wallet.application.web.dtos.DebitRequestDto;
import com.services.wallet.application.web.dtos.TransferRequestDto;
import com.services.wallet.application.web.dtos.WalletDto;
import com.services.wallet.domain.entities.Balance;
import com.services.wallet.domain.entities.BalanceSnapshot;
import com.services.wallet.domain.entities.Client;
import com.services.wallet.domain.entities.CreditRequest;
import com.services.wallet.domain.entities.DebitRequest;
import com.services.wallet.domain.entities.Transaction;
import com.services.wallet.domain.entities.TransferRequest;
import com.services.wallet.domain.entities.Wallet;
import com.services.wallet.domain.exceptions.business.InsufficientFundsException;
import com.services.wallet.domain.exceptions.business.InvalidAmountException;
import com.services.wallet.resources.exceptions.ResourceJpaException;
import com.services.wallet.resources.repositories.entities.BalanceSnapshotJpa;
import com.services.wallet.resources.repositories.entities.ClientJpa;
import com.services.wallet.resources.repositories.entities.TransactionJpa;
import com.services.wallet.resources.repositories.entities.WalletJpa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.services.wallet.domain.entities.enums.Currency.BRL;
import static com.services.wallet.domain.entities.enums.OperationType.CREDIT;
import static com.services.wallet.domain.entities.enums.OperationType.DEBIT;
import static com.services.wallet.domain.entities.enums.PersonType.PF;
import static com.services.wallet.domain.entities.enums.PersonType.PJ;
import static com.services.wallet.domain.exceptions.BusinessErrorType.INSUFFICIENT_BALANCE;
import static com.services.wallet.domain.exceptions.BusinessErrorType.NOT_FOUND;
import static java.math.BigDecimal.ZERO;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class Fixtures {

    // Utils
    public static LocalDateTime now = LocalDateTime.now();
    public static LocalDateTime marchThirtieth = LocalDateTime.of(2025, 3, 30, 0, 0, 0);

    public static final BigDecimal FIFTY = BigDecimal.valueOf(50.0);
    public static final BigDecimal SEVENTY_FIVE = BigDecimal.valueOf(75.0);
    public static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100.0);
    public static final BigDecimal ONE_HUNDRED_TWENTY_FIVE = BigDecimal.valueOf(125.0);
    public static final BigDecimal ONE_HUNDRED_AND_FIFTH = BigDecimal.valueOf(150.0);

    // Client Fixtures
    public static final String CLIENT_DOCUMENT_NUMBER = "111111111111";
    public static final String CLIENT_DOCUMENT_NUMBER_PJ = "22222222222222";
    public static final String CLIENT_NAME = "john";
    public static final String CLIENT_PJ_NAME = "company";
    public static ClientDto clientRequestDto = new ClientDto(CLIENT_DOCUMENT_NUMBER, CLIENT_NAME, PF.name());
    public static Client client = new Client(CLIENT_DOCUMENT_NUMBER, CLIENT_NAME, PF);

    public static Client clientPj = new Client(CLIENT_DOCUMENT_NUMBER_PJ, CLIENT_PJ_NAME, PJ);
    public static ClientJpa clientJpa = new ClientJpa(CLIENT_DOCUMENT_NUMBER, CLIENT_NAME, PF.name());

    // Wallet Fixtures
    public static WalletDto requestDto = new WalletDto(clientRequestDto, null, null);
    public static Wallet wallet = new Wallet(client);

    public static Wallet walletPj = new Wallet(clientPj);

    public static Wallet walletFull = new Wallet(client, BRL, ONE_HUNDRED);

    public static WalletJpa walletJpa = new WalletJpa(clientJpa, BRL.name(), ZERO);

    // Debit Fixtures
    public static DebitRequestDto debitRequestDto = new DebitRequestDto(CLIENT_DOCUMENT_NUMBER, FIFTY);

    public static DebitRequest debitRequest = new DebitRequest(CLIENT_DOCUMENT_NUMBER, FIFTY);
    public static DebitRequest invalidDebitRequest = new DebitRequest(CLIENT_DOCUMENT_NUMBER, ZERO);

    // Credit Fixtures
    public static CreditRequestDto creditRequestDto = new CreditRequestDto(CLIENT_DOCUMENT_NUMBER, SEVENTY_FIVE);

    public static CreditRequest creditRequest = new CreditRequest(CLIENT_DOCUMENT_NUMBER, SEVENTY_FIVE);

    public static CreditRequest invalidCreditRequest = new CreditRequest(CLIENT_DOCUMENT_NUMBER, ZERO);

    // Balance Fixtures
    public static Balance balanceNow = new Balance(ONE_HUNDRED, now);
    public static Balance balanceMarchThirtieth = new Balance(ONE_HUNDRED_AND_FIFTH, marchThirtieth);

    // Balance Snapshot Fixtures
    public static BalanceSnapshot balanceSnapshot = new BalanceSnapshot(
        CLIENT_DOCUMENT_NUMBER, balanceNow.amount(), balanceNow.date()
    );

    public static BalanceSnapshot balanceSnapshotPj = new BalanceSnapshot(
        CLIENT_DOCUMENT_NUMBER_PJ, balanceNow.amount(), balanceNow.date()
    );

    public static BalanceSnapshotJpa balanceSnapshotJpa = new BalanceSnapshotJpa(
        CLIENT_DOCUMENT_NUMBER, balanceNow.amount()
    );

    // Transaction Fixtures
    public static Transaction credit = new Transaction(wallet, SEVENTY_FIVE, CREDIT);

    public static TransactionJpa creditJpa = new TransactionJpa(walletJpa, FIFTY, CREDIT.name());

    public static Transaction debit = new Transaction(wallet, FIFTY, DEBIT);

    // Transfer Fixtures

    public static TransferRequest transferRequest = new TransferRequest(
            walletFull.getClient().documentNumber(),
            walletPj.getClient().documentNumber(),
            FIFTY
    );

    public static TransferRequest invalidTransferRequest = new TransferRequest(
            walletFull.getClient().documentNumber(),
            walletPj.getClient().documentNumber(),
            ZERO
    );

    public static TransferRequestDto transferRequestDto = new TransferRequestDto(
            walletFull.getClient().documentNumber(),
            walletPj.getClient().documentNumber(),
            FIFTY
    );
    public static DebitRequest senderDebitRequest = new DebitRequest(walletFull.getClient().documentNumber(), FIFTY);

    public static CreditRequest recipientCreditRequest = new CreditRequest(walletPj.getClient().documentNumber(), FIFTY);

    // Exception Fixtures
    public static String INVALID_CLIENT_EXCEPTION = "Invalid Client";
    public static ResourceJpaException internalErrorResourceJpaException = new ResourceJpaException(
            INVALID_CLIENT_EXCEPTION,
            INTERNAL_SERVER_ERROR,
            NOT_FOUND);

    public static InsufficientFundsException insufficientFundsException = new InsufficientFundsException(
            INVALID_CLIENT_EXCEPTION,
            INSUFFICIENT_BALANCE);

    public static InvalidAmountException invalidAmountException = new InvalidAmountException(
            INVALID_CLIENT_EXCEPTION,
            INSUFFICIENT_BALANCE);
}
