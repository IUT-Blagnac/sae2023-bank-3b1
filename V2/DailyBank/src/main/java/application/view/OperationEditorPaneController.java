package application.view;

import java.util.Locale;

import application.DailyBankState;
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

/**
 * Controlleur de la fenêtre d'édition d'opération crédit & débit.
 */
public class OperationEditorPaneController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private CategorieOperation categorieOperation;
	private CompteCourant compteEdite;
	private Operation operationResultat;

	// Manipulation de la fenêtre

	/**
	 * Initialisaton du contexte de la fenêtre d'édition d'opération crédit & débit.
	 * @param _containingStage feneêtre physique contenant la scène
	 * @param _dbstate état courant de l'application
	 */
	public void initContext(Stage _containingStage, DailyBankState _dbstate) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.configure();
	}

	/**
	 * Configuration de la fenêtre d'édition d'opération crédit & débit.
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	/**
	 * Affiche la fenêtre d'édition d'opération crédit & débit.
	 * 
	 * @param cpte Le compte courant qui effectue l'opération.
	 * @param mode La catégorie de l'opération.
	 * @return L'opération effectuée.
	 */
	public Operation displayDialog(CompteCourant cpte, CategorieOperation mode) {
		this.categorieOperation = mode;
		this.compteEdite = cpte;

		String info;

		ObservableList<String> listTypesOpesPossibles;

		switch (mode) {
			case DEBIT:

				info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
						+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
						+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
				this.lblMessage.setText(info);

				this.btnOk.setText("Effectuer Débit");
				this.btnCancel.setText("Annuler Débit");

				listTypesOpesPossibles = FXCollections.observableArrayList();
				listTypesOpesPossibles.addAll(ConstantesIHM.OPERATIONS_DEBIT_GUICHET);

				this.cbTypeOpe.setItems(listTypesOpesPossibles);
				this.cbTypeOpe.getSelectionModel().select(0);
				break;

			case CREDIT:

				info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
						+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
						+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
				this.lblMessage.setText(info);

				this.btnOk.setText("Effectuer Crédit");
				this.btnCancel.setText("Annuler Crédit");

				listTypesOpesPossibles = FXCollections.observableArrayList();
				listTypesOpesPossibles.addAll(ConstantesIHM.OPERATIONS_CREDIT_GUICHET);

				this.cbTypeOpe.setItems(listTypesOpesPossibles);
				this.cbTypeOpe.getSelectionModel().select(0);
				break;
		}

		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dailyBankState.getEmployeActuel())) {
			// rien pour l'instant
		}

		this.operationResultat = null;
		this.cbTypeOpe.requestFocus();

		this.primaryStage.showAndWait();
		return this.operationResultat;
	}

	// Gestion du stage

	/**
	 * Fermeture de la fenêtre d'édition d'opération crédit & débit.
	 * @param e événement de fermeture
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
	private Label lblCible;
	@FXML
	private ComboBox<String> cbTypeOpe;
	@FXML
	private TextField txtMontant;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnCancel;

	/**
	 * Annule l'opération.
	 */
	@FXML
	private void doCancel() {
		this.operationResultat = null;
		this.primaryStage.close();
	}


	/**
	 * Effectue l'opération de crédit ou de débit.
	 *
	 * Débit exeptionnel : Émilien FIEU
	 * @author Émilien FIEU
	 */
	@FXML
	private void doAjouter() {
		switch (this.categorieOperation) {
			case DEBIT -> {
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

				if (this.compteEdite.solde - montant < this.compteEdite.debitAutorise ) {
					if (!ConstantesIHM.isAdmin(this.dailyBankState.getEmployeActuel())){
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
					else{
						boolean continuer = AlertUtilities.confirmYesCancel(this.primaryStage,"Confirmation débite exeptionnel",
								"Voulez vous vraiment débiter le compte de "+montant+"€ ?",
								"Compte : " + this.compteEdite.idNumCompte + " \nCe débit dépassera la limite de découvert autorisé !",
								AlertType.CONFIRMATION);

						if(!continuer) {

							return;
						}
					}
				}
				String typeOp = this.cbTypeOpe.getValue();
				this.operationResultat = new Operation(-1, montant, null, null, this.compteEdite.idNumCli, typeOp);
				this.primaryStage.close();
			}
			case CREDIT -> {
				// règles de validation d'un crédit :
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

				/*
				 * if (this.compteEdite.solde - montant < this.compteEdite.debitAutorise) {
				 * info = "Dépassement du découvert ! - Cpt. : " + this.compteEdite.idNumCompte
				 * + "  "
				 * + String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
				 * + String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
				 * this.lblMessage.setText(info);
				 * this.txtMontant.getStyleClass().add("borderred");
				 * this.lblMontant.getStyleClass().add("borderred");
				 * this.lblMessage.getStyleClass().add("borderred");
				 * this.txtMontant.requestFocus();
				 * return;
				 * }
				 */

				String typeOp = this.cbTypeOpe.getValue();
				this.operationResultat = new Operation(-1, montant, null, null, this.compteEdite.idNumCli, typeOp);
				this.primaryStage.close();
			}
		}
	}
}
