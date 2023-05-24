package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.EmployeEditorPaneController;
import application.view.EmployeManagementController;
import application.view.PrelevementEditorPaneController;
import application.view.PrelevementManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.CompteCourant;
import model.data.Employe;
import model.data.Prelevement;

/**
 * Classe PrelevementEditorPane, gère l'affichage de la fenêtre de gestion des prélèvements
 * @author Vincent Barette
 */
public class PrelevementEditorPane {
    private  Stage primaryStage;
    private PrelevementEditorPaneController pepcViewController;
    private DailyBankState dailyBankState;


    /**
     * Constructeur de la classe PrelevementEditorPane
     * @param primaryStage La fenêtre principale de l'application
     * @param dailyBankState L'état de l'application
     * @autor Vincent Barette
     */
    public PrelevementEditorPane(Stage primaryStage, DailyBankState dailyBankState) {
        this.dailyBankState = dailyBankState;
        try {
            FXMLLoader loader = new FXMLLoader(PrelevementManagementController.class.getResource("prelevementeditorpane.fxml"));
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

            this.pepcViewController = loader.getController();
            this.pepcViewController.initContext(this.primaryStage, this.dailyBankState);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche la fenêtre de gestion des prélèvements
     * @param p Le prélèvement à afficher
     * @param editionMode Le mode d'édition de la fenêtre
     * @param idCompte L'identifiant du compte courant
     * @return Le prélèvement
     * @autor Vincent Barette
     */
    public Prelevement doPrelevementEditorDialog(Prelevement p, EditionMode editionMode, String idCompte) {
        return this.pepcViewController.displayDialog(p, editionMode, idCompte);
    }
}
