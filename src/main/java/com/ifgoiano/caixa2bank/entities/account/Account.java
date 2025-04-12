package com.ifgoiano.caixa2bank.entities.account;

import com.ifgoiano.caixa2bank.entities.transaction.Transaction;
import com.ifgoiano.caixa2bank.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "num_seq")
    @SequenceGenerator(name = "num_seq", sequenceName = "number_seq", allocationSize = 1, initialValue = 100000)
    @Column(name = "number", nullable = false, length = 6, updatable = false)
    private Integer number;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "pix-random-key")
    private String pixRandomKey;

    @Column(name = "pix-phone")
    private String pixPhone;

    @Column(name = "pix-email")
    private String pixEmail;

    @Column(name = "pix-cpf")
    private String pixCpf;

    @Column(name = "password-transaction", nullable = false)
    private String passwordTransaction;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    User user;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Transaction> transactionsSent;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Transaction> transactionsReceived;

    public Account(String password, String passwordTransaction, User user) {
        this.password = password;
        this.setPasswordTransaction(passwordTransaction);
        this.user = user;
        this.setBalance(BigDecimal.valueOf(0));
    }

    public KeysDTO getKeys() {
        KeysDTO keys = new KeysDTO(this.getPixCpf(), this.getPixRandomKey(), this.getPixEmail(), this.getPixPhone());

        return keys;
    }

    public KeysExistisDTO checkRegisteredKeys() {

        KeysExistisDTO registeredKeys = new KeysExistisDTO(
                this.getPixCpf() == null,
                this.getPixRandomKey() == null,
                this.getPixPhone() == null,
                this.getPixEmail() == null
        );

        return registeredKeys;
    }

}
