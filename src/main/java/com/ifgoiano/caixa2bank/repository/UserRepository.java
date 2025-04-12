package com.ifgoiano.caixa2bank.repository;

import com.ifgoiano.caixa2bank.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {
    //@Query("select u from conta u where u.username like :username")
    //@Query("SELECT u FROM User u WHERE u.username = ?")
    User findByCpf(String cpf);

    @Query("select u from User u where u.email like :login " +
            "OR u.cpf like :login OR u.phone like :login")
    User findByLogin(String login);

    List<User> findAll();

    @Query("select u from User u where u.phone like :login OR " +
            "u.cpf like :login OR " +
            "u.email like :login")
    User findByAdmin(String login);
}
