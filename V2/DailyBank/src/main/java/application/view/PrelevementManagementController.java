package application.view;

import application.DailyBankState;
import application.control.EmployeManagement;
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
 * Classe controlleur du menu de gestion des Employé
 * @author Émilien FIEU
 */
public class PrelevementManagementController {
    private Stage primaryStage;
    private EmployeManagement cmDialogController;
    private ObservableList<Employe> olvEmployes;

    /**
     * Méthode qui initialise le contexte de l’objet
     * @param _containingStage La fenetre qui contient ce menu
     * @param _cm le controlleur des employé
     * @param _dbstate l’état de l’application
     * @author Émilien FIEU
     */
    public void initContext(Stage _containingStage, EmployeManagement _cm, DailyBankState _dbstate) {
        this.cmDialogController = _cm;
        this.primaryStage = _containingStage;
        this.configure();
    }

    /**
     * Méthode qui permet de configurer la fenetre
     * @author Émilien FIEU
     */
    private void configure() {
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
        this.olvEmployes = FXCollections.observableArrayList();
        this.lvEmployes.setItems(this.olvEmployes);
        this.lvEmployes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.lvEmployes.getFocusModel().focus(-1);
        this.lvEmployes.getSelectionModel().selectedItemProperty().addListener(e -> validateComponentState());
        this.validateComponentState();
    }

    /**
     * Méthode qui affiche le menu
     * @author Émilien FIEU
     */
    public void displayDialog() {
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
    private ListView<Employe> lvEmployes;
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
     * @author Émilien FIEU
     */
    @FXML
    private void doCancel() {
        this.primaryStage.close();
    }

    /**
     * Déclenche la recherche des employés
     * @author Émilien FIEU
     */
    @FXML
    private void doRechercher() {
        int numPrelev;
        try {
            String nc = this.txtIDPrelev.getText();

            if (nc.equals("")) {
                numPrelev = -1;
            } else {
                numPrelev = Integer.parseInt(nc);
                if (numPrelev < 0) {
                    this.txtIDPrelev.setText("");
                    numPrelev = -1;
                }
            }
        } catch (NumberFormatException e) {
            this.txtIDPrelev.setText("");
            numPrelev = -1;
        }

        String debutNom = this.txtIDCompte.getText();

        if (numPrelev !=-1){
            this.txtIDCompte.setText("");
        } else {
            if (debutNom.equals("") && !debutPrenom.equals("")) {
                this.txtIDCompte.setText("");
            }
        }

        ArrayList<Prelevement> listeEmploye = new ArrayList<>();
        listeEmploye = cmDialogController.getlisteEmploye(numPrelev, txtIDCompte.getText());

        lvEmployes.getItems().clear();
        lvEmployes.getItems().addAll(listeEmploye);
    }

    /**
     * Déclenche la création d’un employé
     * @author Émilien FIEU
     */
    @FXML
    private void doCreerEmploye() {
        Employe employe;
        employe = this.cmDialogController.creerEmploye();
        if (employe != null) {
            this.olvEmployes.add(employe);
        }
    }

    /**
     * Déclenche la modification d’un employé
     * @author Émilien FIEU
     */
    @FXML
    public void doModifierEmploye() {
        int selectedIndice = this.lvEmployes.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0){
            Employe employeMod= this.olvEmployes.get(selectedIndice);
            Employe resultat = this.cmDialogController.modifierEmploye(employeMod);
            if (resultat != null) {
                this.olvEmployes.set(selectedIndice, resultat);
            }
        }
    }

    /**
     * Déclenche la supprésion d’un employé
     * @author Émilien FIEU
     */
    @FXML
    public void doSupprimerEmplyé() {
        int selectedIndice = this.lvEmployes.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0){
            Employe employeSup= this.olvEmployes.get(selectedIndice);
            this.cmDialogController.supprimerEmploye(employeSup);
            this.olvEmployes.remove(selectedIndice);
        }
    }

    /**
     * Cette méthode est appelée pour faire en sorte que les boutons supprimer et activer ne soit actifs que lorsqu'un
     * employé est sélectionné
     * @author Émilien FIEU
     */
    private void validateComponentState() {

        int selectedIndice = this.lvEmployes.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            this.btnModifierEmploye.setDisable(false);
            this.btnSupprimerEmploye.setDisable(false);
        } else {
            this.btnModifierEmploye.setDisable(true);
            this.btnSupprimerEmploye.setDisable(true);
        }
    }

}
