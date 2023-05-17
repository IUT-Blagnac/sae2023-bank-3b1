package application.view;

import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;


/**
 * Controlleur du menu de modification/création d’employé
 * @author Émilien FIEU
 */
public class EmployeEditorPaneController {
    private DailyBankState dailyBankState;
    private Stage primaryStage;

    private Employe employeEdite;
    private EditionMode editionMode;
    private Employe employeResultat;

    /**
     * Initialise le contexte de l’objet
     * @param primaryStage la fenetre dans laquelle est le menue
     * @param dailyBankState l’état de l’application
     * @author Émilien FIEU
     */
    public void initContext(Stage primaryStage, DailyBankState dailyBankState) {
        this.primaryStage = primaryStage;
        this.dailyBankState = dailyBankState;
        this.configure();
    }

    /**
     * Configure le menu
     */
    private void configure() {
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
    }

    /**
     * Affiche le menu
     * @param employe l’employé à modifier (null si création d’un employé)
     * @param mode mode du menu : création ou modificatio
     * @return l’employé modifié / créé
     * @author Émilien FIEU
     */
    public Employe displayDialog(Employe employe, EditionMode mode) {
        this.editionMode = mode;
        if (employe == null) {
            this.employeEdite = new Employe(0, "", "", "", "", "", 0);
        } else {
            this.employeEdite = new Employe(employe);
        }

        this.employeResultat = null;
        switch (mode) {
        case CREATION:
            this.txtNum.setDisable(true);
            this.txtNom.setDisable(false);
            this.txtPrenom.setDisable(false);
            this.txtLogin.setDisable(false);
            this.txtMdp.setDisable(false);
            this.rbChefDAgence.setDisable(false);
            this.rbGuichetier.setDisable(false);

            this.lblMessage.setText("Informations sur le nouvel employé");
            this.butOk.setText("Créer");
            this.butCancel.setText("Annuler");
            break;
        case MODIFICATION:
            this.txtNum.setDisable(true);
            this.txtNom.setDisable(false);
            this.txtPrenom.setDisable(false);
            this.txtLogin.setDisable(false);
            this.txtMdp.setDisable(false);
            this.rbChefDAgence.setDisable(false);
            this.rbGuichetier.setDisable(false);

            this.lblMessage.setText("Modification des informations de l'employé");
            this.butOk.setText("Modifier");
            this.butCancel.setText("Annuler");
            break;
        }

        this.txtNum.setText("" + this.employeEdite.idEmploye);
        this.txtNom.setText(this.employeEdite.nom);
        this.txtPrenom.setText(this.employeEdite.prenom);
        this.txtLogin.setText(this.employeEdite.login);
        this.txtMdp.setText(this.employeEdite.motPasse);
        if (this.employeEdite.droitsAccess == "chefAgence") {
            this.rbChefDAgence.setSelected(true);
        } else {
            this.rbGuichetier.setSelected(true);
        }

        this.employeResultat = null;

        this.primaryStage.showAndWait();
        return this.employeResultat;
    }

    /**
     * Ferme la fenetre
     * @param e l’évenement qui a déclenché la fermeture de la fenetre
     * @author Émilien FIEU
     */
    private void closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
    }

    // Les différentes parties de l’interface dont nous avons besoin
    @FXML
    private Label lblMessage;
    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private TextField txtNum;
    @FXML
    private TextField txtLogin;
    @FXML
    private TextField txtMdp;
    @FXML
    private RadioButton rbChefDAgence;
    @FXML
    private RadioButton rbGuichetier;
    @FXML
    private Button butOk;
    @FXML
    private Button butCancel;

    /**
     * Annulation de l’opération
     * @author Émilien FIEU
     */
    @FXML
    private void doCancel() {
        this.employeResultat = null;
        this.primaryStage.close();
    }

    /**
     * Valide l’opération et ferme la fenetre
     * @author Émilien FIEU
     */
    @FXML
    private void doAjouter(){

        if (this.isSaisieValide()){
            this.employeResultat = this.employeEdite;
            employeEdite.idAg = this.dailyBankState.getEmployeActuel().idAg;
            this.primaryStage.close();
        }

    }

    /**
     * Vérifie si la saisie est valide
     * @return true si la saisie est valide, false sinon
     * @author Émilien FIEU
     */
    private boolean isSaisieValide() {
        this.employeEdite.nom = this.txtNom.getText().trim();
        this.employeEdite.prenom = this.txtPrenom.getText().trim();
        this.employeEdite.login = this.txtLogin.getText().trim();
        this.employeEdite.motPasse = this.txtMdp.getText().trim();
        this.employeEdite.droitsAccess = this.rbChefDAgence.isSelected() ? "chefAgence" : "guichetier";

        if (this.employeEdite.nom.isEmpty()) {
            AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le nom de l'employé est obligatoire", Alert.AlertType.ERROR);
            this.txtNom.requestFocus();
            return false;
        }

        if (this.employeEdite.prenom.isEmpty()) {
            AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le prénom de l'employé est obligatoire", Alert.AlertType.ERROR);
            this.txtPrenom.requestFocus();
            return false;
        }

        if (this.employeEdite.login.isEmpty()) {
            AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le login de l'employé est obligatoire", Alert.AlertType.ERROR);
            this.txtLogin.requestFocus();
            return false;
        }

        if (this.employeEdite.motPasse.isEmpty()) {
            AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le mot de passe de l'employé est obligatoire", Alert.AlertType.ERROR);
            this.txtMdp.requestFocus();
            return false;
        }

        return true;

    }

}
