package main;

import utils.CustomStage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import model.Vehicule;
import dao.VehiculeDAO;

public class ListVehiculeWindowAdmin {
    private VBox root = new VBox();
    private Scene scene = new Scene(root, 800, 500);
    private Stage window = new Stage();
    private TableView<Vehicule> tableView = new TableView<>();
    private ObservableList<Vehicule> vehiculeList;

    private void initWindow() {
        window.setTitle("Liste des Véhicules");
        window.setScene(scene);
        window.show();
    }

    private void setupTable() {
        TableColumn<Vehicule, String> immatriculationCol = new TableColumn<>("Immatriculation");
        TableColumn<Vehicule, String> marqueCol = new TableColumn<>("Marque");
        TableColumn<Vehicule, String> typeCol = new TableColumn<>("Type");
        TableColumn<Vehicule, String> categorieCol = new TableColumn<>("Catégorie");
        TableColumn<Vehicule, Integer> placesCol = new TableColumn<>("Places");

        immatriculationCol.setCellValueFactory(new PropertyValueFactory<>("immatriculation"));
        marqueCol.setCellValueFactory(new PropertyValueFactory<>("marque"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        categorieCol.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        placesCol.setCellValueFactory(new PropertyValueFactory<>("nbresPlaces"));

        tableView.getColumns().addAll(immatriculationCol, marqueCol, typeCol, categorieCol, placesCol);
        
        // Charger les véhicules
        VehiculeDAO vehiculeDAO = new VehiculeDAO();
        vehiculeList = FXCollections.observableArrayList(vehiculeDAO.getAllVehicules());
        tableView.setItems(vehiculeList);

        root.getChildren().add(tableView);
    }
    
    private Vehicule openModifyVehiculeForm(Vehicule vehicule) {
        Stage editWindow = new Stage();
        VBox root = new VBox(15); // Espacement amélioré
        root.setPadding(new Insets(20));
        Scene scene = new Scene(root, 450, 350);
        editWindow.setTitle("Modifier Véhicule");

        // Labels et champs de saisie
        Label immatriculationLabel = new Label("Immatriculation :");
        TextField immatriculationField = new TextField(vehicule.getImmatriculation());
        immatriculationField.setDisable(true); // Désactiver modification de l'immatriculation (Clé primaire)

        Label marqueLabel = new Label("Marque :");
        TextField marqueField = new TextField(vehicule.getMarque());

        Label typeLabel = new Label("Type :");
        TextField typeField = new TextField(vehicule.getType());

        Label categorieLabel = new Label("Catégorie :");
        TextField categorieField = new TextField(vehicule.getCategorie());

        Label placesLabel = new Label("Nombre de Places :");
        TextField placesField = new TextField(String.valueOf(vehicule.getNbresPlaces()));

        Label agenceLabel = new Label("ID Agence :");
        TextField agenceField = new TextField(String.valueOf(vehicule.getIdAgence()));
        agenceField.setDisable(true); // Désactiver modification ID agence

        // Bouton de sauvegarde stylisé
        Button saveButton = new Button("✅ Sauvegarder");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 15px;");
        saveButton.setOnAction(event -> {
            vehicule.setMarque(marqueField.getText());
            vehicule.setType(typeField.getText());
            vehicule.setCategorie(categorieField.getText());
            vehicule.setNbresPlaces(Integer.parseInt(placesField.getText()));

            editWindow.close(); // Fermer après modification
        });

        // Ajout des composants à la fenêtre
        root.getChildren().addAll(immatriculationLabel, immatriculationField, marqueLabel, marqueField, typeLabel, typeField, categorieLabel, categorieField, placesLabel, placesField, agenceLabel, agenceField, saveButton);
        editWindow.setScene(scene);
        editWindow.showAndWait(); 

        return vehicule; // Retourne le véhicule modifié
    }

    
    private void setupButtons() {
        Button addButton = new Button("Ajouter");
        Button modifyButton = new Button("Modifier");
        Button deleteButton = new Button("Supprimer");

        // 🟢 Ajouter un véhicule
        addButton.setOnAction(e -> {
            Vehicule newVehicule = openAddVehiculeForm(); // Ouvre le formulaire d'ajout
            if (newVehicule != null) {
                VehiculeDAO vehiculeDAO = new VehiculeDAO();
                boolean isAdded = vehiculeDAO.addVehicule(newVehicule);
                if (isAdded) {
                    vehiculeList.add(newVehicule);
                    showAlert("Succès", "Véhicule ajouté avec succès !");
                    refreshTable();
                } else {
                    showAlert("Erreur", "Échec de l'ajout du véhicule.");
                }
            }
        });

        // Existing modify and delete button logic remains unchanged
        modifyButton.setOnAction(e -> {
            Vehicule selectedVehicule = tableView.getSelectionModel().getSelectedItem();
            if (selectedVehicule != null) {
                Vehicule updatedVehicule = openModifyVehiculeForm(selectedVehicule);
                if (updatedVehicule != null) {
                    VehiculeDAO vehiculeDAO = new VehiculeDAO();
                    boolean isUpdated = vehiculeDAO.modifierVehicule(updatedVehicule);
                    if (isUpdated) {
                        int index = vehiculeList.indexOf(selectedVehicule);
                        vehiculeList.set(index, updatedVehicule);
                        refreshTable();
                        showAlert("Succès", "Véhicule modifié avec succès !");
                    } else {
                        showAlert("Erreur", "Échec de la modification du véhicule.");
                    }
                }
            } else {
                showAlert("Erreur", "Veuillez sélectionner un véhicule à modifier.");
            }
        });

        deleteButton.setOnAction(e -> {
            Vehicule selectedVehicule = tableView.getSelectionModel().getSelectedItem();
            if (selectedVehicule != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Êtes-vous sûr de vouloir supprimer ce véhicule ?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        VehiculeDAO vehiculeDAO = new VehiculeDAO();
                        boolean isDeleted = vehiculeDAO.supprimerVehicule(selectedVehicule.getImmatriculation());
                        if (isDeleted) {
                            vehiculeList.remove(selectedVehicule);
                            refreshTable();
                            showAlert("Succès", "Véhicule supprimé avec succès !");
                        } else {
                            showAlert("Erreur", "Échec de la suppression du véhicule.");
                        }
                    }
                });
            } else {
                showAlert("Erreur", "Veuillez sélectionner un véhicule à supprimer.");
            }
        });

