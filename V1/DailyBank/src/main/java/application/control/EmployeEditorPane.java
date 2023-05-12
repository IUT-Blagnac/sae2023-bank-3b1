package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ClientEditorPaneController;
import application.view.ClientsManagementController;
import application.view.EmployeEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Employe;

public class EmployeEditorPane {
    private  Stage primaryStage;
    private EmployeEditorPaneController eepcViewController;
    private DailyBankState dailyBankState;

    public EmployeEditorPane(Stage primaryStage, DailyBankState dailyBankState) {
        this.dailyBankState = dailyBankState;
        try {
            FXMLLoader loader = new FXMLLoader(ClientsManagementController.class.getResource("employeeditorpane.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, root.getPrefWidth() + 20, root.getPrefHeight() + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.primaryStage = new Stage();
            this.primaryStage.initModality(Modality.WINDOW_MODAL);
            this.primaryStage.initOwner(primaryStage);
            StageManagement.manageCenteringStage(primaryStage, this.primaryStage);
            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("Gestion d'un client");
            this.primaryStage.setResizable(false);

            this.eepcViewController = loader.getController();
            this.eepcViewController.initContext(this.primaryStage, this.dailyBankState);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Employe doEmployeEditorDialog(Employe e, EditionMode editionMode) {
        return this.eepcViewController.displayDialog(e, editionMode);
    }
}
