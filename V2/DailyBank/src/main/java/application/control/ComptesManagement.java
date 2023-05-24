package application.control;

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

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ComptesManagementController;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.fonts.otf.TableHeader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.Access_BD_CompteCourant;
import model.orm.Access_BD_Operation;
import model.orm.exception.*;

public class ComptesManagement {

	private Stage primaryStage;
	private ComptesManagementController cmcViewController;
	private DailyBankState dailyBankState;
	private Client clientDesComptes;

	public ComptesManagement(Stage _parentStage, DailyBankState _dbstate, Client client) {

		this.clientDesComptes = client;
		this.dailyBankState = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(ComptesManagementController.class.getResource("comptesmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des comptes");
			this.primaryStage.setResizable(false);

			this.cmcViewController = loader.getController();
			this.cmcViewController.initContext(this.primaryStage, this, _dbstate, client);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doComptesManagementDialog() {
		this.cmcViewController.displayDialog();
	}

	public void gererOperationsDUnCompte(CompteCourant cpt) {
		OperationsManagement om = new OperationsManagement(this.primaryStage, this.dailyBankState,
				this.clientDesComptes, cpt);
		om.doOperationsManagementDialog();
	}

	public CompteCourant creerNouveauCompte() {
		CompteCourant compte;
		CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dailyBankState);
		compte = cep.doCompteEditorDialog(this.clientDesComptes, null, EditionMode.CREATION);
		if (compte != null) {
			try {
				Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
				acc.ajouterCompteCourant(compte, this.clientDesComptes.idNumCli);
				AlertUtilities.showAlert(this.primaryStage, "Création d'un compte",
						"Succès création compte", "Le nouveau compte a été créé avec succès", AlertType.INFORMATION);



				// if JAMAIS vrai
				// existe pour compiler les catchs dessous
				if (Math.random() < -1) {
					throw new ApplicationException(Table.CompteCourant, Order.INSERT, "todo : test exceptions", null);
				}
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
				ed.doExceptionDialog();
			}
		}
		return compte;
	}

	public ArrayList<CompteCourant> getComptesDunClient() {
		ArrayList<CompteCourant> listeCpt = new ArrayList<>();

		try {
			Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
			listeCpt = acc.getCompteCourants(this.clientDesComptes.idNumCli);
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeCpt = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
			ed.doExceptionDialog();
			listeCpt = new ArrayList<>();
		}
		return listeCpt;
	}

	public void supprimerCompte(CompteCourant cpt) throws RowNotFoundOrTooManyRowsException, DatabaseConnexionException, DataAccessException {
		// Le compte peut seulement être supprimé si son solde est à 0
		if (cpt.solde != 0 ) {
			AlertUtilities.showAlert(this.primaryStage, "Suppression d'un compte",
					"Erreur suppression compte", "Le compte ne peut pas être supprimé car le solde n'est pas à 0", AlertType.ERROR);
			return;
		}
		Access_BD_CompteCourant acc = new Access_BD_CompteCourant();
		acc.supprimerCompteCourant(cpt);
		AlertUtilities.showAlert(this.primaryStage, "Suppression d'un compte",
				"Succès suppression compte", "Le compte a été supprimé avec succès", AlertType.INFORMATION);
	}

	public void gererReleve(CompteCourant cpt) {

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
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
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

		AlertUtilities.showAlert(this.primaryStage, "Génération du relevé",
				"Succès génération relevé", "Le relevé a été généré avec succès", AlertType.INFORMATION);



	}
}
