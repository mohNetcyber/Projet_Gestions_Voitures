package main;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.Vehicule;

public class VehiculeInfoPane extends GridPane {
    private TextField immatriculeField;
    private TextField marqueField;
    private TextField typeField;
    private TextField categorieField;
    private TextField nbPlacesField;

    public VehiculeInfoPane(Vehicule vehicule) {
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(15));
        getStyleClass().add("vehicule-pane");

        Label title = new Label("Informations Véhicule");
        title.getStyleClass().add("label-title-pane");
        add(title, 0, 0, 2, 1);

        add(createStyledLabel("Immatricule : "), 0, 1);
        immatriculeField = createStyledField();
        add(immatriculeField, 1, 1);

        add(createStyledLabel("Marque : "), 0, 2);
        marqueField = createStyledField();
        add(marqueField, 1, 2);

        add(createStyledLabel("Type : "), 0, 3);
        typeField = createStyledField();
        add(typeField, 1, 3);

        add(createStyledLabel("Catégorie : "), 0, 4);
        categorieField = createStyledField();
        add(categorieField, 1, 4);

        add(createStyledLabel("Nombre de places : "), 0, 5);
        nbPlacesField = createStyledField();
        add(nbPlacesField, 1, 5);

        update(vehicule);
    }

    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("label-pane");
        return label;
    }

    private TextField createStyledField() {
        TextField tf = new TextField();
        tf.setEditable(false);
        tf.getStyleClass().add("pane-textfield");
        return tf;
    }

    public void update(Vehicule vehicule) {
        if (vehicule == null) {
            immatriculeField.setText("");
            marqueField.setText("");
            typeField.setText("");
            categorieField.setText("");
            nbPlacesField.setText("");
        } else {
            immatriculeField.setText(vehicule.getImmatriculation());
            marqueField.setText(vehicule.getMarque());
            typeField.setText(vehicule.getType());
            categorieField.setText(vehicule.getCategorie());
            nbPlacesField.setText(String.valueOf(vehicule.getNbresPlaces()));
        }
    }
}
