package com.ifgoiano.caixa2bank.utils;

import com.ifgoiano.caixa2bank.entities.account.Account;
import com.ifgoiano.caixa2bank.entities.user.User;
import com.ifgoiano.caixa2bank.repository.AccountRepository;
import com.ifgoiano.caixa2bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReturnAccountByLogin {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    public Account findByLogin(String login) {
        Account account = null;

        if (login.length() == 11) {
            account = accountRepository.findByCpf(login);
        } else if (login.contains("@")) {
            account = accountRepository.findByEmail(login);
        } else {
            if (!login.contains("(")) account = accountRepository.findByNumber(Integer.parseInt(login));
        }

        return account;
    }

    public User findByAdmin(String login) {
        return userRepository.findByAdmin(login);
    }
}
