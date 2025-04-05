package com.services.wallet.extensions;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.flywaydb.core.Flyway;

import java.io.IOException;

public class PostgresEmbeddedExtension {

    private static final int dbPort = 15432;

    private static final String databaseConnectionString = System.getenv("DATABASE_CONNECTION_STRING");
    private static final String databaseUsername = System.getenv("DATABASE_USERNAME");
    private static final String databasePassword = System.getenv("DATABASE_PASSWORD");

    private static EmbeddedPostgres embeddedPostgres;

    public static void init() throws IOException {
        embeddedPostgres = EmbeddedPostgres.builder()
                .setPort(dbPort)
                .start();

        createRole();
    }

    private static void createRole() {
        try (var connection = embeddedPostgres.getPostgresDatabase().getConnection()) {
            connection.createStatement().execute("CREATE ROLE WALLET_DATA;");
        } catch (Exception exc) {
            exc.printStackTrace();
            throw new RuntimeException("Failed to create role or database.", exc);
        }
    }

    private static void dropRole() {
        try (var connection = embeddedPostgres.getPostgresDatabase().getConnection()) {
            connection.createStatement().execute("DROP ROLE WALLET_DATA;");
        } catch (Exception exc) {
            exc.printStackTrace();
            throw new RuntimeException("Failed to drop role.", exc);
        }
    }

    public static void close() throws IOException {
        dropRole();
        embeddedPostgres.close();
    }

    private static Flyway configFlyway() {
        return Flyway.configure()
                .dataSource(databaseConnectionString, databaseUsername, databasePassword)
                .cleanDisabled(false)
                .load();
    }

    public static void clean() {
        configFlyway().clean();
    }
}