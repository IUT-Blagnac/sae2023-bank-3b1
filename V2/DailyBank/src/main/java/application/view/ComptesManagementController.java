package application.view;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;

import application.DailyBankState;
import application.control.ComptesManagement;
import application.tools.AlertUtilities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.RowNotFoundOrTooManyRowsException;

public class ComptesManagementController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à ComptesManagementController
	private ComptesManagement cmDialogController;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDesComptes;
	private ObservableList<CompteCourant> oListCompteCourant;

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, ComptesManagement _cm, DailyBankState _dbstate, Client client) {
		this.cmDialogController = _cm;
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.clientDesComptes = client;
		this.configure();
	}

	private void configure() {
		String info;

		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.oListCompteCourant = FXCollections.observableArrayList();
		this.lvComptes.setItems(this.oListCompteCourant);
		this.lvComptes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvComptes.getFocusModel().focus(-1);
		this.lvComptes.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

		info = this.clientDesComptes.nom + "  " + this.clientDesComptes.prenom + "  (id : "
				+ this.clientDesComptes.idNumCli + ")";
		this.lblInfosClient.setText(info);

		this.loadList();
		this.validateComponentState();
	}

	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions

	@FXML
	private Label lblInfosClient;
	@FXML
	private ListView<CompteCourant> lvComptes;
	@FXML
	private Button btnVoirOpes;
	@FXML
	private Button btnModifierCompte;
	@FXML
	private Button btnSupprCompte;
	@FXML
	private Button btnReleve;

	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	@FXML
	private void doVoirOperations() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);
			this.cmDialogController.gererOperationsDUnCompte(cpt);
		}
		this.loadList();
		this.validateComponentState();
	}

	@FXML
	private void doModifierCompte() {
	}

	@FXML
	private void doSupprimerCompte() throws RowNotFoundOrTooManyRowsException, DatabaseConnexionException, DataAccessException {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);
			this.cmDialogController.supprimerCompte(cpt);
			this.loadList();
		}
		this.validateComponentState();
	}

	@FXML
	private void doNouveauCompte() {
		CompteCourant compte;
		compte = this.cmDialogController.creerNouveauCompte();
		if (compte != null) {
			this.oListCompteCourant.add(compte);
		}
	}

	@FXML
	private void doReleve() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.oListCompteCourant.get(selectedIndice);


			Pair<String, String> result = AlertUtilities.showMonthRequestDialog();

			if (result == null) {
				return;
			}

			if (result.getKey().isEmpty() || result.getValue().isEmpty()) {
				AlertUtilities.showAlert(primaryStage,"Erreur", "Les champs ne peuvent pas être vides", "Veuillez remplir les champs", Alert.AlertType.ERROR);
				doReleve();
				return;
			}
			if (!result.getValue().matches("[0-9]{4}")) {
				AlertUtilities.showAlert(primaryStage,"Erreur", "Le champ année doit être un nombre à 4 chiffres", "Veuillez remplir le champ année avec un nombre à 4 chiffres", Alert.AlertType.ERROR);
				doReleve();
				return;
			}
			if (!result.getKey().matches("[0-9]{2}")) {
				AlertUtilities.showAlert(primaryStage,"Erreur", "Le champ mois doit être un nombre à 2 chiffres", "Veuillez remplir le champ mois avec un nombre à 2 chiffres", Alert.AlertType.ERROR);
				doReleve();
				return;
			}
			if (Integer.parseInt(result.getKey())>12 || Integer.parseInt(result.getKey())<1){
				AlertUtilities.showAlert(primaryStage,"Erreur", "Le champ mois doit être compris entre 1 et 12", "Veuillez remplir le champ mois avec un nombre compris entre 1 et 12", Alert.AlertType.ERROR);
				doReleve();
				return;
			}

			LocalDate dateActuelle = LocalDate.now();
			LocalDate dateReleve = LocalDate.of(Integer.parseInt(result.getValue()), Integer.parseInt(result.getKey()), 1);

			if (dateReleve.isAfter(dateActuelle)) {
				AlertUtilities.showAlert(primaryStage,"Erreur", "La date du relevé ne peut pas être dans le futur", "Veuillez remplir les champs avec une date qui ne dépasse pas la date actuelle", Alert.AlertType.ERROR);
				return;
			}

			Month month = Month.of(Integer.parseInt(result.getKey()));
			Year year = Year.parse(result.getValue());

			this.cmDialogController.genererReleve(cpt, month, year);

		}
	}

	private void loadList() {
		ArrayList<CompteCourant> listeCpt;
		listeCpt = this.cmDialogController.getComptesDunClient();
		this.oListCompteCourant.clear();
		this.oListCompteCourant.addAll(listeCpt);
	}

	private void validateComponentState() {
		// Non implémenté => désactivé
		this.btnModifierCompte.setDisable(true);

		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.btnSupprCompte.setDisable(false);
			this.btnVoirOpes.setDisable(false);
		} else {
			this.btnVoirOpes.setDisable(true);
			this.btnSupprCompte.setDisable(true);
		}
	}


}
