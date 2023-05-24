package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.PrelevementManagementController;
import application.view.PrelevementManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Prelevement;
import model.data.Prelevement;
import model.orm.Access_BD_Prelevement;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

import java.util.ArrayList;

/**
 * Classe de gestion du menu d’édition d’employé
 * @author Émilien FIEU
 */
public class PrelevementManagement {

    private Stage primaryStage;
    private DailyBankState dailyBankState;
    private PrelevementManagementController pmcViewController;

    /**
     * Constructeur de la classe
     * @param _parentStage la fenetre dans lequel est le menu
     * @param _dbstate l’état de l’application
     * @author Émilien FIEU
     */
    public PrelevementManagement(Stage _parentStage, DailyBankState _dbstate) {
        this.dailyBankState = _dbstate;
        try{
            FXMLLoader loader = new FXMLLoader(PrelevementManagementController.class.getResource("Prelevementmanagement.fxml"));
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

            this.pmcViewController = loader.getController();
            this.pmcViewController.initContext(this.primaryStage, this, _dbstate);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Lance la modification de l’employé
     * @param Prelevement l’employé à modifier
     * @return l’employé modifié
     * @author Émilien FIEU
     */
    public Prelevement modifierPrelevement(Prelevement Prelevement){
        PrelevementEditorPane eep = new PrelevementEditorPane(this.primaryStage, this.dailyBankState);
        Prelevement result = eep.doPrelevementEditorDialog(Prelevement, EditionMode.MODIFICATION);
        if (result != null){
            try {
                Access_BD_Prelevement ac = new Access_BD_Prelevement();
                ac.updatePrelevement(result);
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

    /**
     * Lance la suppression de l’employé
     * @param Prelevement l’employé à modifier
     * @author Émilien FIEU
     */
    public void supprimerPrelevement(Prelevement Prelevement){
        boolean confirmation = AlertUtilities.confirmYesCancel(this.primaryStage, "Suppression d'un employé", "Voulez-vous vraiment supprimer l'employé ", Prelevement.toString(), Alert.AlertType.CONFIRMATION);

        if (confirmation){
            try {
                Access_BD_Prelevement ac = new Access_BD_Prelevement();
                ac.supprimerPrelevement(Prelevement);
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

    /**
     * Lance la création d’un employé
     * @return l’employé crée
     */
    public Prelevement creerPrelevement(){
        Prelevement Prelevement;
        PrelevementEditorPane eep = new PrelevementEditorPane(this.primaryStage, this.dailyBankState);
        Prelevement = eep.doPrelevementEditorDialog(null, EditionMode.CREATION);

        if (Prelevement != null) {
            try {
                Access_BD_Prelevement ac = new Access_BD_Prelevement();

                ac.insertPrelevement(Prelevement);
            } catch (DatabaseConnexionException e) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
                ed.doExceptionDialog();
                this.primaryStage.close();
                Prelevement = null;
            } catch (ApplicationException ae) {
                ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, ae);
                ed.doExceptionDialog();
                Prelevement = null;
            }
        }
        return Prelevement;
    }

    /**
     * Lance le menu de gestion des employés
     * @author Émilien FIEU
     */
    public void doPrelevementManagementDialog() {
        this.pmcViewController.displayDialog();
    }

    /**
     * Cherche les employés selon le numéro, le nom, le prénom
     * @param num le numéro de l’employé recherché
     * @param nom le nom ou le début de nom de l’employé recherché
     * @param prenom le prénom ou le début de prénom de l’employé recherché
     * @return una liste des employés trouvés
     * @author Vincent Barette
     */
    public ArrayList<Prelevement> getlistePrelevement(int num, String nom, String prenom){
        ArrayList<Prelevement> listePrelevement = new ArrayList<Prelevement>();
        try {
            Access_BD_Prelevement ac = new Access_BD_Prelevement();
            listePrelevement = ac.getPrelevementList(this.dailyBankState.getEmployeActuel().idAg, num, nom, prenom);
        } catch (DatabaseConnexionException e) {
            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
            ed.doExceptionDialog();
            this.primaryStage.close();
            listePrelevement = new ArrayList<>();
        } catch (ApplicationException e){
            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dailyBankState, e);
            ed.doExceptionDialog();
            listePrelevement = new ArrayList<>();
        }


        return listePrelevement;
    }
}
