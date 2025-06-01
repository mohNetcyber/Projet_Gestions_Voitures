package main;

import dao.ClientDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Client;
import model.Utilisateur;

public class ViewClientsWindow {
    private Stage window;
    private TableView<Client> clientTable;
    private ObservableList<Client> clientList;
    private ClientDAO clientDAO;

    public ViewClientsWindow() {
        this.clientDAO = new ClientDAO();
        this.clientList = FXCollections.observableArrayList();
    }

    public void display() {
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Liste des Clients");

        // Create table
        clientTable = new TableView<>();
        clientTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        clientTable.getStyleClass().add("table-view");

        // Define columns
        TableColumn<Client, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(String.valueOf(data.getValue().getId())));

        TableColumn<Client, String> nameColumn = new TableColumn<>("Nom");
        nameColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getNom()));

        TableColumn<Client, String> prenomColumn = new TableColumn<>("Prénom");
        prenomColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getPrenom()));

        TableColumn<Client, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getAdresse()));

        TableColumn<Client, String> telephoneColumn = new TableColumn<>("Téléphone");
        telephoneColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getTelephone()));

        TableColumn<Client, String> userColumn = new TableColumn<>("Ajouté par");
        userColumn.setCellValueFactory(data -> {
            ClientDAO clientDAO = new ClientDAO();
            Utilisateur utilisateur = clientDAO.getUserByClientId(data.getValue().getId());
            return new SimpleStringProperty(utilisateur != null ? 
                utilisateur.getNom() + " " + utilisateur.getPrenom() : "N/A");
        });


        // Set column widths
        idColumn.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        nameColumn.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        prenomColumn.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        emailColumn.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        telephoneColumn.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        userColumn.setMaxWidth(1f * Integer.MAX_VALUE * 15);

        clientTable.getColumns().addAll(idColumn, nameColumn, prenomColumn, 
                                      emailColumn, telephoneColumn, userColumn);

        // Load data
        loadClientsFromDatabase();

        // Create close button
        Button closeButton = new Button("Fermer");
        closeButton.setId("back-button");
        closeButton.setOnAction(e -> window.close());

        // Layout
        Label titleLabel = new Label("Liste des Clients");
        titleLabel.setId("page-title");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(titleLabel, clientTable, closeButton);
        layout.getStyleClass().add("main-layout");

        // Scene
        Scene scene = new Scene(layout, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/css/styleAdmin.css").toExternalForm());

        window.setScene(scene);
        window.getIcons().add(new Image("file:icon.png"));
        window.showAndWait();
    }

    private void loadClientsFromDatabase() {
        try {
            clientList.setAll(clientDAO.getAllClientsWithUsers());
            clientTable.setItems(clientList);
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les clients depuis la base de données.");
            e.printStackTrace();
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
