package application.tools;

import application.DailyBankState;
import application.control.ExceptionDialog;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class RelevePDF {
    public static void genereRelevePDF(Stage primaryStage, DailyBankState dailyBankState, CompteCourant cpt, Client clientDesComptes) {
        Document d = new Document();
        try {
            if (!Files.exists(Paths.get("releves"))) {
                Files.createDirectory(Paths.get("releves"));
            }
            String pdfFilename = "releves/Releve du compte no" + cpt.idNumCompte +" Date "+ LocalDate.now()+ ".pdf";
            PdfWriter writer = PdfWriter.getInstance(d, new FileOutputStream(pdfFilename));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        d.open();

        Font titleFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 20);
        Font descFont = FontFactory.getFont(FontFactory.TIMES, 10);

        // titre
        Paragraph titre = new Paragraph("Relevé de compte", titleFont);
        titre.setAlignment(Element.ALIGN_CENTER);

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

            LocalDate date = LocalDate.now();
            Month month = date.getMonth();
            LocalDate firstDay = LocalDate.of(date.getYear(), month, 1);
            Date firstDayDate = Date.from(firstDay.atStartOfDay(ZoneId.systemDefault()).toInstant());

            for (Operation o : ope) {
                if (o.dateOp.compareTo(firstDayDate) < 0) {
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
