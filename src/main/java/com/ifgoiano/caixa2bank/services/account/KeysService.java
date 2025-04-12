package com.ifgoiano.caixa2bank.services.account;

import com.ifgoiano.caixa2bank.entities.account.Account;
import com.ifgoiano.caixa2bank.entities.account.KeysDTO;
import com.ifgoiano.caixa2bank.entities.account.KeysExistisDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class KeysService {

    @Autowired
    AccountService accountService;

    public KeysDTO getKeysAccount() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Account account = accountService.findByLogin(principal.getUsername());

        KeysDTO keys = account.getKeys();

        return keys;
    }

    public KeysExistisDTO checkRegisteredKeys() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Account account = accountService.findByLogin(principal.getUsername());

        return account.checkRegisteredKeys();
    }

}
