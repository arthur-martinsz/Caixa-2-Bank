package com.ifgoiano.caixa2bank.repository;

import com.ifgoiano.caixa2bank.entities.account.Account;
import com.ifgoiano.caixa2bank.entities.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("select t from Transaction t where t.receiver = :account OR t.sender = :account ORDER BY t.time ASC")
    List<Transaction> findAllTransactions(Account account);
}
