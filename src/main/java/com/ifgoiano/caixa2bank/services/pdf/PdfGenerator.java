package com.ifgoiano.caixa2bank.services.pdf;

import com.ifgoiano.caixa2bank.entities.account.Account;
import com.ifgoiano.caixa2bank.entities.transaction.ListTransactionDTO;
import com.ifgoiano.caixa2bank.services.account.AccountService;
import com.ifgoiano.caixa2bank.services.transaction.TransactionService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class PdfGenerator {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    public void generatePdf(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD:HH:MM:SS");
        String currentDateTime = dateFormat.format(new Date());
        String headerkey = "Content-Disposition";
        String headervalue = "attachment; filename=Transações_" + currentDateTime + ".pdf";
        response.setHeader(headerkey, headervalue);


        List<ListTransactionDTO> transactions = getTransactions();


        Document document = new Document(PageSize.A4);

        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font fontTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        fontTitle.setSize(20);

        Paragraph paragraph = new Paragraph("Transações", fontTitle);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingBefore(5);

        document.add(paragraph);

        PdfPTable table = new PdfPTable(5);

        table.setWidthPercentage(100f);
        table.setWidths(new int[]{1, 3, 3, 3, 3});
        table.setSpacingBefore(5);

        PdfPCell cell = new PdfPCell();

        cell.setBackgroundColor(CMYKColor.LIGHT_GRAY);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(4);

        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        font.setColor(CMYKColor.WHITE);

        cell.setPhrase(new Phrase("ID", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Enviado por", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Recebido por", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Valor", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Data e hora", font));
        table.addCell(cell);

        Font fontCell = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        fontCell.setSize(10);

        for (ListTransactionDTO transaction : transactions) {
            /*DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            String hour = transaction.time().format(hourFormatter);
            String date = transaction.time().format(dateFormatter);*/

            table.addCell(new PdfPCell(new Phrase(String.valueOf(transaction.id()), fontCell)));
            table.addCell(new PdfPCell(new Phrase(transaction.sender(), fontCell)));
            table.addCell(new PdfPCell(new Phrase(transaction.receiver(), fontCell)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(transaction.value()), fontCell)));
            table.addCell(new PdfPCell(new Phrase(transaction.time(), fontCell)));
        }

        document.add(table);

        document.close();
    }

    private List<ListTransactionDTO> getTransactions() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Account account = accountService.findByLogin(principal.getUsername());

        return transactionService.findAllTransactions(account);
    }
}
