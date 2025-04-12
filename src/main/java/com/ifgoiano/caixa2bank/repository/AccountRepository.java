package com.ifgoiano.caixa2bank.repository;

import com.ifgoiano.caixa2bank.entities.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    Account findByNumber(int account);

    @Query("select u from Account u where u.user.cpf like :cpf")
    Account findByCpf(@Param("cpf") String cpf);

    @Query("select u from Account u where u.number = :number")
    Account findByNumberAccount(int number);

    @Query("select u from Account u where u.user.email like :email")
    Account findByEmail(@Param("email") String email);

    @Query("select u from Account u where u.user.phone like :phone")
    Account findByPhone(@Param("phone") String phone);

    @Query("select u from Account u where u.pixRandomKey like :key " +
            "OR u.pixCpf like :key OR u.pixEmail like :key OR u.pixPhone like :key")
    Account findByPix(String key);

    @Modifying
    @Query("update Account a set a.password = :newPassword where a.number = :number")
    void changePasswordByNumberAccount(@Param("number") int number, @Param("newPassword") String password);

    @Modifying
    @Query("update Account a set a.passwordTransaction = :passwordT where a.number = :number")
    void changePasswordTransactionByNumberAccount(@Param("number") int number, @Param("passwordT") String passwordTransaction);

    @Modifying
    @Query("update Account a set a.balance = :balance where a.number = :number")
    void updateBalance(@Param("number") int number, @Param("balance") BigDecimal balance);
}
