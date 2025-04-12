package com.ifgoiano.caixa2bank.entities.account;

public record NewAccountDTO(String username, String password, String passwordTransaction, String cpf, String email,
                            String phone) {
}
