package com.ifgoiano.caixa2bank.controllers.transaction;

import com.ifgoiano.caixa2bank.entities.account.Account;
import com.ifgoiano.caixa2bank.entities.transaction.ListTransactionDTO;
import com.ifgoiano.caixa2bank.entities.transaction.TransactionDTO;
import com.ifgoiano.caixa2bank.exception.TransactionError;
import com.ifgoiano.caixa2bank.services.account.AccountService;
import com.ifgoiano.caixa2bank.services.pdf.PdfGenerator;
import com.ifgoiano.caixa2bank.services.transaction.TransactionService;
import com.ifgoiano.caixa2bank.services.user.UserDataService;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PdfGenerator pdfGenerator;

    @PostMapping("/send")
    public String sendCash(RedirectAttributes attr, TransactionDTO tr) {
        attr.addFlashAttribute("userData", userDataService.data());

        try {
            transactionService.send(tr);
        } catch (TransactionError t) {
            attr.addFlashAttribute("alert", "error");
            attr.addFlashAttribute("title", "Erro ao prosseguir com a transação.");
            attr.addFlashAttribute("text", t.getMessage());
            attr.addFlashAttribute("subtext", "Entre em contato com o suporte caso algo esteja fora do comum...");

            return "redirect:/user/dashboard";
        }

        attr.addFlashAttribute("alert", "success");
        attr.addFlashAttribute("title", "Sucesso ao prosseguir com a transação.");
        attr.addFlashAttribute("text", "A transação está disponível no extrato para visualização.");
        attr.addFlashAttribute("subtext", "Entre em contato com o suporte caso algo esteja fora do comum...");

        return "redirect:/user/dashboard";
    }

    @GetMapping("/list")
    public ModelAndView listAllTransactions() {
        ModelAndView view = new ModelAndView("transactions");

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account account = accountService.findByLogin(principal.getUsername());

        List<ListTransactionDTO> transactions = transactionService.findAllTransactions(account);

        view.addObject("transactions", transactions);

        return view;
    }

    @GetMapping("/pdf")
    public void generatePdf(HttpServletResponse response) throws IOException, DocumentException {
        pdfGenerator.generatePdf(response);
    }
}
