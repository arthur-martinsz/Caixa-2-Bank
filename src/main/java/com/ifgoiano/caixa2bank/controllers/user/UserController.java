package com.ifgoiano.caixa2bank.controllers.user;

import com.ifgoiano.caixa2bank.entities.account.Account;
import com.ifgoiano.caixa2bank.entities.account.NewAccountDTO;
import com.ifgoiano.caixa2bank.entities.user.Authority;
import com.ifgoiano.caixa2bank.entities.user.User;
import com.ifgoiano.caixa2bank.services.account.AccountService;
import com.ifgoiano.caixa2bank.services.authority.AuthorityService;
import com.ifgoiano.caixa2bank.services.user.UserDataService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private AuthorityService authorityRepository;

    @GetMapping("/login-error")
    public String getErrorPage(ModelMap mM) {
        mM.addAttribute("alert", "error");
        mM.addAttribute("title", "Erro na tentativa de login");
        mM.addAttribute("text", "Senha ou login incorretos, essa conta é sua mesmo????");
        mM.addAttribute("subtext", "Entre em contato com o suporte caso algo esteja fora do comum...");

        return "login";
    }

    @PostMapping("/register")
    public String registerUser(NewAccountDTO newAccountDTO, RedirectAttributes attr) {
        User user = new User(newAccountDTO);

        List<Authority> authorities = new ArrayList<Authority>();

        Authority authority = authorityRepository.findById(2L);

        authorities.add(authority);

        user.setAuthorities(authorities);

        Account account = new Account(newAccountDTO.password(), newAccountDTO.passwordTransaction(), user);

        try {
            accountService.saveAccount(account);
        } catch (DataIntegrityViolationException ex) {
            attr.addFlashAttribute("alert", "error");
            attr.addFlashAttribute("title", "Erro ao criar conta");
            attr.addFlashAttribute("text", ex.getMessage());
            attr.addFlashAttribute("subtext", "Entre em contato com o suporte para saber mais.");

            attr.addFlashAttribute("account", account);
            return "redirect:/user/register";
        }

        attr.addFlashAttribute("alert", "success");
        attr.addFlashAttribute("title", "Conta criada com sucesso");
        attr.addFlashAttribute("text", "Número da conta enviado para o email cadastrado.");
        attr.addFlashAttribute("subtext", "Obrigado por escolher esse banco nada confiável.");

        return "redirect:/user/login";
    }

    @GetMapping("/dashboard")
    public ModelAndView getDashboardPage() {
        ModelAndView view = new ModelAndView("user-dashboard");

        view.addObject("userData", userDataService.data());

        return view;
    }

    @GetMapping("/verify-role-user")
    public String verifyRole() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal == null) return "index";

        if (principal.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("admin"))) {
            return "redirect:/admin/dashboard";
        }

        return "redirect:/user/dashboard";
    }

    @GetMapping("/update/{login}")
    public ModelAndView getUpdateAccount(@PathVariable int login) {
        ModelAndView view = new ModelAndView("update-account");

        view.addObject("account", accountService.findByNumberAccount(login));

        return view;
    }

    @PostMapping("/update/account")
    public String updateAccount(HttpServletRequest request, Account account, RedirectAttributes attr) {
        accountService.updateAccount(account);

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        attr.addFlashAttribute("alert", "success");
        attr.addFlashAttribute("title", "Dados alterados com sucesso");
        attr.addFlashAttribute("text", "Faça login novamente para concluir a alteração.");
        attr.addFlashAttribute("subtext", "Entre em contato com o suporte caso algo esteja fora do comum...");

        return "redirect:/user/login";
    }


}
