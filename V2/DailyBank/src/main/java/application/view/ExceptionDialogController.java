package application.view;

import java.io.PrintWriter;
import java.io.StringWriter;

import application.DailyBankState;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.orm.exception.ApplicationException;

/**
 * Controlleur de la fenêtre d’exception
 */
public class ExceptionDialogController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	// Données de la fenêtre
	private ApplicationException aException;

	// Manipulation de la fenêtre

	/**
	 * Initialisation du contexte de la fenêtre.
	 * @param _containingStage Fenêtre physique ou est la scène
	 * @param _dbstate Etat courant de l'application
	 * @param _ae Exception à afficher
	 */
	public void initContext(Stage _containingStage, DailyBankState _dbstate, ApplicationException _ae) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.aException = _ae;
		this.configure();
	}

	/**
	 * Configuration de la fenêtre.
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
		this.lblTitre.setText(this.aException.getMessage());
		this.txtTable.setText(this.aException.getTableName().toString());
		this.txtOpe.setText(this.aException.getOrder().toString());
		this.txtException.setText(this.aException.getClass().getName());
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		this.aException.printStackTrace(pw);
		this.txtDetails.setText(sw.toString());
	}

	/**
	 * Affichage de la fenêtre.
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	// Gestion du stage

	/**
	 * Fermeture de la fenêtre.
	 * @param e Evènement de fermeture
	 * @return null
	 */
	private Object closeWindow(WindowEvent e) {
		return null;
	}

	// Attributs de la scene + actions

	@FXML
	private Label lblTitre;
	@FXML
	private TextField txtTable;
	@FXML
	private TextField txtOpe;
	@FXML
	private TextField txtException;
	@FXML
	private TextArea txtDetails;

	/**
	 * Action sur le bouton OK.
	 */
	@FXML
	private void doOK() {
		this.primaryStage.close();
	}
}
