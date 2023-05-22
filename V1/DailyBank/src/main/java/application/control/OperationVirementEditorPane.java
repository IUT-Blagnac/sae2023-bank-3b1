package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.StageManagement;
import application.view.OperationEditorPaneController;
import application.view.OperationVirementEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.CompteCourant;
import model.data.Operation;

public class OperationVirementEditorPane {

	private Stage primaryStage;
	private OperationVirementEditorPaneController oepcViewController;

	public OperationVirementEditorPane(Stage _parentStage, DailyBankState _dbstate) {
		System.out.println("hello");
		try {
			FXMLLoader loader = new FXMLLoader(
					OperationVirementEditorPaneController.class.getResource("operationvirementeditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 500 + 20, 250 + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Enregistrement d'une opération");
			this.primaryStage.setResizable(false);

			this.oepcViewController = loader.getController();
			this.oepcViewController.initContext(this.primaryStage, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lance le dialogue d'opération de virement.
	 * 
	 * @param cpte      Le compte qui effectue le virement.
	 * @param cpteCible Le compte qui reçoit le virement.
	 * @param om        L'objet responsable de l'opération.
	 * @return Une ArrayList qui contient les 2 objets Operation effectués (Crédit &
	 *         Débit).
	 * 
	 * @author Vincent
	 */

	public ArrayList<Operation> doOperationEditorDialog(CompteCourant cpte, CompteCourant cpteCible,
			OperationsManagement om) {
		ArrayList<Operation> o = this.oepcViewController.displayDialog(cpte, cpteCible, om);
		return o;
	}
}
