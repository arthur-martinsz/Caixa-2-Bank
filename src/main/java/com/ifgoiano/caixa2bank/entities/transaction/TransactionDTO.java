package com.ifgoiano.caixa2bank.entities.transaction;

import java.math.BigDecimal;

public record TransactionDTO(BigDecimal value, String receiver, String passwordTransaction) {
}
