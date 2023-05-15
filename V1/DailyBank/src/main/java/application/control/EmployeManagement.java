package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.EmployeManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.Employe;
import model.orm.Access_BD_Client;
import model.orm.Access_BD_Employe;
import model.orm.exception.ApplicationException;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.RowNotFoundOrTooManyRowsException;

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

    public Employe modifierEmploye(Employe employe){
        EmployeEditorPane eep = new EmployeEditorPane(this.primaryStage, this.dailyBankState);
        Employe result = eep.doEmployeEditorDialog(employe, EditionMode.MODIFICATION);
        if (result != null){
            try {
                Access_BD_Employe ac = new Access_BD_Employe();
                ac.updateEmploye(result);
            } catch (DatabaseConnexionException e){
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
                ed.doExceptionDialog();
                this.primaryStage.close();
                result = null;
            } catch (ApplicationException ae){
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
                ed.doExceptionDialog();
                result = null;
            }
        }
        return result;
    }

    public void supprimerEmploye(Employe employe){
        boolean confirmation = AlertUtilities.confirmYesCancel(this.primaryStage, "Suppression d'un employé", "Voulez-vous vraiment supprimer l'employé ", employe.toString(), Alert.AlertType.CONFIRMATION);

        if (confirmation){
            try {
                Access_BD_Employe ac = new Access_BD_Employe();
                ac.supprimerEmploye(employe);
            } catch (DatabaseConnexionException e){
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
                ed.doExceptionDialog();
                this.primaryStage.close();
            } catch (ApplicationException ae){
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
                ed.doExceptionDialog();
            }
        }

    }

    public Employe creerEmploye(){
        Employe employe;
        EmployeEditorPane eep = new EmployeEditorPane(this.primaryStage, this.dailyBankState);
        employe = eep.doEmployeEditorDialog(null, EditionMode.CREATION);

        if (employe != null) {
            try {
                Access_BD_Employe ac = new Access_BD_Employe();

                ac.insertEmploye(employe);
            } catch (DatabaseConnexionException e) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
                ed.doExceptionDialog();
                this.primaryStage.close();
                employe = null;
            } catch (ApplicationException ae) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
                ed.doExceptionDialog();
                employe = null;
            }
        }
        return employe;
    }

    public void doEmployeManagementDialog() {
        this.emcViewController.displayDialog();
    }

    public ArrayList<Employe> getlisteEmploye(int num, String nom, String prenom){
        ArrayList<Employe> listeEmploye = new ArrayList<Employe>();
        try {
            Access_BD_Employe ac = new Access_BD_Employe();
            listeEmploye = ac.getEmployeList(this.dailyBankState.getEmployeActuel().idAg, num, nom, prenom);
        } catch (DatabaseConnexionException e) {
            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
            ed.doExceptionDialog();
            this.primaryStage.close();
            listeEmploye = new ArrayList<>();
        } catch (ApplicationException e){
            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
            ed.doExceptionDialog();
            listeEmploye = new ArrayList<>();
        }


        return listeEmploye;
    }



}
