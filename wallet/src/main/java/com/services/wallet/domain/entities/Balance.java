package com.services.wallet.domain.entities;

import java.math.*;
import java.time.*;

public record Balance(
        BigDecimal amount,
        LocalDateTime date
) {}
