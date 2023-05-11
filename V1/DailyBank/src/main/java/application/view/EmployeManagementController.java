package application.view;

import application.DailyBankState;
import application.control.EmployeManagement;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;

public class EmployeManagementController {
    private Stage primaryStage;
    private EmployeManagement cmDialogController;

    public void initContext(Stage _containingStage, EmployeManagement _cm, DailyBankState _dbstate) {
        this.cmDialogController = _cm;
        this.primaryStage = _containingStage;
        this.configure();
    }

    private void configure() {
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

    }

    private void closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
    }

    @FXML
    private ListView<Employe> lvEmployes;

    @FXML
    private void doCancel() {
        this.primaryStage.close();
    }

    @FXML
    private void doRechercher() {
        lvEmployes.getItems().clear();
        lvEmployes.getItems().addAll(cmDialogController.getlisteEmploye());
    }

    @FXML
    private void doCreerEmploye() {
        this.cmDialogController.creerEmploye();
    }

    @FXML
    public void doModifierEmploye() {
        this.cmDialogController.modifierEmploye();
    }

    @FXML
    public void doSupprimerEmplyé() {
        this.cmDialogController.supprimerEmploye();
    }

    public void displayDialog() {
        this.primaryStage.showAndWait();
    }



}
