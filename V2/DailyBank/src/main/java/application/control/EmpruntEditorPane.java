package application.control;

import application.DailyBankState;
import application.tools.StageManagement;
import application.view.ClientsManagementController;
import application.view.EmpruntEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EmpruntEditorPane {
    private Stage primaryStage;
    private EmpruntEditorPaneController empruntEditorPaneController;

    public EmpruntEditorPane(Stage primaryStage, DailyBankState _dbstate) {
        try {
            FXMLLoader loader = new FXMLLoader(ClientsManagementController.class.getResource("EmpruntManagement.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

            this.primaryStage = primaryStage;
            this.primaryStage.initModality(Modality.WINDOW_MODAL);
            this.primaryStage.initOwner(primaryStage);
            StageManagement.manageCenteringStage(this.primaryStage, primaryStage);
            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("Gestion des emprunts");
            this.primaryStage.setResizable(false);

            this.empruntEditorPaneController = loader.getController();
            this.empruntEditorPaneController.initContext(this.primaryStage, _dbstate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
