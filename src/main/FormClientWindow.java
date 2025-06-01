package main;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.Client;
import model.Utilisateur;
import utils.CustomStage;
import dao.ClientDAO;
import dao.UtilisateurDAO;

public class FormClientWindow {

    private final VBox root = new VBox(15);
    private final HBox buttonBox = new HBox(10);
    private final Scene scene = new Scene(root, 900, 600);
    private final Stage window = new Stage();

    private final Label titleLabel = new Label("Nouveau Client");
    private final Label nomLabel = new Label("Nom");
    private final Label prenomLabel = new Label("Prénom");
    private final Label adresseLabel = new Label("Adresse");
    private final Label telephoneLabel = new Label("Téléphone");

    private final TextField nomField = new TextField();
    private final TextField prenomField = new TextField();
    private final TextField adresseField = new TextField();
    private final TextField telephoneField = new TextField();

    private final Button ajouterButton = new Button("Ajouter Client");
    private final Button annulerButton = new Button("Annuler");

    public FormClientWindow() {
        initWindow();
        addStyles();
        addNodes();
        addEvents();
        
        CustomStage customStage = new CustomStage();
        customStage.decorate(window, scene, "Nouveau Client", false, 900, 600, true);
        
        window.show();
    }

    private void initWindow() {
        scene.setRoot(root);
        window.setScene(scene);
        window.setTitle("Nouveau Client");
        window.setWidth(900);
        window.setHeight(600);
        window.getIcons().add(new Image("file:icone.png"));
        window.initModality(Modality.APPLICATION_MODAL);
    }

    private void addNodes() {
        root.getChildren().addAll(
                titleLabel,
                nomLabel, nomField,
                prenomLabel, prenomField,
                adresseLabel, adresseField,
                telephoneLabel, telephoneField,
                buttonBox
        );
        buttonBox.getChildren().addAll(ajouterButton, annulerButton);
    }

    private void addStyles() {
        scene.getStylesheets().add("css/style.css");

        root.getStyleClass().add("form-root");
        buttonBox.getStyleClass().add("form-button-box");

        titleLabel.getStyleClass().add("form-title");

        for (Label label : new Label[]{nomLabel, prenomLabel, adresseLabel, telephoneLabel}) {
            label.getStyleClass().add("form-label");
        }

        for (TextField field : new TextField[]{nomField, prenomField, adresseField, telephoneField}) {
            field.getStyleClass().add("form-textfield");
        }

        ajouterButton.getStyleClass().add("form-button");
        annulerButton.getStyleClass().add("form-button");
    }

    private void addEvents() {
        window.setOnCloseRequest(event -> event.consume());

        ajouterButton.setOnAction(event -> {
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String adresse = adresseField.getText();
            String telephone = telephoneField.getText();

            if (nom.isBlank() || prenom.isBlank() || adresse.isBlank() || telephone.isBlank()) {
                showAlert("Erreur", "Veuillez remplir tous les champs !");
                return;
            }

            Utilisateur utilisateur = new UtilisateurDAO().getUtilisateurConnecte();
            if (utilisateur == null) {
                showAlert("Erreur", "Aucun utilisateur connecté !");
                return;
            }

            Client client = new Client(nom, prenom, adresse, telephone);
            boolean success = new ClientDAO().ajouterClient(client, utilisateur.getId());

            showAlert(
                success ? "Succès" : "Erreur",
                success ? "Client ajouté avec succès !" : "Échec de l'ajout du client."
            );

            if (success) window.close();
        });

        annulerButton.setOnAction(e -> window.close());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getStyleClass().add("custom-alert");
        alert.getDialogPane().getStylesheets().add("css/style.css");
        alert.showAndWait();
    }

}
