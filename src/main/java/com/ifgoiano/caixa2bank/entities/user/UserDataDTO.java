package com.ifgoiano.caixa2bank.entities.user;

import java.math.BigDecimal;

public record UserDataDTO(String username, int number, BigDecimal balance, String cpf) {

}
