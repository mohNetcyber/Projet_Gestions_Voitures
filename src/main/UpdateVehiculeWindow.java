package main;

import java.util.List;

import dao.AgenceDAO;
import dao.VehiculeDAO;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Agence;
import model.Vehicule;

public class UpdateVehiculeWindow {
    private Vehicule vehicule;
    private VehiculeDAO vehiculeDAO = new VehiculeDAO();
    private AgenceDAO agenceDAO = new AgenceDAO();
    private List<Agence> agences;
    private ScrollPane scrollPane = new ScrollPane();

    public UpdateVehiculeWindow(Vehicule vehicule, Runnable refreshCallback) {
        this.vehicule = vehicule;

        Stage window = new Stage();
        window.setTitle("✏️ Modifier le Véhicule");

        Label marqueLabel = createLabel("🚗 Marque :");
        marqueLabel.getStyleClass().add("form-label");
        TextField marqueField = createTextField(vehicule.getMarque());
        marqueField.getStyleClass().add("form-field");
        
        Label typeLabel = createLabel("🔧 Type :");
        typeLabel.getStyleClass().add("form-label");
        TextField typeField = createTextField(vehicule.getType());
        typeField.getStyleClass().add("form-field");
        
        Label categorieLabel = createLabel("📦 Catégorie :");
        categorieLabel.getStyleClass().add("form-label");
        TextField categorieField = createTextField(vehicule.getCategorie());
        categorieField.getStyleClass().add("form-field");
        
        Label carburantLabel = createLabel("⛽ Carburant :");
        carburantLabel.getStyleClass().add("form-label");
        TextField carburantField = createTextField(vehicule.getCarburant());
        carburantField.getStyleClass().add("form-field");
        
        Label nombrePlacesLabel = createLabel("👥 Nombre de places :");
        nombrePlacesLabel.getStyleClass().add("form-label");
        TextField nombrePlacesField = createTextField(String.valueOf(vehicule.getNbresPlaces()));
        nombrePlacesField.getStyleClass().add("form-field");
        
        Label forfaitJournalierLabel = createLabel("Forfait Journalier :");
        forfaitJournalierLabel.getStyleClass().add("form-label");
        TextField forfaitJournalierField = createTextField(String.valueOf(vehicule.getForfaitJournalier()));
        forfaitJournalierField.getStyleClass().add("form-field");
        
        agences = agenceDAO.getAllAgences();
        Label agenceLabel = createLabel("🏢 Agence :");
        agenceLabel.getStyleClass().add("form-label");
        ComboBox<String> agenceCombo = new ComboBox<>();
        for (Agence agence : agences) {
            agenceCombo.getItems().add(agence.getNomAgence());
        }

        // Pré-sélectionner l’agence actuelle
        for (int i = 0; i < agences.size(); i++) {
            if (agences.get(i).getIdAgence() == vehicule.getIdAgence()) {
                agenceCombo.getSelectionModel().select(i);
                break;
            }
        }

        Label disponibleLabel = createLabel("📦 Disponible :");
        ComboBox<String> disponibleCombo = new ComboBox<>();
        disponibleCombo.getItems().addAll("1", "0");
        disponibleCombo.setValue(String.valueOf(vehicule.getDisponible()));

        Button saveButton = new Button("💾 Enregistrer");
        saveButton.getStyleClass().add("form-button");
        saveButton.setOnAction(e -> {
            if (marqueField.getText().isEmpty() || typeField.getText().isEmpty() ||
                categorieField.getText().isEmpty() || carburantField.getText().isEmpty() ||
                nombrePlacesField.getText().isEmpty() || forfaitJournalierField.getText().isEmpty() || 
                agenceCombo.getSelectionModel().isEmpty()) {
                showAlert("Erreur", "Tous les champs doivent être remplis.");
                return;
            }

            try {
                vehicule.setMarque(marqueField.getText());
                vehicule.setType(typeField.getText());
                vehicule.setCategorie(categorieField.getText());
                vehicule.setCarburant(carburantField.getText());
                vehicule.setNbresPlaces(Integer.parseInt(nombrePlacesField.getText()));
                vehicule.setIdAgence(agences.get(agenceCombo.getSelectionModel().getSelectedIndex()).getIdAgence());
                vehicule.setForfaitJournalier(Float.parseFloat(forfaitJournalierField.getText()));
                vehicule.setDisponible(Integer.parseInt(disponibleCombo.getValue()));

                boolean success = vehiculeDAO.updateVehicule(vehicule);
                if (success) {
                    showAlert("Succès", "Véhicule modifié avec succès.");
                    if (refreshCallback != null) refreshCallback.run();
                    window.close();
                } else {
                    showAlert("Erreur", "Échec de la mise à jour du véhicule.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Erreur", "Vérifiez que tout les champs sont bien remplis.");
            }
        });

        VBox layout = new VBox(12,
            marqueLabel, marqueField,
            typeLabel, typeField,
            categorieLabel, categorieField,
            carburantLabel, carburantField,
            nombrePlacesLabel, nombrePlacesField,
            forfaitJournalierLabel, forfaitJournalierField,
            agenceLabel, agenceCombo,
            disponibleLabel, disponibleCombo,
            saveButton
        );

        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("form-root");

        scrollPane.setContent(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");

        Scene scene = new Scene(scrollPane, 400, 500);
        scene.getStylesheets().add("/css/styleAdmin.css");

        window.setScene(scene);
        window.getIcons().add(new Image("file:admin.png"));
        window.initModality(Modality.APPLICATION_MODAL);
        window.show();
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
