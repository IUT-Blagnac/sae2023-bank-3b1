/**
 * @author Tanguy Picuira
 * Classe EmpruntManagement
 */
package application.control;

import application.DailyBankState;
import application.tools.StageManagement;
import application.view.EmpruntEditorPaneController;
import application.view.EmpruntManagementController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;

import java.net.URL;
import java.util.ResourceBundle;

public class EmpruntManagement implements Initializable {

    private Stage primaryStage;
    private EmpruntEditorPaneController omcDialogController;

    /**
     * Constructeur de la classe EmpruntManagement.
     * @param primaryStage
     * @param dailyBankState
     * @author Tanguy Picuira
     */
    public EmpruntManagement(Stage primaryStage, DailyBankState dailyBankState) {
        try {
            FXMLLoader loader = new FXMLLoader(EmpruntManagementController.class.getResource("EmpruntManagement.fxml"));
            BorderPane root = loader.load();
            Scene scene = new Scene(root);

            this.primaryStage = new Stage();
            this.primaryStage.initModality(Modality.WINDOW_MODAL);
            this.primaryStage.initOwner(primaryStage);
            StageManagement.manageCenteringStage(this.primaryStage, primaryStage);
            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("Gestion des emprunts");
            this.primaryStage.setResizable(false);

            this.omcDialogController = loader.getController();
            this.omcDialogController.initContext(this.primaryStage, dailyBankState);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initContext(Stage primaryStage, OperationsManagement omDialogController, DailyBankState dailyBankState, Client clientDuCompte, CompteCourant compteConcerne) {

    }

    /**
     * Affiche la fenêtre de gestion des emprunts.
     * @author Tanguy Picuira
     */
    public void doSimulerEditorDialog() {
        this.omcDialogController.displayDialog();
    }
}
