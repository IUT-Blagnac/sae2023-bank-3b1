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
import model.data.Client;
import model.data.Employe;

import java.util.ArrayList;

public class EmployeManagementController {
    private Stage primaryStage;
    private EmployeManagement cmDialogController;
    private ObservableList<Employe> olvEmployes;

    public void initContext(Stage _containingStage, EmployeManagement _cm, DailyBankState _dbstate) {
        this.cmDialogController = _cm;
        this.primaryStage = _containingStage;
        this.configure();
    }

    private void configure() {
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
        this.olvEmployes = FXCollections.observableArrayList();
        this.lvEmployes.setItems(this.olvEmployes);
        this.lvEmployes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.lvEmployes.getFocusModel().focus(-1);
        this.lvEmployes.getSelectionModel().selectedItemProperty().addListener(e -> validateComponentState());
        this.validateComponentState();
    }

    public void displayDialog() {
        this.primaryStage.showAndWait();
    }

    private void closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
    }

    @FXML
    private ListView<Employe> lvEmployes;
    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private TextField txtNum;
    @FXML
    private Button btnModifierEmploye;

    @FXML
    private void doCancel() {
        this.primaryStage.close();
    }

    @FXML
    private void doRechercher() {
        int numEmploye;
        try {
            String nc = this.txtNum.getText();

            if (nc.equals("")) {
                numEmploye = -1;
            } else {
                numEmploye = Integer.parseInt(nc);
                if (numEmploye < 0) {
                    this.txtNum.setText("");
                    numEmploye = -1;
                }
            }
        } catch (NumberFormatException e) {
            this.txtNum.setText("");
            numEmploye = -1;
        }

        String debutNom = this.txtNom.getText();
        String debutPrenom = this.txtPrenom.getText();

        if (numEmploye !=-1){
            this.txtNom.setText("");
            this.txtPrenom.setText("");
        } else {
            if (debutNom.equals("") && !debutPrenom.equals("")) {
                this.txtPrenom.setText("");
            }
        }

        ArrayList<Employe> listeEmploye = new ArrayList<>();
        listeEmploye = cmDialogController.getlisteEmploye(numEmploye, txtNom.getText(), txtPrenom.getText());

        lvEmployes.getItems().clear();
        lvEmployes.getItems().addAll(listeEmploye);
    }

    @FXML
    private void doCreerEmploye() {
        Employe employe;
        employe = this.cmDialogController.creerEmploye();
        if (employe != null) {
            this.olvEmployes.add(employe);
        }
    }

    @FXML
    public void doModifierEmploye() {
        this.cmDialogController.modifierEmploye();
    }

    @FXML
    public void doSupprimerEmplyé() {
        this.cmDialogController.supprimerEmploye();
    }


    private void validateComponentState() {

        int selectedIndice = this.lvEmployes.getSelectionModel().getSelectedIndex();
        if (selectedIndice >= 0) {
            this.btnModifierEmploye.setDisable(false);
        } else {
            this.btnModifierEmploye.setDisable(true);
        }
    }

}
