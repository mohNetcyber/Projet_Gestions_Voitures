package main;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;

public class TestPdf {
    public static void main(String[] args) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("facture_test.pdf"));
            document.open();
            document.add(new Paragraph("Hello PDF avec OpenPDF !"));
            document.close();
            System.out.println("PDF généré avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
