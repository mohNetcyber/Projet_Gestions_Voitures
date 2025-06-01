package main;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import model.Agence;
import model.Client;
import model.Contrat;
import model.Vehicule;

import java.awt.Color;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FacturePDFGenerator {

    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 16, Font.BOLD);
    private static final Font REGULAR_FONT = new Font(Font.HELVETICA, 12);
    private static final Font FOOTER_FONT = new Font(Font.HELVETICA, 10, Font.ITALIC);
    private static final String LOGO_PATH = "resources/logo.png"; // change this if needed

    public static void generatePDF(Contrat contrat, Client client, Vehicule vehicule,
                                   Agence agence, int jours, double montantJour,
                                   double montantTotal, int kmParcouru) {
        String fileName = "Facture_" + contrat.getIdContrat()+"_" +client.getNom()+"_"+client.getPrenom() +".pdf";

        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, fos);
            writer.setPageEvent(new HeaderFooterPageEvent(agence));

            document.open();

            ajouterTitre(document);
            ajouterTableauInformations(document, contrat, client, vehicule, agence, jours, montantJour, montantTotal, kmParcouru);

            document.close();
            System.out.println("PDF généré : " + fileName);

        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du PDF : " + e.getMessage());
        }
    }

    private static void ajouterTitre(Document document) throws DocumentException {
        document.add(new Paragraph(" "));
        Paragraph title = new Paragraph("FACTURE DE LOCATION", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));
    }

    private static void ajouterTableauInformations(Document document, Contrat contrat, Client client,
                                                   Vehicule vehicule, Agence agence, int jours,
                                                   double montantJour, double montantTotal,
                                                   int kmParcouru) throws DocumentException {
    	PdfPTable table = new PdfPTable(2);
    	table.setWidthPercentage(80);
    	table.setSpacingBefore(20f);
    	table.setSpacingAfter(20f);

    	// Entête personnalisée
    	PdfPCell header1 = new PdfPCell(new Phrase("Détail", new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE)));
    	PdfPCell header2 = new PdfPCell(new Phrase("Valeur", new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE)));

    	header1.setBackgroundColor(new Color(70, 130, 180)); // bleu acier
    	header2.setBackgroundColor(new Color(70, 130, 180));

    	header1.setHorizontalAlignment(Element.ALIGN_CENTER);
    	header2.setHorizontalAlignment(Element.ALIGN_CENTER);

    	table.addCell(header1);
    	table.addCell(header2);

    	// Lignes de données
    	addStyledCell(table, "Contrat #", String.valueOf(contrat.getIdContrat()));
    	addStyledCell(table, "Client", client.getNom() + " " + client.getPrenom());
    	addStyledCell(table, "Véhicule", vehicule.getMarque());
    	addStyledCell(table, "Agence de départ", agence.getNomAgence());
    	String rawDate = contrat.getDateDepart();
    	String formattedDate = rawDate;

    	try {
    	    // Adapter le format selon ce que retourne exactement getDateDepart()
    	    SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
    	    Date date = parser.parse(rawDate);
    	    formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(date);
    	} catch (Exception e) {
    	    // Si le format échoue, on garde la chaîne brute
    	    System.err.println("Erreur de formatage de la date : " + e.getMessage());
    	}
    	addStyledCell(table, "Date de départ", formattedDate); // supposée déjà formatée
    	addStyledCell(table, "Nombre de jours", String.valueOf(jours));
    	addStyledCell(table, "Kilométrage parcouru", kmParcouru + " km");
    	addStyledCell(table, "Montant journalier", String.format("%.2f DH", montantJour));
    	addStyledCell(table, "Montant total", String.format("%.2f DH", montantTotal));

    	document.add(table);
    }

    private static void addCellToTable(PdfPTable table, String key, String value) {
        PdfPCell cell1 = new PdfPCell(new Phrase(key, REGULAR_FONT));
        PdfPCell cell2 = new PdfPCell(new Phrase(value, REGULAR_FONT));
        cell1.setBorder(Rectangle.NO_BORDER);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        table.addCell(cell2);
    }

    // Classe d'événement pour l'en-tête/pied de page
    static class HeaderFooterPageEvent extends PdfPageEventHelper {
        private final Agence agence;
        private final Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        public HeaderFooterPageEvent(Agence agence) {
            this.agence = agence;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Rectangle rect = document.getPageSize();

            // Logo (optionnel)
            try {
                Image logo = Image.getInstance(LOGO_PATH);
                logo.scaleToFit(50, 50);
                logo.setAbsolutePosition(rect.getLeft(40), rect.getTop() - 50);
                cb.addImage(logo);
            } catch (Exception e) {
                // logo manquant, ignorer
            }

            // En-tête texte
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                    new Phrase("Agence: " + agence.getNomAgence(), headerFont),
                    rect.getLeft(100), rect.getTop(30), 0);

            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    new Phrase("Date: " + dateFormat.format(new Date()), headerFont),
                    rect.getRight(40), rect.getTop(30), 0);

            // Pied de page
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    new Phrase("Merci pour votre confiance - Page " + writer.getPageNumber(), FOOTER_FONT),
                    (rect.getLeft() + rect.getRight()) / 2, rect.getBottom(30), 0);
        }
    }
    
    private static void addStyledCell(PdfPTable table, String key, String value) {
        PdfPCell cell1 = new PdfPCell(new Phrase(key, new Font(Font.HELVETICA, 11, Font.BOLD)));
        PdfPCell cell2 = new PdfPCell(new Phrase(value, new Font(Font.HELVETICA, 11)));

        cell1.setBackgroundColor(new Color(240, 240, 240));
        cell2.setBackgroundColor(Color.WHITE);

        cell1.setPadding(8);
        cell2.setPadding(8);

        cell1.setBorderWidth(0.5f);
        cell2.setBorderWidth(0.5f);

        table.addCell(cell1);
        table.addCell(cell2);
    }

}
