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
import utils.CustomStage;

public class AddUserWindow {

	private ObservableList<Utilisateur> userList;
    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private Stage windowAdmin = new Stage();
    private Stage window = new Stage();
    
    public AddUserWindow(ObservableList<Utilisateur> userList) {
        this.userList = userList;
    }

    public void display() {
    	windowAdmin = new Stage();
    	windowAdmin.initModality(Modality.APPLICATION_MODAL);
    	windowAdmin.setTitle("Ajouter un utilisateur");
    	
    	GridPane gridAdmin = new GridPane();
        gridAdmin.setPadding(new Insets(20));
        gridAdmin.setHgap(10);
        gridAdmin.setVgap(10);

        // Champs de saisie
        TextField nomField = new TextField();
        nomField.getStyleClass().add("form-field");
        TextField prenomField = new TextField();
        prenomField.getStyleClass().add("form-field");
        TextField loginField = new TextField();
        loginField.getStyleClass().add("form-field");
        PasswordField passwordField = new PasswordField();
        passwordField.getStyleClass().add("form-field");
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("administrateur", "utilisateur");
        roleCombo.setValue("Le rôle de l'utilisateur");
        ComboBox<String> valideCombo = new ComboBox<>();
        valideCombo.getItems().addAll("oui", "non");

        
        
        // Ajout au grid
        gridAdmin.add(createLabel("Nom :"), 0, 0);
        gridAdmin.add(nomField, 1, 0);
        gridAdmin.add(createLabel("Prénom :"), 0, 1);
        gridAdmin.add(prenomField, 1, 1);
        gridAdmin.add(createLabel("Login :"), 0, 2);
        gridAdmin.add(loginField, 1, 2);
        gridAdmin.add(createLabel("Mot de Passe :"), 0, 3);
        gridAdmin.add(passwordField, 1, 3);
        gridAdmin.add(createLabel("Rôle :"), 0, 4);
        gridAdmin.add(roleCombo, 1, 4);
        gridAdmin.add(createLabel("Valide :"), 0, 5);
        gridAdmin.add(valideCombo, 1, 5);

        // Boutons
        Button addButton = new Button("Ajouter");
        addButton.getStyleClass().add("form-button");
        Button cancelButton = new Button("Annuler");
        cancelButton.getStyleClass().add("form-button");
        gridAdmin.add(addButton, 0, 6);
        gridAdmin.add(cancelButton, 1, 6);
        gridAdmin.getStyleClass().add("form-root");
        
        addButton.setOnAction(e -> {
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String login = loginField.getText();
            String password = passwordField.getText();
            String role = roleCombo.getValue();
            String valide = valideCombo.getValue();

            if (nom.isEmpty() || prenom.isEmpty() || login.isEmpty() || password.isEmpty()) {
                showAlert("Erreur", "Tous les champs sont obligatoires.");
                return;
            }

            String hashedPassword = hashPassword(password);
            if (hashedPassword == null) {
                showAlert("Erreur", "Erreur lors du hashage du mot de passe.");
                return;
            }

            Utilisateur newUser = new Utilisateur();
            newUser.setNom(nom);
            newUser.setPrenom(prenom);
            newUser.setLogin(login);
            newUser.setMotDePasse(hashedPassword);
            newUser.setRole(role);
            newUser.setValide(valide);

            if (utilisateurDAO.addUser(newUser)) {
                userList.add(newUser);
                showAlert("Succès", "Utilisateur ajouté avec succès.");
                windowAdmin.close();
            } else {
                showAlert("Erreur", "Échec de l'ajout de l'utilisateur.");
            }
        });


        cancelButton.setOnAction(e -> window.close());
        ScrollPane sp =new ScrollPane(gridAdmin);
        sp.getStyleClass().add("scroll-pane");
        Scene sceneAdmin = new Scene(sp, 500, 400);
        sceneAdmin.getStylesheets().add(getClass().getResource("/css/styleAdmin.css").toExternalForm());
        windowAdmin.setScene(sceneAdmin);
        windowAdmin.initModality(Modality.APPLICATION_MODAL);
        windowAdmin.getIcons().add(new Image("file:admin.png"));
        windowAdmin.showAndWait();
    }
    
    public void displayuser() {
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Page d'inscription");
        
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        // Champs de saisie
        TextField nomField = new TextField();
        nomField.getStyleClass().add("form-field");
        TextField prenomField = new TextField();
        prenomField.getStyleClass().add("form-field");
        TextField loginField = new TextField();
        loginField.getStyleClass().add("form-field");
        PasswordField passwordField = new PasswordField();

        // Ajout au grid
        grid.add(new Label("Nom :"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom :"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Login :"), 0, 2);
        grid.add(loginField, 1, 2);
        grid.add(new Label("Mot de passe :"), 0, 3);
        grid.add(passwordField, 1, 3);

        // Boutons
        Button addButton = new Button("S'inscrire");
        addButton.getStyleClass().add("button-login");
        Button cancelButton = new Button("Annuler");
        grid.add(addButton, 0, 5);
        grid.add(cancelButton, 1, 5);
        grid.getStyleClass().add("form-root");
        
        addButton.setOnAction(e -> {
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String login = loginField.getText();
            String password = passwordField.getText();

            if (nom.isEmpty() || prenom.isEmpty() || login.isEmpty() || password.isEmpty()) {
                showAlert("Erreur", "Tous les champs sont obligatoires.");
                return;
            }

            String hashedPassword = hashPassword(password);
            if (hashedPassword == null) {
                showAlert("Erreur", "Erreur lors du hashage du mot de passe.");
                return;
            }

            Utilisateur newUser = new Utilisateur();
            newUser.setNom(nom);
            newUser.setPrenom(prenom);
            newUser.setLogin(login);
            newUser.setMotDePasse(hashedPassword);
            newUser.setValide("non");
            newUser.setRole("utilisateur");

            if (utilisateurDAO.addUser(newUser)) {
                userList.add(newUser);
                showAlert("Inscription efffectué avec succès", "Contacter l'administrateur pour la validation du compte.");
                window.close();
            } else {
                showAlert("Erreur", "Échec de l'ajout de l'utilisateur.");
            }
        });


        cancelButton.setOnAction(e -> window.close());
        ScrollPane sp = new ScrollPane(grid);
        sp.getStyleClass().add("scroll-pane");
        
        Scene scene = new Scene(sp, 400, 300);
        scene.getStylesheets().add("/css/style.css");
        window.setScene(scene);
        CustomStage custom = new CustomStage();
        custom.decorate(window, scene, "Formulaire d'incription", false, 400, 300, false);
        window.getIcons().add(new Image("file:icone.png"));
        window.showAndWait();
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
