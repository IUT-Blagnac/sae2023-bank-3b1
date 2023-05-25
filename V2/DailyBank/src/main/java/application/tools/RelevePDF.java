package application.tools;

import application.DailyBankState;
import application.control.ExceptionDialog;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Font;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.BaseColor  ;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.Access_BD_Operation;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

/**
 * Cette classe permet de générer un relevé de compte au format PDF
 * @author Émilien FIEU
 */
public class RelevePDF {
    /**
     * Cette méthode génère un relevé de compte au format PDF
     * @param primaryStage La fenêtre principale de l'application
     * @param dailyBankState L'état de l'application
     * @param cpt Le compte pour lequel on veut générer un relevé
     * @param clientDesComptes Le client propriétaire du compte
     * @param mois Le mois du relevé
     * @param annee L'année du relevé
     * @autor Émilien FIEU
     */
    public static void genereRelevePDF(Stage primaryStage, DailyBankState dailyBankState, CompteCourant cpt, Client clientDesComptes, Month mois, Year annee, String fileLocation) {
        Document d = new Document();

        try {
            PdfWriter writer = PdfWriter.getInstance(d, new FileOutputStream(fileLocation));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        d.open();

        Font titleFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 20);
        Font descFont = FontFactory.getFont(FontFactory.TIMES, 10);

        // titre
        Paragraph titre = new Paragraph("Relevé de compte", titleFont);
        titre.setAlignment(Element.ALIGN_CENTER);

        // ligne de séparation

        LineSeparator ls = new LineSeparator();
        Chunk linebreak = new Chunk(ls);

        // infos client
        Paragraph infosClient = new Paragraph("Client : \n"
                + clientDesComptes.nom + " " + clientDesComptes.prenom + "\n"
                + clientDesComptes.adressePostale + "\n"
                + clientDesComptes.telephone
                + "\n\nCompte n°"
                + cpt.idNumCompte);
        infosClient.setAlignment(Element.ALIGN_LEFT);
        infosClient.setFont(descFont);

        // infos agence

        Paragraph infosAgence = new Paragraph("Agence : \n"
                + dailyBankState.getAgenceActuelle().nomAg + "\n"
                + dailyBankState.getAgenceActuelle().adressePostaleAg);
        infosAgence.setAlignment(Element.ALIGN_RIGHT);
        infosAgence.setFont(descFont);

        // tableau des opérations
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // entête du tableau
        PdfPCell cell1 = new PdfPCell(new Paragraph("Identifiant opération"));
        PdfPCell cell2 = new PdfPCell(new Paragraph("Date"));
        PdfPCell cell3 = new PdfPCell(new Paragraph("Libellé"));
        PdfPCell cell4 = new PdfPCell(new Paragraph("Montant"));

        cell1.setBackgroundColor(BaseColor.GRAY);
        cell2.setBackgroundColor(BaseColor.GRAY);
        cell3.setBackgroundColor(BaseColor.GRAY);
        cell4.setBackgroundColor(BaseColor.GRAY);

        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);

        // contenu du tableau

        try {
            Access_BD_Operation acc = new Access_BD_Operation();
            ArrayList<Operation> ope = acc.getOperations(cpt.idNumCompte);

            Date dateDebutDate = Date.from(LocalDate.of(annee.getValue(), mois.getValue(), 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date dateFinDate = Date.from(LocalDate.of(annee.getValue(), mois.getValue(), mois.length(annee.isLeap())).atStartOfDay(ZoneId.systemDefault()).toInstant());
            for (Operation o : ope) {
                if (o.dateOp.before(dateDebutDate) || o.dateOp.after(dateFinDate)) {
                    continue;
                }
                table.addCell(String.valueOf(o.idOperation));
                table.addCell(o.dateOp.toString());
                table.addCell(o.idTypeOp);
                table.addCell(String.valueOf(o.montant));
            }
        } catch (DatabaseConnexionException e) {
            ExceptionDialog ed = new ExceptionDialog(primaryStage, dailyBankState, e);
            ed.doExceptionDialog();
            primaryStage.close();
        } catch (ApplicationException ae) {
            ExceptionDialog ed = new ExceptionDialog(primaryStage, dailyBankState, ae);
            ed.doExceptionDialog();
        }

        // ajout des couleurs alternatives

        boolean pair = false;
        boolean first = true;
        for (PdfPRow row : table.getRows()) {
            if (first) {
                first = false;
                continue;
            }
            for (PdfPCell cell : row.getCells()) {
                cell.setBackgroundColor((pair ? BaseColor.LIGHT_GRAY : BaseColor.WHITE));
            }
            pair = !pair;
        }

        try {
            d.add(titre);
            d.add(linebreak);
            d.add(infosClient);
            d.add(infosAgence);
            d.add(table);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        d.close();

        AlertUtilities.showAlert(primaryStage, "Génération du relevé",
                "Succès génération relevé", "Le relevé a été généré avec succès", Alert.AlertType.INFORMATION);

    }
}
