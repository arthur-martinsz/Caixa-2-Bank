package com.ifgoiano.caixa2bank.services.user;

import com.ifgoiano.caixa2bank.entities.account.Account;
import com.ifgoiano.caixa2bank.entities.user.Authority;
import com.ifgoiano.caixa2bank.entities.user.User;
import com.ifgoiano.caixa2bank.repository.AuthorityRepository;
import com.ifgoiano.caixa2bank.repository.UserRepository;
import com.ifgoiano.caixa2bank.services.account.AccountService;
import com.ifgoiano.caixa2bank.utils.ReturnAccountByLogin;
import com.ifgoiano.caixa2bank.websecurity.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository repository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private ReturnAccountByLogin returnAccountByLogin;

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Account account = accountService.findByLogin(login);
        User user = this.findByLogin(login);

        if (account == null && user == null) throw new UsernameNotFoundException("User not exists");

        if (account == null && user.getAuthorities().stream()
                .noneMatch(authority -> authority.equals("admin"))) {

            return new CustomUserDetails(login, user);
        }
        return new CustomUserDetails(login, account);
    }

    public User findByLogin(String login) {
        return repository.findByLogin(login);
    }

    public void saveUser(User user) {
        List<Authority> authorities = new ArrayList<Authority>();

        Authority authority = authorityRepository.findById(2L).get();

        authorities.add(authority);

        user.setAuthorities(authorities);

        repository.save(user);
    }

    public void saveAdmin(User user) {
        if (returnAccountByLogin.findByAdmin(user.getPhone()) != null) {
            throw new DataIntegrityViolationException("Telefone já cadastrado.");
        } else if (returnAccountByLogin.findByAdmin(user.getCpf()) != null) {
            throw new DataIntegrityViolationException("CPF já cadastrado.");
        } else if (returnAccountByLogin.findByAdmin(user.getEmail()) != null) {
            throw new DataIntegrityViolationException("Email já cadastrado.");
        }

        List<Authority> authorities = new ArrayList<Authority>();

        Authority authority = authorityRepository.findById(1L).get();

        authorities.add(authority);

        user.setAuthorities(authorities);

        user.setAdminPassword(passwordEncoder().encode(user.getAdminPassword()));

        repository.save(user);
    }
}
