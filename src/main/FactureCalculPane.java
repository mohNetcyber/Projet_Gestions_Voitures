// FactureClaulPane.java
package main;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Contrat;

public class FactureCalculPane extends VBox {
    public TextField forfaitAgenceTextField;
    public TextField forfaitJourTextField;
    public TextField nbrJourTextField;
    public TextField montantJourTextField;
    public TextField kmTextField;
    public TextField kmTarifTextField;
    public TextField montantKmTextField;
    public TextField montantTotalTextField;
    public VBox v1 = new VBox(15);
    public VBox v2 = new VBox(15);
    public VBox v3 = new VBox(15);
    public VBox v4 = new VBox(15);
    public VBox v5 = new VBox(15);
    public VBox v6 = new VBox(15);
    public HBox hboxFacture = new HBox(15);

    public FactureCalculPane(Contrat contrat) {
        setSpacing(15);
        getStyleClass().add("facture-pane");
        
        HBox hboxMont = new HBox(15);
        Label titleMont = new Label("Montant");
        titleMont.getStyleClass().add("label-title-pane");
        hboxMont.getChildren().add(titleMont);
        
        Label forfaitagence = createStyledLabel("Forfait agence = ");
        Label forfaitJour = createStyledLabel("Forfait journalier = ");
        Label KmParcourus = createStyledLabel("Kilomètres parcourus : ");
        Label montantTotal = createStyledLabel("Montant total : ");
        v1.getChildren().addAll(forfaitagence, forfaitJour, KmParcourus, montantTotal);
        
        
        //HBox forfaitAgenceHbox = new HBox(15);
        forfaitAgenceTextField = new TextField();
        forfaitAgenceTextField = createStyledField();
        forfaitAgenceTextField.setPromptText("Ex: 40");
        //forfaitAgenceHbox.getChildren().addAll(new Label("Forfait agence = "), forfaitAgenceTextField);
        
        
        //HBox forfaitJourHbox = new HBox(15);
        forfaitJourTextField = new TextField(String.valueOf(contrat.getForfaitJournalier()));
        forfaitJourTextField = createStyledField();
        forfaitJourTextField.setEditable(false);
        nbrJourTextField = new TextField();
        nbrJourTextField = createStyledField();
        nbrJourTextField.setEditable(true);
        montantJourTextField = new TextField();
        montantJourTextField = createStyledField();
        montantJourTextField.setEditable(false);
        
        kmTextField = new TextField();
        kmTextField = createStyledField();
        kmTextField.setEditable(true);
        kmTarifTextField = new TextField(String.valueOf(contrat.getTarifKm()));
        kmTarifTextField = createStyledField();
        kmTarifTextField.setEditable(false);
        montantKmTextField = new TextField();
        montantKmTextField = createStyledField();
        montantKmTextField.setEditable(false);
        
        montantTotalTextField = new TextField();
        montantTotalTextField = createStyledField();
        montantTotalTextField.setEditable(false);
        Label empty1 = createStyledLabel("");
        Label empty3 = createStyledLabel("");
        Label empty2 = createStyledLabel("");
        Label empty = createStyledLabel("");
        v2.getChildren().addAll(forfaitAgenceTextField, forfaitJourTextField, kmTextField);
        v3.getChildren().addAll(empty1, new Label("dh * "), new Label("dh * "));
        v4.getChildren().addAll(empty2, nbrJourTextField, kmTarifTextField);
        v5.getChildren().addAll(empty3, new Label(" jours ="), new Label("Km = "));
        v6.getChildren().addAll(empty, montantJourTextField, montantKmTextField, montantTotalTextField);
        hboxFacture.getChildren().addAll(v1, v2, v3, v4, v5, v6);
        getChildren().addAll(hboxMont, hboxFacture);
    }
    
    public void update(Contrat contrat) {
        forfaitJourTextField.setText(String.valueOf(contrat.getForfaitJournalier()));
        kmTarifTextField.setText(String.valueOf(contrat.getTarifKm()));

        // Réinitialiser les champs calculés
        nbrJourTextField.clear();
        montantJourTextField.clear();
        kmTextField.clear();
        montantKmTextField.clear();
        montantTotalTextField.clear();
        forfaitAgenceTextField.clear();
    }
    
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("label-pane");
        return label;
    }
    
    private TextField createStyledField() {
        TextField tf = new TextField();
        tf.getStyleClass().add("pane-textfield");
        return tf;
    }

}
