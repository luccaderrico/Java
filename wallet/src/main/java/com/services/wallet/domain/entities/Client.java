package com.services.wallet.domain.entities;

import com.services.wallet.domain.entities.enums.PersonType;

public record Client(
        String documentNumber,
        String name,
        PersonType personType
) {}
