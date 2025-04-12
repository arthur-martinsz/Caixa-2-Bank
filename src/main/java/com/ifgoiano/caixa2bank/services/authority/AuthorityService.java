package com.ifgoiano.caixa2bank.services.authority;

import com.ifgoiano.caixa2bank.entities.user.Authority;
import com.ifgoiano.caixa2bank.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorityService {

    @Autowired
    private AuthorityRepository repository;

    public Authority findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Authority saveAuthority(Authority authority) {
        return repository.save(authority);
    }

    public void saveAllAuthority(List<Authority> authority) {
        repository.saveAll(authority);
    }

}
