package com.ifgoiano.caixa2bank.services;

import com.ifgoiano.caixa2bank.entities.user.Authority;
import com.ifgoiano.caixa2bank.entities.user.User;
import com.ifgoiano.caixa2bank.entities.user.UserAdminDTO;
import com.ifgoiano.caixa2bank.services.authority.AuthorityService;
import com.ifgoiano.caixa2bank.services.user.UserService;
import com.ifgoiano.caixa2bank.utils.ReturnAccountByLogin;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DbInit {

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReturnAccountByLogin returnAccountByLogin;

    @PostConstruct
    public void createAuthorities() {
        Authority authorityAdmin = new Authority(1L, "admin");
        Authority authorityUser = new Authority(2L, "user");

        List<Authority> authorities = new ArrayList<>();
        authorities.add(authorityAdmin);
        authorities.add(authorityUser);

        authorityService.saveAllAuthority(authorities);
    }

    @PostConstruct
    public void createAdmin() {
        UserAdminDTO newUser = new UserAdminDTO("admin", "admin", "00000000011",
                "admin@gmail.com", "(62)900000000"
        );

        User user = new User(newUser);

        if (
                returnAccountByLogin.findByAdmin(user.getPhone()) == null &&
                        returnAccountByLogin.findByAdmin(user.getCpf()) == null &&
                        returnAccountByLogin.findByAdmin(user.getEmail()) == null
        ) {
            userService.saveAdmin(user);
        }
    }
}