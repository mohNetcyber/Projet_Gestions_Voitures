package main;

import dao.UtilisateurDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Utilisateur;
import model.Vehicule;

import java.util.List;

public class ManageUsersPage {
    private Stage stage;
    private TableView<Utilisateur> userTable;
    private ObservableList<Utilisateur> userList = FXCollections.observableArrayList();
    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public void display(Stage primaryStage) {
        this.stage = primaryStage;

        // Chargement des utilisateurs
        loadUsersFromDatabase();

        // TableView
        userTable = new TableView<>();
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        userTable.getStyleClass().add("table-view");

        TableColumn<Utilisateur, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getId())));
        
        
        TableColumn<Utilisateur, String> nameColumn = new TableColumn<>("Nom et Prénom");
        nameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getNom() + " " + data.getValue().getPrenom()));

        TableColumn<Utilisateur, String> loginColumn = new TableColumn<>("Login");
        loginColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLogin()));

        TableColumn<Utilisateur, String> roleColumn = new TableColumn<>("Rôle");
        roleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRole()));
        
        TableColumn<Utilisateur, String> valideColumn = new TableColumn<>("Valide");
        valideColumn.setCellValueFactory(data -> {
            String valide = data.getValue().getValide();
            String affichage = "non".equals(valide) ? "Compte non valide" : "Compte valide";
            return new SimpleStringProperty(affichage);
        });

        
        idColumn.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        nameColumn.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        loginColumn.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        roleColumn.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        valideColumn.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        
        userTable.getColumns().addAll(idColumn, nameColumn, loginColumn, roleColumn, valideColumn);
        userTable.setItems(userList);

        // Boutons
        Button addButton = new Button("Ajouter");
        Button modifyButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");
        Button backButton = new Button("Retour");

        addButton.getStyleClass().add("action-button");
        modifyButton.getStyleClass().add("action-button");
        deleteButton.getStyleClass().add("action-button");
        backButton.setId("back-button");
        
        Button viewClientsButton = new Button("Voir les Clients");
        viewClientsButton.getStyleClass().add("action-button");
        viewClientsButton.setOnAction(e -> {
            ViewClientsWindow viewClientsWindow = new ViewClientsWindow();
            viewClientsWindow.display();
        });


        addButton.setOnAction(e -> {
            AddUserWindow addUserWindow = new AddUserWindow(userList);
            
            addUserWindow.display();
        });

        modifyButton.setOnAction(e -> modifyUser());
        deleteButton.setOnAction(e -> deleteUser());

        backButton.setOnAction(e -> {
            new AdminDashboard().afficher();
            stage.close();
        });

        // Layout
        Label titleLabel = new Label("Gestion des utilisateurs");
        titleLabel.setId("page-title");

        HBox buttonBox = new HBox(10, addButton, modifyButton, deleteButton, viewClientsButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, titleLabel, userTable, buttonBox, backButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("main-layout");

        // Scene
        Scene manageUsersScene = new Scene(layout, 1100, 650);

        // 💡 Application de la feuille de style CSS
        manageUsersScene.getStylesheets().add(getClass().getResource("/css/styleAdmin.css").toExternalForm());
        stage.getIcons().add(new Image("file:icon.png"));
        stage.setScene(manageUsersScene);
    }

    private void loadUsersFromDatabase() {
        try {
            List<Utilisateur> users = utilisateurDAO.getAllUsers();
            userList.setAll(users);
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les utilisateurs depuis la base de données.");
            e.printStackTrace();
        }
    }

    private void modifyUser() {
        Utilisateur selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            ModifyUserWindow modifyUserWindow = new ModifyUserWindow(selectedUser, userList);
            modifyUserWindow.display();
        } else {
            showAlert("Aucun utilisateur sélectionné", "Veuillez sélectionner un utilisateur à modifier.");
        }
    }

    private void deleteUser() {
        Utilisateur selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            boolean confirmed = confirmDialog("Confirmation", "Supprimer cet utilisateur ?");
            if (confirmed) {
                if (utilisateurDAO.deleteUser(selectedUser.getId())) {
                    userList.remove(selectedUser);
                    showAlert("Succès", "Utilisateur supprimé.");
                } else {
                    showAlert("Erreur", "Échec de la suppression.");
                }
            }
        } else {
            showAlert("Aucun utilisateur sélectionné", "Veuillez sélectionner un utilisateur.");
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


    private boolean confirmDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }
}
