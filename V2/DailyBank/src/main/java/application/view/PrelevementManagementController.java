package application.view;

import application.DailyBankState;
import application.control.EmployeManagement;
import application.control.PrelevementManagementPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;
import model.data.Prelevement;

import java.util.ArrayList;

/**
 * Classe controlleur du menu de gestion des prélèvements
 * @author Vincent Barette
 */
public class PrelevementManagementController {
    private Stage primaryStage;
    private PrelevementManagementPane pmDialogController;
    private ObservableList<Prelevement> olvPrelevements;

    /**
     * Méthode qui initialise le contexte de l’objet
     * @param _containingStage La fenetre qui contient ce menu
     * @param _pm le controlleur des prélèvements
     * @param _dbstate l’état de l’application
     * @author Vincent Barette
     */
    public void initContext(Stage _containingStage, PrelevementManagementPane _pm, DailyBankState _dbstate) {
        this.pmDialogController = _pm;
        this.primaryStage = _containingStage;
        this.configure();
    }

    /**
     * Méthode qui permet de configurer la fenetre
     * @author Vincent BARETTE
     */
    private void configure() {
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
        this.olvPrelevements = FXCollections.observableArrayList();
        this.lvPrelevements.setItems(this.olvPrelevements);
        this.lvPrelevements.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.lvPrelevements.getFocusModel().focus(-1);
        this.lvPrelevements.getSelectionModel().selectedItemProperty().addListener(e -> validateComponentState());
        this.validateComponentState();
    }

    /**
     * Méthode qui affiche le menu
     * @author Vincent BARETTE
     */
    public void displayDialog(String idCompte) {
    	this.txtIDCompte.setText(idCompte);
        this.primaryStage.showAndWait();
    }

    /**
     * Méthode qui ferme la fenêtre
     * @param e L’évenement qui déclenche la fermeture de la fenêtre
     */
    private void closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
    }

    // Les différentes parties de l’interface dont nous avons besoin
    @FXML
    private ListView<Prelevement> lvPrelevements;
    @FXML
    private TextField txtIDPrelev;
    @FXML
    private TextField txtIDCompte;
    @FXML
    private Button btnModifier;
    @FXML
    private Button btnSupprimer;

    /**
     * Méthode appelée lorsque l’on appuie sur le bouton "Retour gestion Agence"
     * @author Vincent Barette
     */
    @FXML
    private void doCancel() {
        this.primaryStage.close();
    }

    /**
     * Déclenche la recherche des prélèvements
     * @author Vincent Barette
     */
    @FXML
    private void doRechercher() {
        int numPrelev;
        try {
            String np = this.txtIDPrelev.getText();

            if (np.equals("")) {
                numPrelev = -1;
            } else {
                numPrelev = Integer.parseInt(np);
                if (numPrelev < 0) {
                    this.txtIDPrelev.setText("");
                    numPrelev = -1;
                }
            }
        } catch (NumberFormatException e) {
            this.txtIDPrelev.setText("");
            numPrelev = -1;
        }

        ArrayList<Prelevement> listePrelevements = new ArrayList<>();
        int idCompte = Integer.valueOf(this.txtIDCompte.getText());
        listePrelevements = pmDialogController.getlistePrelevement(numPrelev, idCompte);

        this.lvPrelevements.getItems().clear();
        this.lvPrelevements.getItems().addAll(listePrelevements);
    }

    /**
     * Déclenche la création d’un prélèvement
     * @author Vincent Barette
     */
    @FXML
    private void doCreerPrelevement() {
        Prelevement pre;
        pre = this.pmDialogController.creerPrelevement();
        if (pre != null) {
            this.olvPrelevements.add(pre);
        }
    }

    /**
     * Déclenche la modification d’un prélèvement
     * @author Vincent Barette
     */
    @FXML
    public void doModifierPrelevement() {
        int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0){
            Prelevement prelevMod= this.olvPrelevements.get(selectedIndice);
            Prelevement resultat = this.pmDialogController.modifierPrelevement(prelevMod);
            if (resultat != null) {
                this.olvPrelevements.set(selectedIndice, resultat);
            }
        }
    }

    /**
     * Déclenche la supprésion d’un prélèvement
     * @author Vincent Barette
     */
    @FXML
    public void doSupprimerPrelevement() {
        int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0){
        	Prelevement prelevementSup= this.olvPrelevements.get(selectedIndice);
            this.pmDialogController.supprimerPrelevement(prelevementSup);
            this.olvPrelevements.remove(selectedIndice);
        }
    }

    /**
     * Cette méthode est appelée pour faire en sorte que les boutons supprimer et activer ne soit actifs que lorsqu'un
     * employé est sélectionné
     * @author Vincent Barette
     */
    private void validateComponentState() {

        int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            this.btnModifier.setDisable(false);
            this.btnSupprimer.setDisable(false);
        } else {
            this.btnModifier.setDisable(true);
            this.btnSupprimer.setDisable(true);
        }
    }

}
