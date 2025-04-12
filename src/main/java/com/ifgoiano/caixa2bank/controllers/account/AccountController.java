package com.ifgoiano.caixa2bank.controllers.account;

import com.ifgoiano.caixa2bank.entities.account.Account;
import com.ifgoiano.caixa2bank.entities.account.KeysDTO;
import com.ifgoiano.caixa2bank.entities.account.KeysExistisDTO;
import com.ifgoiano.caixa2bank.services.account.AccountService;
import com.ifgoiano.caixa2bank.services.account.KeysService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    AccountService accountService;

    @Autowired
    KeysService keysService;

    @GetMapping("/pix")
    public ModelAndView getPagePix() {
        ModelAndView view = new ModelAndView("area-pix");

        KeysDTO keys = keysService.getKeysAccount();
        view.addObject("keys", keys);

        KeysExistisDTO registeredKeys = keysService.checkRegisteredKeys();
        view.addObject("checkKeys", registeredKeys);

        return view;
    }

    @GetMapping("/register-key/{key}")
    public ModelAndView registerKey(@PathVariable String key) {
        ModelAndView view = new ModelAndView("area-pix");

        try {
            accountService.saveKey(key);
        } catch (DataIntegrityViolationException ex) {
            view.addObject("alert", "error");
            view.addObject("title", "Cadastro de chaves pix.");
            view.addObject("text", ex.getMessage());
            view.addObject("subtext", "Tente cadastrar outra chave.");

            KeysDTO keys = keysService.getKeysAccount();
            view.addObject("keys", keys);

            KeysExistisDTO registeredKeys = keysService.checkRegisteredKeys();
            view.addObject("checkKeys", registeredKeys);

            return view;
        }

        view.addObject("alert", "success");
        view.addObject("title", "Cadastro de chaves pix.");
        view.addObject("text", "Cadastro de chave pix realizado com sucesso.");
        view.addObject("subtext", "Você já pode receber transação por essa chave.");

        KeysDTO keys = keysService.getKeysAccount();
        view.addObject("keys", keys);

        KeysExistisDTO registeredKeys = keysService.checkRegisteredKeys();
        view.addObject("checkKeys", registeredKeys);

        return view;
    }

    @GetMapping("/forgot-password")
    public String getForgotPasswordPage(ModelMap mM, @RequestParam(value = "code", required = false) String code) {
        Account account = null;

        if (code != null) {
            byte[] bytesDecode = Base64.getDecoder().decode(code.getBytes(StandardCharsets.UTF_8));
            String login = new String(bytesDecode, StandardCharsets.UTF_8);

            account = accountService.findByLogin(login);

            account.setPassword("");
        }

        mM.addAttribute("account", account);

        return "forgot-password";
    }

    @GetMapping("/email-forgot-password")
    public String sendEmailForgot(@RequestParam("login") String login, RedirectAttributes attr) throws MessagingException {
        accountService.sendEmailForgotPassword(login);

        attr.addFlashAttribute("alert", "success");
        attr.addFlashAttribute("title", "Alteração de senha");
        attr.addFlashAttribute("text", "Email para alteração da senha enviado.");
        attr.addFlashAttribute("subtext", "Siga os passos para alterar sua senha.");

        return "redirect:/user/login";
    }

    @PostMapping("/forgot-password")
    public String getForgotPasswordPage(Account account, RedirectAttributes attr) {
        accountService.updatePassword(account, true);

        attr.addFlashAttribute("alert", "success");
        attr.addFlashAttribute("title", "Alteração de senha");
        attr.addFlashAttribute("text", "Sua senha foi alterada com sucesso, faça login.");
        attr.addFlashAttribute("subtext", "Entre em contato com o suporte caso algo esteja fora do comum...");

        return "redirect:/user/login";
    }

}
