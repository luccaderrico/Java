package com.services.wallet.application;

import com.services.wallet.extensions.PostgresEmbeddedExtension;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
@Slf4j
public class BaseIntegrationTest {

    @LocalServerPort
    protected int serverPort = -1;

    protected final String serviceSecret = System.getenv("SERVICE_SHARED_SECRET");

    @BeforeAll
    public static void beforeAll() throws IOException {
        PostgresEmbeddedExtension.init();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        PostgresEmbeddedExtension.clean();
        PostgresEmbeddedExtension.close();
    }

    @BeforeEach
    public void beforeEach() {
        if (serverPort > 0) {
            RestAssured.port = serverPort;
            log.info("rest-assured ready requesting on port {}", serverPort);
        }
    }
}
