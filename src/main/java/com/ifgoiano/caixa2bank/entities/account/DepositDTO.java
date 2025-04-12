package com.ifgoiano.caixa2bank.entities.account;

import java.math.BigDecimal;

public record DepositDTO(String login, BigDecimal value) {
}