        HBox buttonBox = new HBox(10, addButton, modifyButton, deleteButton);
        root.getChildren().add(buttonBox);
    }


    private Vehicule openAddVehiculeForm() {
        Stage addWindow = new Stage();
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        Scene scene = new Scene(root, 450, 350);
        addWindow.setTitle("Ajouter Véhicule");

        // Champs de saisie
        TextField immatriculationField = new TextField();
        immatriculationField.setPromptText("Immatriculation");

        TextField marqueField = new TextField();
        marqueField.setPromptText("Marque");

        TextField typeField = new TextField();
        typeField.setPromptText("Type");

        TextField categorieField = new TextField();
        categorieField.setPromptText("Catégorie");
        
        TextField carburantField = new TextField();
        carburantField.setPromptText("Carburant");

        TextField placesField = new TextField();
        placesField.setPromptText("Nombre de Places");

        TextField agenceField = new TextField();
        agenceField.setPromptText("ID Agence");

        // Bouton d'ajout
        Button addButton = new Button("✅ Ajouter");
        addButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 15px;");
        addButton.setOnAction(event -> {
            Vehicule newVehicule = new Vehicule(
                immatriculationField.getText(),
                marqueField.getText(),
                typeField.getText(),
                categorieField.getText(),
                carburantField.getText(),
                Integer.parseInt(placesField.getText()),
                Integer.parseInt(agenceField.getText()),
                0
            );
            VehiculeDAO vehiculeDAO = new VehiculeDAO();
            boolean isAdded = vehiculeDAO.addVehicule(newVehicule);
            if (isAdded) {
				vehiculeList.add(newVehicule);
				refreshTable();
				showAlert("Succès", "Véhicule ajouté avec succès !");
			} else {
				showAlert("Erreur", "Échec de l'ajout du véhicule.");
			}

            addWindow.close();
        });

        root.getChildren().addAll(immatriculationField, marqueField, typeField, categorieField, placesField, agenceField, addButton);
        addWindow.setScene(scene);
        addWindow.showAndWait(); 

        return new Vehicule(
            immatriculationField.getText(),
            marqueField.getText(),
            typeField.getText(),
            categorieField.getText(),
            carburantField.getText(),
            Integer.parseInt(placesField.getText()),
            Integer.parseInt(agenceField.getText())
        );
    }
	// 🟢 Méthode pour rafraîchir la table après une action
    private void refreshTable() {
        tableView.refresh();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public ListVehiculeWindowAdmin() {
        initWindow();
        setupTable();
        setupButtons();
    }
}
