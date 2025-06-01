package main;

import java.util.List;

import dao.AgenceDAO;
import dao.VehiculeDAO;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Agence;
import model.Vehicule;

public class AddVehicleWindow {
	private ScrollPane scrollPane = new ScrollPane();
	private VBox root = new VBox(10);
	private Scene scene = new Scene(root);
	private Label immatriculationLabel = new Label("Immatriculation :");
	private TextField immatriculationField = new TextField();
	private Label titleLabel = new Label("Ajouter un véhicule");
	private Label marqueLabel = new Label();
	private Label typeLabel = new Label();
	private Label categorieLabel = new Label();
	private Label carburantLabel = new Label();
	private Label nombrePlacesLabel = new Label();
	private Label agenceLabel = new Label();
	private Label disponibleLabel = new Label();
	private TextField marqueField = new TextField();
	private TextField typeField = new TextField();
	private TextField categorieField = new TextField();
	private TextField carburantField = new TextField();
	private TextField nombrePlacesField = new TextField();
	private ComboBox<Agence> agenceCombo = new ComboBox<>();
	private Label forfaitJournalierLabel = new Label();
	private TextField forfaitJournalierField = new TextField();
	private Button addButton = new Button("Ajouter");
	private Button cancelButton = new Button("Annuler");
	
    public void afficher(Stage window, Runnable refreshCallback) {
        root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("form-root");
        root.setPrefSize(400, 300);

        titleLabel = new Label("Ajouter un véhicule");
        titleLabel.getStyleClass().add("title-label");
        
        immatriculationLabel.setText("Immatriculation :");
        immatriculationLabel.getStyleClass().add("form-label");
        immatriculationField.setPromptText("Entrez l'immatriculation du véhicule");
        immatriculationField.getStyleClass().add("form-field");
        
        marqueLabel.setText("Marque :");
        marqueLabel.getStyleClass().add("form-label");
        marqueField.setPromptText("Entrez la marque du véhicule");
        marqueField.getStyleClass().add("form-field");
        
        typeLabel.setText("Type :");
        typeLabel.getStyleClass().add("form-label");
        typeField.setPromptText("Entrez le type du véhicule");
        typeField.getStyleClass().add("form-field");
        
        categorieLabel.setText("Catégorie :");
        categorieLabel.getStyleClass().add("form-label");
        categorieField.setPromptText("Entrez la catégorie du véhicule");
        categorieField.getStyleClass().add("form-field");
        
        carburantLabel.setText("Carburant :");
        carburantLabel.getStyleClass().add("form-label");
        carburantField.setPromptText("Entrez le carburant du véhicule");
        carburantField.getStyleClass().add("form-field");
        
        nombrePlacesLabel.setText("Nombre de places :");
        nombrePlacesLabel.getStyleClass().add("form-label");
        nombrePlacesField.setPromptText("Entrez le nombre de places");
        nombrePlacesField.getStyleClass().add("form-field");
        
        forfaitJournalierLabel.setText("Forfait Journalier :  ");
        forfaitJournalierLabel.getStyleClass().add("form-label");
        forfaitJournalierField.setPromptText("Forfait journalier");
        forfaitJournalierField.getStyleClass().add("form-field");
        
        agenceLabel.setText("Agence :");
        agenceLabel.getStyleClass().add("form-label");
        agenceCombo.setPromptText("Sélectionnez l'agence");
        agenceCombo.getStyleClass().add("form-field");
        AgenceDAO agenceDAO = new AgenceDAO();
        List<Agence> agences = agenceDAO.getAllAgences();
        agenceCombo.getItems().addAll(agences);
        

        addButton = new Button("Ajouter");
        addButton.getStyleClass().add("form-button");
        addButton.setOnAction(e -> {
            String immatriculation = immatriculationField.getText().trim();
            String marque = marqueField.getText().trim();
            String type = typeField.getText().trim();
            String categorie = categorieField.getText().trim();
            String carburant = carburantField.getText().trim();
            String nombrePlaces = nombrePlacesField.getText().trim();
            float forfait = Integer.parseInt(forfaitJournalierField.getText());
            Agence selectedAgence = agenceCombo.getSelectionModel().getSelectedItem();

            // Check for null or empty values
            if (immatriculation == null || immatriculation.isEmpty()) {
                showAlert("Erreur", "L'immatriculation ne peut pas être vide !");
                return;
            }
            if (marque == null || marque.isEmpty()) {
                showAlert("Erreur", "La marque ne peut pas être vide !");
                return;
            }
            if (type == null || type.isEmpty()) {
                showAlert("Erreur", "Le type ne peut pas être vide !");
                return;
            }
            if (categorie == null || categorie.isEmpty()) {
                showAlert("Erreur", "La catégorie ne peut pas être vide !");
                return;
            }
            if (carburant == null || carburant.isEmpty()) {
                showAlert("Erreur", "Le carburant ne peut pas être vide !");
                return;
            }
            if (nombrePlaces == null || nombrePlaces.isEmpty()) {
                showAlert("Erreur", "Le nombre de places ne peut pas être vide !");
                return;
            }
            if(forfaitJournalierField.getText() == null) {
            	showAlert("Erreur", "Entrer le forfait journalier !");
            	return;
            }
            if (selectedAgence == null) {
                showAlert("Erreur", "Veuillez sélectionner une agence !");
                return;
            }

            try {
                int nombrePlacesInt = Integer.parseInt(nombrePlaces);
                int agenceId = selectedAgence.getIdAgence();
                Vehicule vehicule = new Vehicule(immatriculation, marque, type, categorie, carburant, nombrePlacesInt, forfait, agenceId, 0);
                VehiculeDAO vehiculeDAO = new VehiculeDAO();
                
                if (!vehiculeDAO.addVehicule(vehicule)) {
                    showAlert("Erreur", "Échec de l'ajout du véhicule !");
                    return;
                }
                if (refreshCallback != null) refreshCallback.run();
                showAlert("Succès", "Véhicule ajouté avec succès !");
                window.close();
            } catch (NumberFormatException ex) {
                showAlert("Erreur", "Le nombre de places doit être un nombre entier !");
            }
        });

        
        cancelButton = new Button("Annuler");
        cancelButton.getStyleClass().add("form-button");
        cancelButton.setOnAction(e -> {
			immatriculationField.clear();
			marqueField.clear();
			typeField.clear();
			categorieField.clear();
			carburantField.clear();
			nombrePlacesField.clear();
			forfaitJournalierField.clear();
			agenceCombo.getSelectionModel().clearSelection();
		});

        root.getChildren().addAll(titleLabel, immatriculationLabel, immatriculationField, marqueLabel, marqueField, typeLabel, typeField,
				categorieLabel, categorieField, carburantLabel, carburantField, nombrePlacesLabel, nombrePlacesField, forfaitJournalierLabel, forfaitJournalierField,
				agenceLabel, agenceCombo, addButton, cancelButton);
        scrollPane = new ScrollPane(root);
        
        scene = new Scene(scrollPane, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/css/styleAdmin.css").toExternalForm());
        window.setScene(scene);
        window.getIcons().add(new Image("file:admin.png"));
        window.setTitle("Ajouter un véhicule");

        window.initModality(Modality.APPLICATION_MODAL);
        window.show();
        
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
