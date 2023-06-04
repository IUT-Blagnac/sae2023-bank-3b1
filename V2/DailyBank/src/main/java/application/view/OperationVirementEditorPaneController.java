package application.view;

import java.util.ArrayList;
import java.util.Locale;

import application.DailyBankState;
import application.control.OperationsManagement;
import application.tools.AlertUtilities;
import application.tools.CategorieOperation;
import application.tools.ConstantesIHM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.Access_BD_CompteCourant;
import model.orm.Access_BD_Operation;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.RowNotFoundOrTooManyRowsException;

/**
 * Contrôleur de la fenêtre d'édition d'opération virement.
 */
public class OperationVirementEditorPaneController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private CategorieOperation categorieOperation;
	private CompteCourant compteEdite;
	private CompteCourant compteCible;
	private ArrayList<Operation> operationResultat;

	// Manipulation de la fenêtre

	/**
	 * Initialise le contexte de l'application.
	 * @param _containingStage Fenêtre physique ou est la scène
	 * @param _dbstate Etat courant de l'application
	 */
	public void initContext(Stage _containingStage, DailyBankState _dbstate) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.configure();
	}

	/**
	 * Configure la fenêtre.
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	/**
	 * Affiche la fenêtre d'édition d'opération virement.
	 * 
	 * @param cpte      Le compte courant qui effectue le virement.
	 * @param cpteCible Le compte qui reçoit le virement.
	 * @param om        Référence à l'objet responsable de l'opération.
	 * @return Arraylist des 2 opérations effectuées.
	 * 
	 * @author Vincent
	 */
	public ArrayList<Operation> displayDialog(CompteCourant cpte, CompteCourant cpteCible, OperationsManagement om) {
		this.compteEdite = cpte;

		String info;

		info = "Cpt. [" + this.compteEdite.idNumCompte + "] (" +
				this.compteEdite.solde + ") -> [?] (" +
				(this.compteCible == null ? "" : this.compteCible.solde) + ")";

		this.lblMessage.setText(info);

		this.btnOk.setText("Effectuer Virement");
		this.btnCancel.setText("Annuler Virement");

		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dailyBankState.getEmployeActuel())) {
			// rien pour l'instant
		}

		this.operationResultat = null;

		this.primaryStage.showAndWait();

		om.setCompteCible(this.compteCible);

		return this.operationResultat;
	}

	// Gestion du stage

	/**
	 * Ferme la fenêtre.
	 * @param e Evènement de fermeture
	 * @return null
	 */
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions

	@FXML
	private Label lblMessage;
	@FXML
	private Label lblMontant;
	@FXML
	private TextField txtCible;

	@FXML
	private TextField txtMontant;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnCancel;

	/**
	 * Action sur le bouton "Annuler".
	 */
	@FXML
	private void doCancel() {
		this.operationResultat = null;
		this.primaryStage.close();
	}

	/**
	 * Action sur le bouton "Valider".
	 */
	@FXML
	private void doAjouter() {
		// règles de validation d'un débit :
		// - le montant doit être un nombre valide
		// - et si l'utilisateur n'est pas chef d'agence,
		// - le débit ne doit pas amener le compte en dessous de son découvert autorisé
		double montant;

		this.txtMontant.getStyleClass().remove("borderred");
		this.lblMontant.getStyleClass().remove("borderred");
		this.lblMessage.getStyleClass().remove("borderred");
		String info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
				+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
				+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
		this.lblMessage.setText(info);

		try {
			montant = Double.parseDouble(this.txtMontant.getText().trim());
			if (montant <= 0)
				throw new NumberFormatException();
		} catch (NumberFormatException nfe) {
			this.txtMontant.getStyleClass().add("borderred");
			this.lblMontant.getStyleClass().add("borderred");
			this.txtMontant.requestFocus();
			return;
		}
		if (this.compteEdite.solde - montant < this.compteEdite.debitAutorise) {
			info = "Dépassement du découvert ! - Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);
			this.txtMontant.getStyleClass().add("borderred");
			this.lblMontant.getStyleClass().add("borderred");
			this.lblMessage.getStyleClass().add("borderred");
			this.txtMontant.requestFocus();
			return;
		}

		Access_BD_CompteCourant ac = new Access_BD_CompteCourant();
		try {
			this.compteCible = ac.getCompteCourant(Integer.parseInt(txtCible.getText()));
		} catch (RowNotFoundOrTooManyRowsException e) {
			throw new RuntimeException(e);
		} catch (DataAccessException e) {
			throw new RuntimeException(e);
		} catch (DatabaseConnexionException e) {
			throw new RuntimeException(e);
		}

		this.operationResultat = new ArrayList<Operation>();
		this.operationResultat.add(new Operation(-1, montant, null, null, this.compteEdite.idNumCli, "Débit Virement"));
		this.operationResultat
				.add(new Operation(-1, montant, null, null, this.compteCible.idNumCli, "Crédit Virement"));
		this.primaryStage.close();

	}
}
