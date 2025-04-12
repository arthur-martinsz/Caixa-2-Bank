package com.ifgoiano.caixa2bank.entities.transaction;

import java.math.BigDecimal;

public record ListTransactionDTO(Long id, String sender, BigDecimal value, String receiver, String time) {
}
