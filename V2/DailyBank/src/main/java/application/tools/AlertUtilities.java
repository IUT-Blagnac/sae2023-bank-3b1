package application.tools;

import java.util.Optional;

import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * Utilitaire pour afficher une fenêtre de message ou de confirmation.
 *
 */

public class AlertUtilities {

	/**
	 * Affiche une message de confirmation d'un message avec boutons Oui/Non.
	 *
	 * @param _fen     Fenêtre (Stage) sur laquelle le dialogue se centre et est
	 *                 modal.
	 * @param _title   Titre du dialogue
	 * @param _message Message à confirmer
	 * @param _content Détail d'information
	 * @param _at      Type d'alerte (icône associé) (constante définie par
	 *                 AlertType)
	 * @return true si dialogue confirmé, false sinon
	 */
	public static boolean confirmYesCancel(Stage _fen, String _title, String _message, String _content, AlertType _at) {

		if (_at == null) {
			_at = AlertType.INFORMATION;
		}
		Alert alert = new Alert(_at);
		alert.initOwner(_fen);
		alert.setTitle(_title);
		if (_message == null || !_message.equals(""))
			alert.setHeaderText(_message);
		alert.setContentText(_content);

		Optional<ButtonType> option = alert.showAndWait();
		if (option.isPresent() && option.get() == ButtonType.OK) {
			return true;
		}
		return false;
	}

	/**
	 * Affiche une message simple avec bouton de fermeture.
	 *
	 * @param _fen     Fenêtre (Stage) sur laquelle le dialogue se centre et est
	 *                 modal.
	 * @param _title   Titre du dialogue
	 * @param _message Message à donner
	 * @param _content Détail d'information
	 * @param _at      Type d'alerte (icône associé) (constante définie par
	 *                 AlertType)
	 */
	public static void showAlert(Stage _fen, String _title, String _message, String _content, AlertType _at) {

		if (_at == null) {
			_at = AlertType.INFORMATION;
		}
		Alert alert = new Alert(_at);
		alert.initOwner(_fen);
		alert.setTitle(_title);
		if (_message == null || !_message.equals(""))
			alert.setHeaderText(_message);
		alert.setContentText(_content);

		alert.showAndWait();
	}

	public static Pair<String, String> showMonthRequestDialog() {
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Selection du mois et de l'année du relevé");
		dialog.setHeaderText(null);

		DialogPane dialogPane = dialog.getDialogPane();

		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		GridPane gridPane = new GridPane();
		gridPane.setHgap(10);
		gridPane.setVgap(10);

		TextField monthTextField = new TextField();
		monthTextField.setPromptText("Mois");
		TextField yearTextField = new TextField();
		yearTextField.setPromptText("Année");



		gridPane.add(new Label("Mois :"), 0, 0);
		gridPane.add(monthTextField, 1, 0);
		gridPane.add(new Label("Année :"), 0, 1);
		gridPane.add(yearTextField, 1, 1);

		dialogPane.setContent(gridPane);
		monthTextField.requestFocus();

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == ButtonType.OK) {
				return new Pair<>(monthTextField.getText(), yearTextField.getText());
			}
			return null;
		});

		dialog.showAndWait();

		Pair<String, String> result = dialog.getResult();
		if (result != null) {
			String month = result.getKey();
			String year = result.getValue();
			System.out.println("Mois: " + month);
			System.out.println("Année: " + year);
		}

		return result;
	}
}
