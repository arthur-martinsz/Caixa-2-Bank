package com.ifgoiano.caixa2bank.services.account;

import com.ifgoiano.caixa2bank.entities.account.Account;
import com.ifgoiano.caixa2bank.entities.account.DepositDTO;
import com.ifgoiano.caixa2bank.repository.AccountRepository;
import com.ifgoiano.caixa2bank.services.email.EmailService;
import com.ifgoiano.caixa2bank.utils.ReturnAccountByLogin;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository repository;

    @Autowired
    private ReturnAccountByLogin returnAccountByLogin;

    @Autowired
    private EmailService emailService;

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Transactional
    public void updateAccount(Account account) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Account accountDB = findByLogin(principal.getUsername());

        if (account != accountDB) {
            accountDB.getUser().setUsername(account.getUser().getUsername());
            accountDB.getUser().setEmail(account.getUser().getEmail());
            accountDB.getUser().setPhone(account.getUser().getPhone());

            if (account.getPixCpf() != null) accountDB.setPixCpf(account.getPixCpf());

            if (account.getPixEmail() != null) accountDB.setPixEmail(account.getPixEmail());

            if (account.getPixPhone() != null) accountDB.setPixPhone(account.getPixPhone());

            repository.saveAndFlush(accountDB);

            return;
        }


        repository.save(account);
    }

    public void saveAccount(Account account) {
        String password = passwordEncoder().encode(account.getPassword());
        String passwordTransaction = passwordEncoder().encode(account.getPasswordTransaction());

        Account nAccount = new Account(password, passwordTransaction, account.getUser());

        if (repository.findByCpf(account.getUser().getCpf()) != null) {
            throw new DataIntegrityViolationException("CPF já cadastrado.");
        } else if (repository.findByEmail(account.getUser().getEmail()) != null) {
            throw new DataIntegrityViolationException("Email já cadastrado.");
        } else if (repository.findByPhone(account.getUser().getPhone()) != null) {
            throw new DataIntegrityViolationException("Número de telefone já cadastrado.");
        }

        repository.save(nAccount);

        emailService.sendEmailCreatedAccount(nAccount.getUser().getCpf());
    }

    public Account findByLogin(String login) {
        Account account;

        account = returnAccountByLogin.findByLogin(login);

        return account;
    }

    public Account findByNumberAccount(int number) {
        return repository.findByNumberAccount(number);
    }

    public void saveKey(String key) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Account account = findByLogin(principal.getUsername());

        switch (key) {
            case "cpf" -> {
                if (repository.findByPix(account.getUser().getCpf()) == null) {
                    account.setPixCpf(account.getUser().getCpf());
                } else throw new DataIntegrityViolationException("Chave de CPF já cadastrada.");
            }
            case "random" -> {
                UUID uuid;

                if (account.getPixRandomKey() != null)
                    throw new DataIntegrityViolationException("Chave aleatória já cadastrada.");

                do {
                    uuid = UUID.randomUUID();
                } while (repository.findByPix(uuid.toString()) != null); // Verify exists random key in database
                account.setPixRandomKey(uuid.toString());
            }
            case "email" -> {
                if (repository.findByPix(account.getUser().getEmail()) == null) {
                    account.setPixEmail(account.getUser().getEmail());
                } else throw new DataIntegrityViolationException("Chave de Email já cadastrada.");
            }
            case "phone" -> {
                if (repository.findByPix(account.getUser().getPhone()) == null) {
                    account.setPixPhone(account.getUser().getPhone());
                } else throw new DataIntegrityViolationException("Chave de telefone já cadastrada.");
            }
        }

        repository.save(account);
    }

    public Account findByPix(String key) {
        return repository.findByPix(key);
    }

    public List<Account> findAll() {
        return repository.findAll();
    }

    public void deposit(DepositDTO depositDTO) {
        Account account = returnAccountByLogin.findByLogin(depositDTO.login());

        if (account == null) account = repository.findByPix(depositDTO.login());

        if (account == null) throw new UsernameNotFoundException("Conta inexistente.");

        account.setBalance(account.getBalance().add(depositDTO.value()));

        repository.save(account);
    }

    public void deleteAccount(int id) {
        repository.deleteById(id);
    }

    @Transactional
    public void updatePassword(Account account, boolean passwordLogin) {
        if (passwordLogin) {
            account.setPassword(passwordEncoder().encode(account.getPassword()));
            repository.changePasswordByNumberAccount(account.getNumber(), account.getPassword());
        } else {
            account.setPasswordTransaction(passwordEncoder().encode(account.getPasswordTransaction()));
            repository.changePasswordTransactionByNumberAccount(account.getNumber(), account.getPasswordTransaction());
        }
    }

    public void sendEmailForgotPassword(String login) throws MessagingException {
        String code = Base64.getEncoder().encodeToString(login.getBytes());

        Account account = findByLogin(login);

        emailService.sendEmailForgotPassword(account, code);
    }

    @Transactional
    public void updateBalance(int number, BigDecimal balance) {
        repository.updateBalance(number, balance);
    }

    public Account findByCpf(String cpf) {
        return repository.findByCpf(cpf);
    }

    public Account findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Account findByPhone(String phone) {
        return repository.findByPhone(phone);
    }
}

