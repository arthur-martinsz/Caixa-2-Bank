package com.ifgoiano.caixa2bank.entities.transaction;

import com.ifgoiano.caixa2bank.entities.account.Account;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 6, updatable = false)
    private Long id;

    @Column(name = "value", nullable = false, updatable = false)
    private BigDecimal value;

    @Column(columnDefinition = "TIMESTAMP with time zone", updatable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name = "sender_number")
    @NotNull
    private Account sender;

    @ManyToOne
    @JoinColumn(name = "receiver_number")
    @NotNull
    private Account receiver;

    public Transaction(BigDecimal value, LocalDateTime time, Account sender, Account receiver) {
        this.value = value;
        this.time = time;
        this.sender = sender;
        this.receiver = receiver;
    }
}
