package com.ifgoiano.caixa2bank.controllers.admin;

import com.ifgoiano.caixa2bank.entities.account.Account;
import com.ifgoiano.caixa2bank.entities.account.DepositDTO;
import com.ifgoiano.caixa2bank.entities.user.User;
import com.ifgoiano.caixa2bank.entities.user.UserAdminDTO;
import com.ifgoiano.caixa2bank.services.account.AccountService;
import com.ifgoiano.caixa2bank.services.user.UserDataService;
import com.ifgoiano.caixa2bank.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDataService userDataService;

    @GetMapping("/list-all")
    public ModelAndView listAllUsers() {
        ModelAndView view = new ModelAndView("list-all");

        List<Account> all = accountService.findAll();

        view.addObject("allUsers", all);

        return view;
    }

    @GetMapping("/dashboard")
    public String getDashboardPage(ModelMap mM) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        mM.addAttribute("user", userService.findByLogin(principal.getUsername()));

        return "admin-dashboard";
    }

    @PostMapping("/register-admin")
    public String getRegister(UserAdminDTO newUser, RedirectAttributes attr) {
        User user = new User(newUser);

        try {
            userService.saveAdmin(user);
        } catch (DataIntegrityViolationException d) {
            attr.addFlashAttribute("alert", "error");
            attr.addFlashAttribute("title", "Criação de conta admin não concluída.");
            attr.addFlashAttribute("text", d.getMessage());
            attr.addFlashAttribute("subtext", "Tente novamente com dados não cadastrados.");

            return "redirect:/admin/dashboard";
        }

        attr.addFlashAttribute("alert", "success");
        attr.addFlashAttribute("title", "Conta admin criada com sucesso");
        attr.addFlashAttribute("text", "Use o email para fazer o login na mesma.");
        attr.addFlashAttribute("subtext", "Essa conta tem funcionalidades exclusivas.");

        return "redirect:/admin/dashboard";
    }

    @PostMapping("/deposit")
    public String deposit(DepositDTO depositDTO, RedirectAttributes attr) {
        try {
            accountService.deposit(depositDTO);
        } catch (UsernameNotFoundException u) {
            attr.addFlashAttribute("alert", "error");
            attr.addFlashAttribute("title", "Depósito.");
            attr.addFlashAttribute("text", u.getMessage() + " Login: " + depositDTO.login());
            attr.addFlashAttribute("subtext", "O depósito não foi realizado.");

            return "redirect:/admin/list-all";
        }

        attr.addFlashAttribute("alert", "success");
        attr.addFlashAttribute("title", "Depósito.");
        attr.addFlashAttribute("text", "Depósito realizado com sucesso no login: " + depositDTO.login());
        attr.addFlashAttribute("subtext", "A conta já está com o saldo a mais.");

        return "redirect:/admin/list-all";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(RedirectAttributes attr, @PathVariable int id) {
        accountService.deleteAccount(id);

        attr.addFlashAttribute("alert", "success");
        attr.addFlashAttribute("title", "Exclusão de conta.");
        attr.addFlashAttribute("text", "Conta com id: " + id + ", foi excluída com sucesso.");
        attr.addFlashAttribute("subtext", "A conta não poderá ser acessada novamente.");

        return "redirect:/admin/list-all";
    }


}
