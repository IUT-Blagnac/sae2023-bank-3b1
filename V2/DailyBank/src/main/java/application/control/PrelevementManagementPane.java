package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.PrelevementManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Prelevement;
import model.orm.Access_BD_Prelevement;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

import java.util.ArrayList;

/**
 * Classe de gestion du menu d’édition de prélèvement
 * @author Vincent Barette
 */
public class PrelevementManagementPane {

    private Stage primaryStage;
    private DailyBankState dailyBankState;
    private PrelevementManagementController pmcViewController;
	private String idCompte;

    /**
     * Constructeur de la classe
     * @param _parentStage la fenetre dans lequel est le menu
     * @param _dbstate l’état de l’application
     * @author Vincent Barette
     */
    public PrelevementManagementPane(Stage _parentStage, DailyBankState _dbstate) {
        this.dailyBankState = _dbstate;
        try{
            FXMLLoader loader = new FXMLLoader(PrelevementManagementController.class.getResource("prelevementmanagement.fxml"));
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
     * Lance la modification du prélèvement
     * @param Prelevement à modifier
     * @return le prélèvement modifié
     * @author Vincent Barette
     */
    public Prelevement modifierPrelevement(Prelevement Prelevement){
        PrelevementEditorPane pep = new PrelevementEditorPane(this.primaryStage, this.dailyBankState);
        Prelevement result = pep.doPrelevementEditorDialog(Prelevement, EditionMode.MODIFICATION, this.idCompte);
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
     * Lance la suppression du prélèvement
     * @param Prelevement Prélèvement à modifier
     * @author Vincent Barette
     */
    public void supprimerPrelevement(Prelevement Prelevement){
        boolean confirmation = AlertUtilities.confirmYesCancel(this.primaryStage, "Suppression d'un prélèvement", "Voulez-vous vraiment supprimer ce prélèvement ", Prelevement.toString(), Alert.AlertType.CONFIRMATION);

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
     * Lance la création d’un prélèvement
     * @return une copie du prélèvement créé
     * @author Vincent Barette
     */
    public Prelevement creerPrelevement(){
        Prelevement Prelevement;
        PrelevementEditorPane eep = new PrelevementEditorPane(this.primaryStage, this.dailyBankState);
        Prelevement = eep.doPrelevementEditorDialog(null, EditionMode.CREATION, this.idCompte);

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
     * Lance le menu de gestion des prélèvements
     * @author Vincent BARETTE
     */
    public void doPrelevementManagementDialog(String idCompte) {
    	this.idCompte = idCompte;
        this.pmcViewController.displayDialog(idCompte);
    }

    /**
     * Cherche les prélèvements selon l'identifiant et le propriétaire du compte
     * @param idPrelev L'identifiant du prélèvement
     * @param idCompte L'identifiant du proprio
     * @return une liste des prélèvements trouvés
     * @author Vincent BARETTE
     */
    public ArrayList<Prelevement> getlistePrelevement(int idPrelev, int idCompte){
        ArrayList<Prelevement> listePrelevement = new ArrayList<Prelevement>();
        try {
            Access_BD_Prelevement ac = new Access_BD_Prelevement();
            listePrelevement = ac.getPrelevementList(idPrelev, idCompte);
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
