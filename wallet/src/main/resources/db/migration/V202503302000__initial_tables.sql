CREATE TABLE IF NOT EXISTS "client"(
    "document_number"   VARCHAR(14)     NOT NULL,
    "name"              VARCHAR(30)     NOT NULL,
    "person_type"       VARCHAR(30)     NOT NULL,
    "status"            VARCHAR(8)      NOT NULL,
    "created_at"        TIMESTAMP       NOT NULL,
    "updated_at"        TIMESTAMP       NOT NULL,
    CONSTRAINT "pk_client" PRIMARY KEY ("document_number")
);

CREATE TABLE IF NOT EXISTS "wallet"(
    "wallet_id"                 SERIAL          NOT NULL,
    "client_document_number"    VARCHAR(14)     NOT NULL UNIQUE,
    "currency"                  VARCHAR(8)      NOT NULL,
    "balance"                   REAL            NOT NULL,
    "status"                    VARCHAR(8)      NOT NULL,
    "created_at"                TIMESTAMP       NOT NULL,
    "updated_at"                TIMESTAMP       NOT NULL,
    CONSTRAINT "pk_wallet" PRIMARY KEY ("wallet_id"),
    CONSTRAINT "fk_wallet_client" FOREIGN KEY ("client_document_number") REFERENCES client("document_number")
);

CREATE INDEX idx_wallet_client_document_number ON wallet(client_document_number);

CREATE TABLE IF NOT EXISTS "transaction"(
    "transaction_id"            SERIAL          NOT NULL,
    "wallet_id"                 INTEGER         NOT NULL,
    "amount"                    REAL            NOT NULL,
    "type"                      VARCHAR(15)     NOT NULL,
    "created_at"                TIMESTAMP       NOT NULL,
    CONSTRAINT "pk_transaction" PRIMARY KEY ("transaction_id"),
    CONSTRAINT "fk_transaction_wallet" FOREIGN KEY ("wallet_id") REFERENCES wallet("wallet_id")
);

CREATE INDEX idx_transaction_wallet_client_created_at ON "transaction"(wallet_id, created_at);

CREATE TABLE IF NOT EXISTS "balance_snapshot"(
    "snapshot_id"               SERIAL          NOT NULL,
    "client_document_number"    VARCHAR(14)     NOT NULL,
    "balance"                   REAL            NOT NULL,
    "created_at"                TIMESTAMP       NOT NULL,
    CONSTRAINT "pk_balance_snapshot" PRIMARY KEY ("snapshot_id")
);

CREATE INDEX idx_snapshot_client_document_number_created_at ON balance_snapshot(client_document_number, created_at);
