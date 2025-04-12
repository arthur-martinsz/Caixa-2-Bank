package com.ifgoiano.caixa2bank.entities.user;

import com.ifgoiano.caixa2bank.entities.account.NewAccountDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "cpf", unique = true, nullable = false, length = 11, updatable = false)
    private String cpf;

    @Column(name = "username", nullable = false)
    public String username;

    @Column(name = "email", nullable = false, unique = true)
    public String email;

    @Column(name = "phone", nullable = false, unique = true)
    public String phone;

    @Column(name = "admin-password", nullable = true)
    public String adminPassword;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_authorities",
            joinColumns = @JoinColumn(name = "user_cpf"),
            inverseJoinColumns = @JoinColumn(name = "authorities_id"))
    List<Authority> authorities;

    public User(NewAccountDTO newAccountDTO) { // User
        this.setUsername(newAccountDTO.username());
        this.setCpf(newAccountDTO.cpf());
        this.setEmail(newAccountDTO.email());
        this.setPhone(newAccountDTO.phone());
    }

    public User(UserAdminDTO newUser) { // Admin
        this.setUsername(newUser.username());
        this.setAdminPassword(newUser.password());
        this.setCpf(newUser.cpf());
        this.setEmail(newUser.email());
        this.setPhone(newUser.phone());
    }
}
