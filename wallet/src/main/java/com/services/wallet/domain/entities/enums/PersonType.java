package com.services.wallet.domain.entities.enums;

public enum PersonType {
    PF,
    PJ,
    NOT_MAPPED;

    public static PersonType toPersonType(String personType) {
        try {
            return PersonType.valueOf(personType);
        } catch (IllegalArgumentException exc) {
            return NOT_MAPPED;
        }
    }

    public static String fromPersonType(PersonType personType) { return personType.name(); }
}
