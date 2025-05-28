package com.bank.integra.services.bank;

import com.bank.integra.entities.details.Transaction;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

//TODO уже хренотень: ачо будет если не сгенерит квитанцию????? АЧО АКАК?? М???
//TODO Ну лучше в бд сохранять  както
@Service
public class AsyncPdfGenerationService {
    @Autowired
    TransactionsService transactionsService;

    public void generateReceiptAsync(String transactionId) {
        System.out.println("Начинаем *:･ﾟ✧*:･ﾟ✧асинхронную*:･ﾟ✧*:･ﾟ✧ генерацию пдф");
        try (InputStream templateStream = getClass().getResourceAsStream("/pdf/cvfg.pdf")) {
            if (templateStream == null) {
                throw new RuntimeException("Template pdf not found in classpath");
            }
            Optional<Transaction> transactionOptional = transactionsService.getTransactionById(Integer.parseInt(transactionId));
            Transaction transaction;
            if (transactionOptional.isPresent()) {
                transaction = transactionOptional.get();
            } else {
                return;
            }
            PdfReader reader = new PdfReader(templateStream);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfStamper stamper = new PdfStamper(reader, baos);
            PdfContentByte canvas = stamper.getOverContent(1);

            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED);
            canvas.setFontAndSize(baseFont, 12);

            BaseFont boldFont = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1250, BaseFont.EMBEDDED);
            BaseFont regularFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.EMBEDDED);

            canvas.beginText();

            // Заголовок жирным и побольше
            canvas.setFontAndSize(boldFont, 14);
            canvas.setRGBColorFill(0, 0, 128); // тёмно-синий
            canvas.showTextAligned(PdfContentByte.ALIGN_LEFT, "Invoice:#" + transactionId, 70, 600, 0);

            // Дата — серым
            canvas.setFontAndSize(regularFont, 12);
            canvas.setRGBColorFill(100, 100, 100);
            canvas.showTextAligned(PdfContentByte.ALIGN_LEFT, "Date: " + transaction.getEventTimeStamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), 70, 570, 0);

            // Отправитель жирным
            canvas.setFontAndSize(boldFont, 12);
            canvas.setRGBColorFill(0, 0, 0); // чёрный
            canvas.showTextAligned(PdfContentByte.ALIGN_LEFT, "From: " + transaction.getSender().getFirstName() + " " + transaction.getSender().getLastName(), 70, 540, 0);

            // Получатель
            canvas.setFontAndSize(regularFont, 12);
            canvas.showTextAligned(PdfContentByte.ALIGN_LEFT, "To: " + transaction.getRecipient().getFirstName() + " " + transaction.getRecipient().getLastName(), 70, 510, 0);

            // Сумма — зелёным и жирным
            canvas.setFontAndSize(boldFont, 12);
            canvas.setRGBColorFill(0, 128, 0); // зелёный
            canvas.showTextAligned(PdfContentByte.ALIGN_LEFT, "Amount: $" + transaction.getBalance(), 70, 480, 0);

            // Описание — обычным шрифтом
            canvas.setFontAndSize(regularFont, 12);
            canvas.setRGBColorFill(0, 0, 0); // чёрный
            canvas.showTextAligned(PdfContentByte.ALIGN_LEFT, "Description: " + transaction.getDescription(), 70, 450, 0);

            canvas.endText();


            stamper.close();
            reader.close();

            byte[] pdfBytes = baos.toByteArray();

            String tmpDir = System.getProperty("java.io.tmpdir");
            String filename = tmpDir + File.separator + transactionId + "_receipt.pdf";

            try (FileOutputStream fos = new FileOutputStream(filename)) {
                fos.write(pdfBytes);
            }

            System.out.println("PDF сохранён на диск по пути: " + filename);

        } catch (Exception p) {
            System.out.println("хрен его знает, что с тем пдф:");
            p.printStackTrace();
        }
    }
}
