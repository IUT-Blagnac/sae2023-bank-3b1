package application.view;

import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Prelevement;


/**
 * Controlleur du menu de modification/création de prélèvements
 * @author Vincent BARETTE
 */
public class PrelevementEditorPaneController {
    private DailyBankState dailyBankState;
    private Stage primaryStage;

    private Prelevement prelevementEdite;
    private EditionMode editionMode;
    private Prelevement prelevementResultat;

    /**
     * Initialise le contexte de l’objet
     * @param primaryStage la fenetre dans laquelle est le menue
     * @param dailyBankState l’état de l’application
     * @author Vincent BARETTE
     */
    public void initContext(Stage primaryStage, DailyBankState dailyBankState) {
        this.primaryStage = primaryStage;
        this.dailyBankState = dailyBankState;
        this.configure();
    }

    /**
     * Configure le menu pour se fermer
     */
    private void configure() {
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
    }

    /**
     * Affiche le menu
     * @param prelevement l’employé à modifier (null si création d’un prélèvement,
     * pas null si modification)
     * @param mode mode du menu : création ou modification
     * @return le prélèvement modifié / créé
     * 
     * @author Vincent BARETTE
     */
    public Prelevement displayDialog(Prelevement p, EditionMode mode, String idCompte) {
        this.editionMode = mode;
        if (p == null) {
            this.prelevementEdite = new Prelevement();
        } else {
            this.prelevementEdite = new Prelevement(p);
        }

        this.prelevementResultat = null;
        switch (mode) {
        case CREATION:
            this.txtIDCompte.setText(idCompte);
            System.out.println(this.txtIDCompte.getText());
            this.txtID.setDisable(true);
            this.txtMontant.setDisable(false);
            this.txtDateReccurente.setDisable(false);
            this.txtBeneficiaire.setDisable(false);
            this.txtIDCompte.setDisable(true);

            this.lblMessage.setText("Informations sur le nouveau prélèvement");
            this.butOk.setText("Créer");
            this.butCancel.setText("Annuler");
            break;
        case MODIFICATION:
            this.txtID.setDisable(true);
            this.txtMontant.setDisable(false);
            this.txtDateReccurente.setDisable(false);
            this.txtBeneficiaire.setDisable(false);
            this.txtIDCompte.setDisable(true);
            this.txtIDCompte.setText(this.prelevementEdite.idCompteProprio+"");

            this.lblMessage.setText("Modification des informations du prélèvement");
            this.butOk.setText("Modifier");
            this.butCancel.setText("Annuler");
            break;
		default:
			break;
        }

        this.txtID.setText("" + this.prelevementEdite.id);
        this.txtMontant.setText(""+this.prelevementEdite.montant);
        this.txtDateReccurente.setText(""+this.prelevementEdite.dateReccurente);
        this.txtBeneficiaire.setText(this.prelevementEdite.beneficiaire);

        this.prelevementResultat = null;

        this.primaryStage.showAndWait();
        return this.prelevementResultat;
    }

    /**
     * Ferme la fenetre
     * @param e l’évenement qui a déclenché la fermeture de la fenetre
     * @author Vincent Barette
     */
    private void closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
    }

    // Les différentes parties de l’interface dont nous avons besoin
    @FXML
    private Label lblMessage;
    @FXML
    private TextField txtID;
    @FXML
    private TextField txtMontant;
    @FXML
    private TextField txtDateReccurente;
    @FXML
    private TextField txtBeneficiaire;
    @FXML
    private TextField txtIDCompte;
    @FXML
    private Button butOk;
    @FXML
    private Button butCancel;

    /**
     * Annulation de l’opération
     * @author Vincent Barette
     */
    @FXML
    private void doCancel() {
        this.prelevementResultat = null;
        this.primaryStage.close();
    }

    /**
     * Valide l’opération et ferme la fenetre
     * @author Vincent Barette
     */
    @FXML
    private void doAjouter(){
        if (this.isSaisieValide()){
            this.prelevementResultat = this.prelevementEdite;
            this.primaryStage.close();
        }
    }

    /**
     * Vérifie si la saisie est valide et envoye une alerte sinon
     * Tous les champs sont obligatoires.
     * @return true si la saisie est valide, false sinon
     * @author Vincent Barette
     */
    private boolean isSaisieValide() {
        try {
        	Integer.valueOf(this.txtDateReccurente.getText());
        	Integer.valueOf(this.txtMontant.getText());
        } catch (Exception e) {
        	AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Les nombres ne sont pas des nombres\n- Philosophe Grec", Alert.AlertType.ERROR);
        	return false;
        	
        }
        
    	this.prelevementEdite.id = Integer.valueOf(this.txtID.getText());
    	this.prelevementEdite.montant = Integer.valueOf(this.txtMontant.getText());
    	this.prelevementEdite.dateReccurente = Integer.valueOf(this.txtDateReccurente.getText());
    	this.prelevementEdite.beneficiaire = this.txtBeneficiaire.getText();
    	this.prelevementEdite.idCompteProprio = Integer.valueOf(this.txtIDCompte.getText());

        if (this.txtID.getText().isEmpty() || this.txtBeneficiaire.getText().isEmpty()
        		|| this.txtMontant.getText().isEmpty() || this.txtDateReccurente.getText().isEmpty()) {
            AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Tous les champs sont mandatory", Alert.AlertType.ERROR);
            this.txtID.requestFocus();
            return false;
        }
        
        if (Integer.valueOf(this.txtDateReccurente.getText()) < 1 || Integer.valueOf(this.txtDateReccurente.getText()) > 31) {
            AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Ce jour ne semble pas exister", Alert.AlertType.ERROR);
            this.txtDateReccurente.requestFocus();
            return false;
        }
        
        if (Integer.valueOf(this.txtMontant.getText()) < 1) {
            AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le montant doit être positif", Alert.AlertType.ERROR);
            this.txtDateReccurente.requestFocus();
            return false;
        }

        return true;

    }

}
