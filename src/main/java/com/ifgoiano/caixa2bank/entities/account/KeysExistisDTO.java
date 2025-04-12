package com.ifgoiano.caixa2bank.entities.account;

public record KeysExistisDTO(boolean cpf, boolean random, boolean phone,
                             boolean email) {  // True = not registered, false = registered

}
