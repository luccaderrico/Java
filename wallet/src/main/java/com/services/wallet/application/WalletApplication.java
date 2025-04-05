package com.services.wallet.application;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.validation.annotation.*;

@SpringBootApplication
@ComponentScan("com.services.wallet.*")
@EnableScheduling
@Validated
public class WalletApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalletApplication.class, args);
	}
}
