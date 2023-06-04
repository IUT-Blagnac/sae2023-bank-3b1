/**
 * Classe EmpruntEditorPaneController.java
 * @author Tanguy Picuira
 */

package application.view;

import java.net.URL;
import java.util.ResourceBundle;
import application.DailyBankState;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.lang.Math;


public class EmpruntEditorPaneController implements Initializable {

        // Etat application
        @SuppressWarnings("unused")
        private DailyBankState dbs;

        // Fenêtre physique
        private Stage primaryStage;

        // Données de la fenêtre

        /**
         * Initialisation du contrôleur de vue EmpruntEditorPaneController.
         * @param _primaryStage
         * @param _dbstate
         */
        public void initContext(Stage _primaryStage, DailyBankState _dbstate) {
                this.primaryStage = _primaryStage;
                this.dbs = _dbstate;
                this.configure();
        }

        /**
         * Configuration de la fenêtre.
         */
        private void configure() {
                this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
        }

        /**
         * Affichage de la fenêtre.
         */
        public void displayDialog() {
                this.butOk.setText("Simulation emprunt");
                this.butAss.setText("Simulation assurance");
                this.butCancel.setText("Annuler");
                this.primaryStage.showAndWait();
        }


        /**
         * Vérifie si le texte est un nombre.
         * @param e
         * @return rien
         */
        private Object closeWindow(WindowEvent e) {
                this.doCancel();
                e.consume();
                return null;
        }

        // Attributs de la scene + actions
        @FXML
        private TextField montant;

        @FXML
        private TextField annee;

        @FXML
        private TextField TA;

        @FXML
        private TextArea txt;

        @FXML
        private TextField montantAss;

        @FXML
        private TextField TauxAnnuel;

        @FXML
        private TextField DureeMois;


        @FXML
        private Button butAss;
        @FXML
        private Button butOk;
        @FXML
        private Button butCancel;

        @Override
        public void initialize(URL location, ResourceBundle resources) {
        }

        @FXML
        private void doCancel() {
                this.primaryStage.close();
        }

        /**
         * Réalise la simulation d'un emprunt.
         * @author Tanguy Picuira
         */
        @FXML
        private void doSimul() {

                txt.setText("");
                String aff = "";

                if (!montant.getText().isEmpty() && isNumber(this.montant) && !annee.getText().isEmpty() && isNumber(annee) && !TA.getText().isEmpty() && isFloat(TA)) {
                        double numTA= Float.parseFloat(TA.getText());
                        double numA= Integer.parseInt(annee.getText());
                        double numMontant= Integer.parseInt(montant.getText());

                        aff ="Année \t| Capital restant dû       \t|Intérêts \t| Amortissement du capital \t| Annuité\n";

                        double Capital=numMontant;
                        double interet= (Capital/100)*numTA;
                        double annuite = numMontant*(((double) numTA /100)/(1-Math.pow((1+((double) numTA /100)),(-numA))));
                        double amor = annuite-interet;

                        double totI = 0;
                        double totC = 0;
                        double totA = 0;
                        for (int i =0 ; i<numA; i++) {
                                int bona =i+1;
                                totI += interet;
                                totC += amor;
                                totA = annuite;
                                aff = aff + ""+ bona +"         \t| " + Capital + "               \t| " + interet + "      \t| " + amor +" \t| " + annuite + "\n";

                                Capital = Capital-amor;
                                interet = (Capital/100)*numTA;
                                amor = annuite-interet;

                        }
                        txt.setText(aff);
                }
        }

        /**
         * Réalise de la simulation d'une assurance emprunt.
         * @author Tanguy Picuira
         */
        @FXML
        private void doAss() {

                txt.setText("");

                String aff = "";

                if (!montantAss.getText().isEmpty() && isNumber(montantAss) && !TauxAnnuel.getText().isEmpty() && isFloat(TauxAnnuel) && !DureeMois.getText().isEmpty() && isNumber(DureeMois)) {

                        float numTA= Float.parseFloat(TauxAnnuel.getText());
                        int numA= Integer.parseInt(DureeMois.getText());
                        int numMontant= Integer.parseInt(montantAss.getText());
                        float Tapl = numTA/100/12;
                        float tour = numA;
                        numA = numA - numA - numA;

                        aff ="Num mois \t| Capital restant dû en début de période \t| Intérêts \t| Montant des intérêts \t| Montant du principal  \t|  Montant à rembourser (Mensualité) \t| Capital restant du en fin de période \n";

                        double CapDeb=numMontant;
                        double interet=CapDeb*Tapl;

                        double MontantArembourser = numMontant * (Tapl/ (1-(Math.pow(1+Tapl, numA))));


                        double princ= MontantArembourser-interet;

                        double CapFin= CapDeb-princ;


                        for (int i =0 ; i<tour; i++) {
                                int bona =i+1;
                                aff = aff + ""+ bona +"         \t| " + CapDeb + "               \t| " + interet + "      \t| " + interet +" \t| " + princ + " \t| " + MontantArembourser + " \t| " + CapFin + "\n";



                                interet = CapDeb*Tapl;
                                CapFin = CapDeb -princ;
                                princ = MontantArembourser-interet;
                                CapDeb = CapFin;


                        }
                        txt.setText(aff);
                }

        }
        private boolean isNumber(TextField message) {
                try {
                        Integer.parseInt(message.getText());
                        return true;
                } catch(NumberFormatException e) {
                        return false;
                }
        }

        private boolean isFloat(TextField message) {
                try {
                        Float.parseFloat(message.getText());
                        return true;
                } catch(NumberFormatException e) {
                        return false;
                }
        }
}