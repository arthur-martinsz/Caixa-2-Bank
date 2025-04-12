package com.ifgoiano.caixa2bank.services.user;

import com.ifgoiano.caixa2bank.entities.account.Account;
import com.ifgoiano.caixa2bank.entities.user.UserDataDTO;
import com.ifgoiano.caixa2bank.repository.AccountRepository;
import com.ifgoiano.caixa2bank.utils.ReturnAccountByLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserDataService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    ReturnAccountByLogin returnAccountByLogin;

    public UserDataDTO data() {
        UserDataDTO userDataDTO;

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String login = principal.getUsername();

        Account account = returnAccountByLogin.findByLogin(login);

        userDataDTO = new UserDataDTO(account.getUser().getUsername(), account.getNumber(), account.getBalance(), account.getUser().getCpf());

        return userDataDTO;
    }

}
