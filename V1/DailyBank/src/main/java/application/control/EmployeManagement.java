package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.EmployeManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Employe;
import model.orm.Access_BD_Employe;

import java.util.ArrayList;

public class EmployeManagement {

    private Stage primaryStage;

    private DailyBankState dailyBankState;

    private EmployeManagementController emcViewController;

    public EmployeManagement(Stage _parentStage, DailyBankState _dbstate) {
        this.dailyBankState = _dbstate;
        try{
            FXMLLoader loader = new FXMLLoader(EmployeManagementController.class.getResource("employemanagement.fxml"));
            BorderPane root = loader.load();

            Scene scene = new Scene(root, root.getPrefWidth() + 50, root.getPrefHeight() + 10);
            scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

            this.primaryStage = new Stage();
            this.primaryStage.initModality(Modality.WINDOW_MODAL);
            this.primaryStage.initOwner(_parentStage);
            StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
            this.primaryStage.setScene(scene);
            this.primaryStage.setTitle("Gestion des employés");
            this.primaryStage.setResizable(false);

            this.emcViewController = loader.getController();
            this.emcViewController.initContext(this.primaryStage, this, _dbstate);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Employe modifierEmploye(){
        System.out.println("Je doit modifier l’employé");
        return null;
    }

    public Employe supprimerEmploye(){
        System.out.println("Je doit supprimer l’employé");
        return null;
    }

    public Employe creerEmploye(){
        System.out.println("Je doit créer l’employé");
        return null;
    }

    public void doEmployeManagementDialog() {
        this.emcViewController.displayDialog();
    }

    public ArrayList<Employe> getlisteEmploye(){
        ArrayList<Employe> listeEmploye = new ArrayList<Employe>();
        try {
            Access_BD_Employe ac = new Access_BD_Employe();
            listeEmploye = ac.getEmployeList();
            ArrayList<Employe> listeEmploye2 = new ArrayList<Employe>();
            for (int i = 0; i < listeEmploye.size(); i++) {
                if (listeEmploye.get(i).idAg == dailyBankState.getEmployeActuel().idAg) {
                    listeEmploye2.add(listeEmploye.get(i));
                }
            }
            listeEmploye = listeEmploye2;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return listeEmploye;
    }



}
