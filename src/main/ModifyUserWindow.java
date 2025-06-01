package main;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dao.UtilisateurDAO;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import model.Utilisateur;

public class ModifyUserWindow {

	private ScrollPane scrollPane = new ScrollPane();
    private Utilisateur userToModify;
    private Stage window = new Stage();
    private ObservableList<Utilisateur> userList;
    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public ModifyUserWindow(Utilisateur userToModify, ObservableList<Utilisateur> userList) {
        this.userToModify = userToModify;
        this.userList = userList;
    }

    public void display() {
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Modifier l'utilisateur");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        // Champs pré-remplis
        TextField nomField = new TextField(userToModify.getNom());
        TextField prenomField = new TextField(userToModify.getPrenom());
        TextField loginField = new TextField(userToModify.getLogin());
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Laisser vide pour ne pas changer");

        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("utilisateur", "administrateur");
        roleCombo.setValue(userToModify.getRole());
        
        ComboBox<String> valideCombo = new ComboBox<>();
        valideCombo.getItems().addAll("oui", "non");
        valideCombo.setValue(String.valueOf(userToModify.getValide()));

        
        // Ajout au grid
        grid.add(createLabel("Nom :"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(createLabel("Prénom :"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(createLabel("Login :"), 0, 2);
        grid.add(loginField, 1, 2);
        grid.add(createLabel("Mot de Passe :"), 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(createLabel("Rôle :"), 0, 4);
        grid.add(roleCombo, 1, 4);
        grid.add(createLabel("Valide :"), 0, 5);
        grid.add(valideCombo, 1, 5);
        
        // Boutons
        Button saveButton = new Button("Enregistrer");
        saveButton.getStyleClass().add("form-button");
        
        Button cancelButton = new Button("Annuler");
        cancelButton.getStyleClass().add("form-button");
        grid.add(saveButton, 0, 6);
        grid.add(cancelButton, 1, 6);

        saveButton.setOnAction(e -> {
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String login = loginField.getText();
            String motDePasse = passwordField.getText();
            String role = roleCombo.getValue();
            String valide = valideCombo.getValue();
            String hashedPassword = hashPassword(motDePasse);
            if (hashedPassword == null) {
                showAlert("Erreur", "Erreur lors du hashage du mot de passe.");
                return;
            }

            if (nom.isEmpty() || prenom.isEmpty() || login.isEmpty()) {
                showAlert("Erreur", "Nom, prénom et login sont obligatoires.");
                return;
            }

            userToModify.setNom(nom);
            userToModify.setPrenom(prenom);
            userToModify.setLogin(login);
            userToModify.setRole(role);
            userToModify.setValide(valide);
            if (!motDePasse.isEmpty()) {
                userToModify.setMotDePasse(hashedPassword);
            }

            if (utilisateurDAO.updateUser(userToModify)) {
                userList.set(userList.indexOf(userToModify), userToModify);
                showAlert("Succès", "Utilisateur modifié avec succès.");
                window.close();
            } else {
                showAlert("Erreur", "Échec de la mise à jour.");
            }
        });

        cancelButton.setOnAction(e -> window.close());
        scrollPane.setContent(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        Scene scene = new Scene(scrollPane, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/css/styleAdmin.css").toExternalForm());
        window.setScene(scene);
        window.getIcons().add(new Image("file:admin.png"));
        window.initModality(Modality.APPLICATION_MODAL);
        window.showAndWait();
    }
    
    private Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private TextField createTextField(String value) {
        TextField field = new TextField(value);
        field.getStyleClass().add("form-field");
        return field;
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
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
