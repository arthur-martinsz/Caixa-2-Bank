package com.ifgoiano.caixa2bank.services.transaction;

import com.ifgoiano.caixa2bank.entities.account.Account;
import com.ifgoiano.caixa2bank.entities.transaction.ListTransactionDTO;
import com.ifgoiano.caixa2bank.entities.transaction.Transaction;
import com.ifgoiano.caixa2bank.entities.transaction.TransactionDTO;
import com.ifgoiano.caixa2bank.exception.TransactionError;
import com.ifgoiano.caixa2bank.repository.TransactionRepository;
import com.ifgoiano.caixa2bank.services.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionRepository transactionRepository;

    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public void send(TransactionDTO tr) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal != null) {
            Account receiver = accountService.findByPix(tr.receiver());
            Account sender = accountService.findByLogin(principal.getUsername());

            if (receiver == null) receiver = accountService.findByNumberAccount(Integer.parseInt(tr.receiver()));

            // Tests
            if (receiver == null)
                throw new TransactionError("Conta com a chave ou número da conta não foi encontrada. Chave: " + tr.receiver());

            else if (receiver == sender)
                throw new TransactionError("Não é possível fazer transações para si mesmo.");

            else if (tr.value() == null)
                throw new TransactionError("O valor da transação deve ser maior que 0.");

            else if (tr.value().compareTo(BigDecimal.ZERO) == 0 || tr.value().compareTo(BigDecimal.ZERO) < 0)
                throw new TransactionError("O valor da transação deve ser maior que 0.");

            else if (!(sender.getBalance().compareTo(tr.value()) >= 0))
                throw new TransactionError("Saldo o suficiente para a transação.");

            else if (!passwordEncoder().matches(tr.passwordTransaction(), sender.getPasswordTransaction()))
                throw new TransactionError("Senha para a transação incorreta.");

            receiver.setBalance(receiver.getBalance().add(tr.value()));
            sender.setBalance(sender.getBalance().subtract(tr.value()));

            accountService.updateBalance(receiver.getNumber(), receiver.getBalance());
            accountService.updateBalance(sender.getNumber(), sender.getBalance());

            Transaction transaction = new Transaction(tr.value(), LocalDateTime.now(), sender, receiver);

            transactionRepository.save(transaction);
        }
    }

    public List<ListTransactionDTO> findAllTransactions(Account account) {
        List<Transaction> allTransactions = transactionRepository.findAllTransactions(account);
        List<ListTransactionDTO> transactions = new ArrayList<ListTransactionDTO>();

        allTransactions.forEach(t -> {
            String sender = t.getSender().getUser().getUsername();
            String receiver = t.getReceiver().getUser().getUsername();

            if (account.getUser().getCpf().equals(t.getSender().getUser().getCpf())) sender = "Você";
            else if (account.getUser().getCpf().equals(t.getReceiver().getUser().getCpf())) receiver = "Você";

            DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            String hour = t.getTime().format(hourFormatter);
            String date = t.getTime().format(dateFormatter);

            String dateTime = date + " às " + hour;

            ListTransactionDTO transaction = new ListTransactionDTO(
                    t.getId(),
                    sender,
                    t.getValue(),
                    receiver,
                    dateTime
            );

            transactions.add(transaction);
        });

        return transactions;
    }
}
