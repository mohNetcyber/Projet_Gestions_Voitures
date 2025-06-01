package main;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import dao.AgenceDAO;
import dao.VehiculeDAO;
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
import model.Agence;
import model.Vehicule;

import java.util.List;

public class ManageVehiculePage {

    private TableView<Vehicule> vehiculeTable;
    private ObservableList<Vehicule> vehiculeList = FXCollections.observableArrayList();
    private VehiculeDAO vehiculeDAO = new VehiculeDAO();

    public void display(Stage primaryStage) {
        loadVehiculesFromDatabase();

        vehiculeTable = new TableView<>();
        vehiculeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        vehiculeTable.getStyleClass().add("table-view");
        vehiculeTable.setItems(vehiculeList);

        TableColumn<Vehicule, String> idColumn = new TableColumn<>("Immatriculation");
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getImmatriculation()));

        TableColumn<Vehicule, String> marqueColumn = new TableColumn<>("Marque");
        marqueColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMarque()));

        TableColumn<Vehicule, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));

        TableColumn<Vehicule, String> categorieColumn = new TableColumn<>("Catégorie");
        categorieColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategorie()));

        TableColumn<Vehicule, String> carburantColumn = new TableColumn<>("Carburant");
        carburantColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCarburant()));
        
        TableColumn<Vehicule, Double> forfaitJournalierColumn = new TableColumn<>("Forfait Journalier");
        forfaitJournalierColumn.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getForfaitJournalier()).asObject()
        );

        TableColumn<Vehicule, Integer> placesColumn = new TableColumn<>("Nombres de Places");
        placesColumn.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getNbresPlaces()).asObject()
        );

        

        TableColumn<Vehicule, String> dispoColumn = new TableColumn<>("Disponible");
        dispoColumn.setCellValueFactory(data -> {
            int disponible = data.getValue().getDisponible();
            String affichage = (disponible == 1) ? "Oui" : "Non";
            return new SimpleStringProperty(affichage);
        });
        
        TableColumn<Vehicule, String> agenceColumn = new TableColumn<>("Agence");
        agenceColumn.setCellValueFactory(data ->
        {
        	AgenceDAO agenceDAO = new AgenceDAO();
        	Agence agence = agenceDAO.getAgenceById(data.getValue().getIdAgence());
        	return new SimpleStringProperty(agence.getNomAgence());
        });
        
        vehiculeTable.getColumns().addAll(
                idColumn, marqueColumn, typeColumn, carburantColumn,
                categorieColumn, forfaitJournalierColumn, placesColumn, dispoColumn, agenceColumn
        );
        
        
        
        // Boutons
        Button addButton = new Button("Ajouter");
        Button modifyButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");
        Button backButton = new Button("Retour");

        addButton.getStyleClass().add("action-button");
        modifyButton.getStyleClass().add("action-button");
        deleteButton.getStyleClass().add("action-button");
        backButton.setId("back-button");

        addButton.setOnAction(e -> {
            Stage addStage = new Stage();
            new AddVehicleWindow().afficher(addStage, this::loadVehiculesFromDatabase);
        });

        modifyButton.setOnAction(e -> {
            Vehicule selected = vehiculeTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                new UpdateVehiculeWindow(selected, this::loadVehiculesFromDatabase);
            } else {
                showAlert("Sélection requise", "Veuillez sélectionner un véhicule à modifier.");
            }
        });

        deleteButton.setOnAction(e -> {
            Vehicule selected = vehiculeTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                boolean confirm = confirmDialog("Confirmation", "Supprimer ce véhicule ?");
                if (confirm) {
                    if (vehiculeDAO.deleteVehicule(selected.getImmatriculation())) {
                        vehiculeList.remove(selected);
                        showAlert("Succès", "Véhicule supprimé.");
                    } else {
                        showAlert("Erreur", "Échec de la suppression, vérifier que la voiture n'est pas associé à un contrat.");
                    }
                }
            } else {
                showAlert("Sélection requise", "Veuillez sélectionner un véhicule.");
            }
        });

        backButton.setOnAction(e -> {
            new AdminDashboard().afficher();
            primaryStage.close();
        });

        // Layout
        Label titleLabel = new Label("Gestion des Véhicules");
        titleLabel.setId("page-title");

        HBox buttonBox = new HBox(10, addButton, modifyButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, titleLabel, vehiculeTable, buttonBox, backButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("main-layout");

        Scene scene = new Scene(layout, 1100, 650);

        // Feuille de style CSS
        scene.getStylesheets().add(getClass().getResource("/css/styleAdmin.css").toExternalForm());
        primaryStage.getIcons().add(new Image("file:icon.png"));
        primaryStage.setScene(scene);
    }

    private void loadVehiculesFromDatabase() {
        try {
            List<Vehicule> vehicules = vehiculeDAO.getAllVehicules();
            vehiculeList.clear(); 
            vehiculeList.setAll(vehicules);
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les véhicules depuis la base de données.");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean confirmDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(button -> button == ButtonType.OK).isPresent();
    }
}
