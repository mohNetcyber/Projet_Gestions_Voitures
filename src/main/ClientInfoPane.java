package main;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.Client;

public class ClientInfoPane extends GridPane {
    public TextField nomClientField;
    public TextField prenomClientField;
    public TextField adresseClientField;
    public TextField telephoneClientField;

    public ClientInfoPane(Client client) {
        setHgap(10);
        setVgap(10);
        setPadding(new javafx.geometry.Insets(15));
        getStyleClass().add("client-pane");

        Label title = new Label("Informations Client");
        title.getStyleClass().add("label-title-pane");
        add(title, 0, 0, 2, 1);

        // Nom
        add(createStyledLabel("Nom :"), 0, 1);
        nomClientField = createStyledField();
        add(nomClientField, 1, 1);

        // Prénom
        add(createStyledLabel("Prénom :"), 0, 2);
        prenomClientField = createStyledField();
        add(prenomClientField, 1, 2);

        // Adresse
        add(createStyledLabel("Adresse :"), 0, 3);
        adresseClientField = createStyledField();
        add(adresseClientField, 1, 3);

        // Téléphone
        add(createStyledLabel("Téléphone :"), 0, 4);
        telephoneClientField = createStyledField();
        add(telephoneClientField, 1, 4);

        update(client);
    }

    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("label-pane");
        return label;
    }

    private TextField createStyledField() {
        TextField field = new TextField();
        field.setEditable(false);
        field.getStyleClass().add("pane-textfield");
        return field;
    }

    public void update(Client client) {
        if (client == null) {
            nomClientField.setText("");
            prenomClientField.setText("");
            adresseClientField.setText("");
            telephoneClientField.setText("");
        } else {
            nomClientField.setText(client.getNom());
            prenomClientField.setText(client.getPrenom());
            adresseClientField.setText(client.getAdresse());
            telephoneClientField.setText(client.getTelephone());
        }
    }
}
