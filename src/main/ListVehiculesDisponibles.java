package main;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import model.Agence;
import model.Contrat;
import model.Vehicule;
import utils.CustomStage;
import model.Utilisateur;
import model.Client;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dao.AgenceDAO;
import dao.ClientDAO;
import dao.ContratDAO;
import dao.VehiculeDAO;
import dao.UtilisateurDAO;

public class ListVehiculesDisponibles {
	private ScrollPane scrollPane = new ScrollPane();
    private VBox root = new VBox();
    private Scene scene = new Scene(root, 800, 500);
    private Stage window = new Stage();
    private VBox rootform = new VBox(15);
    private Label vehiculeLabel = new Label();
    private Label clientLabel = new Label();
    private Label agenceLabel = new Label();
    private Label dateDebutLabel = new Label();
    private Label dateRetourLabel = new Label();
    private Label forfaitLabel = new Label();
    private Label kmDepartLabel = new Label();
    private Label KmTarifLabel = new Label();
    Button validerButton = new Button();
    private DatePicker dateDebutPicker = new DatePicker();
    private DatePicker dateRetourPicker = new DatePicker();
    private TextField forfaitField = new TextField();
    private TextField kmDepartField = new TextField();
    private TextField kmTarifField = new TextField();
    private ComboBox<Client> clientBox = new ComboBox<>();
    private TextField agenceField = new TextField();
    private TableView<Vehicule> tableView = new TableView<>();
    private TableColumn<Vehicule, String> immatriculationCol = new TableColumn<>("Immatriculation");
    private TableColumn<Vehicule, String> marqueCol = new TableColumn<>("Marque");
    private TableColumn<Vehicule, String> typeCol = new TableColumn<>("Type");
    private TableColumn<Vehicule, String> categorieCol = new TableColumn<>("Catégorie");
    private TableColumn<Vehicule, String> carburantCol = new TableColumn<>("Carburant");
    private TableColumn<Vehicule, Integer> placesCol = new TableColumn<>("Places");
    private TableColumn<Vehicule, Float> forfaitJournalier = new TableColumn<>("Forfait Journalier");
    private TableColumn<Vehicule, String> agence = new TableColumn<>("Agence");
    private TableColumn<Vehicule, Void> actionCol = new TableColumn<>("📝 Louer");
    private ObservableList<Vehicule> vehiculeList;
    
    private TextField searchField = new TextField();
    private ComboBox<String> sortComboBox = new ComboBox<>();
    private ToggleGroup sortOrderGroup = new ToggleGroup();
    private RadioButton ascendingRadio = new RadioButton("Ascendant");
    private RadioButton descendingRadio = new RadioButton("Descendant");
    private FilteredList<Vehicule> filteredVehicules;
    private SortedList<Vehicule> sortedVehicules;

    
    private Alert alert = new Alert(Alert.AlertType.INFORMATION);
    
    private void openLouerForm(Vehicule vehicule) {
    	 Stage louerWindow = new Stage();
    	    louerWindow.initModality(Modality.APPLICATION_MODAL);
    	    louerWindow.initOwner(window);
    	    
    	    // Create a new ScrollPane instance for this form
    	    ScrollPane formScrollPane = new ScrollPane();
    	    rootform = new VBox(15);
    	    rootform.setPadding(new Insets(20));
    	    rootform.getStyleClass().add("form-root");

    	    formScrollPane.setContent(rootform);
    	    formScrollPane.setFitToWidth(true);
    	    
    	    Scene scene = new Scene(formScrollPane, 600, 400);
    	    louerWindow.setTitle("📝 Louer un Véhicule");
    	    louerWindow.getIcons().add(new Image("file:icone.png"));
    	    louerWindow.setHeight(600);
    	    louerWindow.setWidth(450);


        vehiculeLabel = new Label("🚗 Véhicule : " + vehicule.getMarque() + " - " + vehicule.getImmatriculation());
        
        Label infoLabel = new Label("Informations :");
        Text text = new Text("⚠️ Note : Le délai de location doit être respecté. Des frais supplémentaires (allant jusqu'à 30% du tarif journalier) seront appliqués en cas de retard lors du calcul de la facture.");
        text.getStyleClass().add("info-text");
        text.setWrappingWidth(380);
        agenceLabel.getStyleClass().add("form-label");
        
        clientLabel = new Label("👤 Client :");
        clientBox = new ComboBox<>();
        
        ClientDAO clientDAO = new ClientDAO();
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        Utilisateur utilisateurConnecte = utilisateurDAO.getUtilisateurConnecte();
        int idUser = utilisateurConnecte.getId();
        List<Client> clients = clientDAO.getClientsByUserId(idUser);
        clientBox.getItems().addAll(clients);

        // Définir l'affichage des clients dans la ComboBox
        clientBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Client client, boolean empty) {
                super.updateItem(client, empty);
                setText(empty || client == null ? null : client.getNom() + " " + client.getPrenom());
            }
        });
        clientBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Client client, boolean empty) {
                super.updateItem(client, empty);
                setText(empty || client == null ? null : client.getNom() + " " + client.getPrenom());
            }
        });
        
        agenceLabel = new Label("🏢 Agence Départ :");
        agenceField = new TextField();
        

        // Charger les agences depuis la base de données
        AgenceDAO agenceDAO = new AgenceDAO();
        Agence agence = agenceDAO.getAgenceByIdVehicule(vehicule.getImmatriculation());
        agenceField.setText(agence.getNomAgence());
        agenceField.setEditable(false);
        
        kmDepartLabel = new Label("🚗 Kilométrage de départ :");
        kmDepartField = new TextField();
        kmDepartField.setPromptText("Ex: 0.00");
        
        KmTarifLabel = new Label("💰 Tarif au km :");
        kmTarifField = new TextField();
        kmTarifField.setPromptText("Ex: 0.50");
        
        dateDebutLabel = new Label("📅 Date départ :");
        dateDebutPicker = new DatePicker();

        dateRetourLabel = new Label("📅 Date retour prévu :");
        dateRetourPicker = new DatePicker();

        forfaitLabel = new Label("💰 Forfait journalier :");
        forfaitField = new TextField();
        forfaitField.setText(String.valueOf(vehicule.getForfaitJournalier()));
        forfaitField.setEditable(false);


        // Bouton de validation
        validerButton = new Button("✅ Valider Contrat");
        validerButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px;");
        validerButton.setOnAction(event -> {
            if (dateDebutPicker.getValue() == null || dateRetourPicker.getValue() == null) {
                showAlert("Erreur", "Veuillez sélectionner les dates de début et de retour.");
                return;
            } else if (dateDebutPicker.getValue().isAfter(dateRetourPicker.getValue())) {
                showAlert("Erreur", "La date de retour doit être après la date de début.");
                return;
            }

        	
            if (clientBox.getValue() == null || 
                dateDebutPicker.getValue() == null ||
                dateRetourPicker.getValue() == null ||
                kmDepartField.getText().trim().isEmpty() ||
                kmTarifField.getText().trim().isEmpty()) {
                    
                showAlert("Erreur", "Veuillez remplir tous les champs.");
                return;
            }

            // Validate numeric fields
            try {
                int kmDepart = Integer.parseInt(kmDepartField.getText().trim());
                double kmTarif = Double.parseDouble(kmTarifField.getText().trim());
                double forfait = Double.parseDouble(forfaitField.getText().trim());

                if (forfait < 0 || kmDepart < 0 || kmTarif < 0) {
                    showAlert("Erreur", "Les valeurs numériques ne peuvent pas être négatives.");
                    return;
                }

                
                Contrat contrat = new Contrat(
                    0, // ID will be generated by database
                    clientBox.getValue().getId(),
                    vehicule.getImmatriculation(),
                    agence.getIdAgence(), // Use the actual agency ID
                    dateDebutPicker.getValue().toString(),
                    dateRetourPicker.getValue().toString(),
                    forfait,
                    kmDepart,
                    kmTarif,
                    0 // Initial status
                );

                ContratDAO contratDAO = new ContratDAO();
                boolean isCreated = contratDAO.ajouterContrat(contrat);
                
                VehiculeDAO vehiculeDAO = new VehiculeDAO();
                boolean indisponible = vehiculeDAO.setVehiculeIndisponible(vehicule.getImmatriculation());
                
                if (isCreated && indisponible) {
                    alert.setAlertType(Alert.AlertType.INFORMATION);
                    showAlert("Succès", "Contrat de location enregistré !");
                    
                    // Refresh table data
                    List<Vehicule> vehicules = vehiculeDAO.getVehiculesDisponibles();
                    vehiculeList.clear();
                    vehiculeList.addAll(vehicules != null ? vehicules : Collections.emptyList());
                    
                    louerWindow.close();
                } else {
                    showAlert("Erreur", "Échec de la création du contrat.");
                }

            } catch (NumberFormatException e) {
                showAlert("Erreur", "Veuillez saisir des valeurs numériques valides pour le forfait, le kilométrage et le tarif.");
            }
        });


        rootform.getChildren().addAll(vehiculeLabel, text, clientLabel, clientBox, agenceLabel, agenceField, dateDebutLabel, dateDebutPicker, dateRetourLabel, dateRetourPicker, kmDepartLabel, kmDepartField, KmTarifLabel, kmTarifField, forfaitLabel, forfaitField, validerButton);
        scene.getStylesheets().add("css/style.css");
        louerWindow.setScene(scene);
        CustomStage customStage = new CustomStage();
        customStage.decorate(louerWindow, scene, "Louer un véhicule", false, 600, 700, true);
        
        louerWindow.showAndWait();
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


    private void addStylesToNodes() {
		scene.getStylesheets().add("css/style.css");
		root.getStyleClass().add("root-liste");
		tableView.getStyleClass().add("table-view");
		alert.getDialogPane().getStyleClass().add("alert-dialog");
		vehiculeLabel.getStyleClass().add("label-form");
		clientLabel.getStyleClass().add("form-label");
		agenceLabel.getStyleClass().add("form-label");
		dateDebutLabel.getStyleClass().add("form-label");
		dateRetourLabel.getStyleClass().add("form-label");
		forfaitLabel.getStyleClass().add("form-label");
		clientBox.getStyleClass().add("combo-box-form");
		agenceField.getStyleClass().add("form-textfield");
		dateDebutPicker.getStyleClass().add("date-picker-form");
		dateRetourPicker.getStyleClass().add("date-picker-form");
		forfaitField.getStyleClass().add("form-textfield");
		validerButton.getStyleClass().add("form-button");
		searchField.getStyleClass().add("form-textfield");
		sortComboBox.getStyleClass().add("combo-box-form");
		ascendingRadio.getStyleClass().add("radio-button");
		descendingRadio.getStyleClass().add("radio-button");
		
	}
    
    private void initWindow() {
        window.setTitle("🚗 Véhicules Disponibles");
        window.setScene(scene);
    }

    private void setupTable() { 
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        
        // Initialisation des colonnes
        immatriculationCol = new TableColumn<>("Immatriculation");
        marqueCol = new TableColumn<>("Marque");
        typeCol = new TableColumn<>("Type");
        categorieCol = new TableColumn<>("Catégorie");
        carburantCol = new TableColumn<>("Carburant");
        placesCol = new TableColumn<>("Places");
        forfaitJournalier = new TableColumn("Forfait Journalier");
        actionCol = new TableColumn<>("📝 Louer");

        // Configuration des cellules
        immatriculationCol.setCellValueFactory(new PropertyValueFactory<>("immatriculation"));
        marqueCol.setCellValueFactory(new PropertyValueFactory<>("marque"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        categorieCol.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        carburantCol.setCellValueFactory(new PropertyValueFactory<>("Carburant"));
        placesCol.setCellValueFactory(new PropertyValueFactory<>("nbresPlaces"));
        forfaitJournalier.setCellValueFactory(new PropertyValueFactory<>("forfaitJournalier"));
        forfaitJournalier.setCellFactory(column -> new TableCell<Vehicule, Float>() {
            @Override
            protected void updateItem(Float amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", amount)+" Dh");
                }
            }
        });


        
        // Configuration des largeurs
        immatriculationCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        marqueCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        typeCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        categorieCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        carburantCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        forfaitJournalier.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        placesCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);

        // Configuration de la colonne d'action
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button louerButton = new Button("Louer");

            {
                louerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 13px;");
                louerButton.setOnAction(event -> {
                    Vehicule selectedVehicule = getTableView().getItems().get(getIndex());
                    if (selectedVehicule != null) {
                        openLouerForm(selectedVehicule);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : louerButton);
            }
        });

        // Chargement des données
        VehiculeDAO vehiculeDAO = new VehiculeDAO();
        List<Vehicule> vehicules = vehiculeDAO.getVehiculesDisponibles();
        vehiculeList = FXCollections.observableArrayList(vehicules != null ? vehicules : Collections.emptyList());

        // Configuration des listes filtrées/triées
        filteredVehicules = new FilteredList<>(vehiculeList, p -> true);
        sortedVehicules = new SortedList<>(filteredVehicules);
        
        // Lier le comparateur de la table au sortedVehicules
        //sortedVehicules.comparatorProperty().bind(tableView.comparatorProperty());

        // Configuration de l'interface de contrôle
        HBox controlsBox = new HBox(10);
        controlsBox.setPadding(new Insets(10));
        
        searchField.setPromptText("Rechercher par marque, type ou carburant...");
        searchField.setPrefWidth(250);
        
        sortComboBox.getItems().addAll("Marque", "Type", "Carburant");
        sortComboBox.setPromptText("Trier par");
        sortComboBox.getSelectionModel().selectFirst();

        ascendingRadio.setToggleGroup(sortOrderGroup);
        descendingRadio.setToggleGroup(sortOrderGroup);
        ascendingRadio.setSelected(true);
        
        controlsBox.getChildren().addAll(searchField, sortComboBox, ascendingRadio, descendingRadio);

        // Configuration des écouteurs
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterVehicules(newVal));
        sortComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateSort());
        sortOrderGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> updateSort());

        // Ajout des colonnes à la table
        tableView.getColumns().addAll(
            immatriculationCol, 
            marqueCol, 
            typeCol, 
            categorieCol, 
            carburantCol,
            placesCol, 
            forfaitJournalier,
            actionCol
        );

        // Lier les données à la table
        tableView.setItems(sortedVehicules);

        // Ajout des composants à la racine
        root.getChildren().addAll(controlsBox, tableView);
    }

    public ListVehiculesDisponibles() {
        initWindow();
        setupTable();
        addStylesToNodes();
        window.getIcons().add(new Image("file:icone.png"));
        
        CustomStage customStage = new CustomStage();
        customStage.decorate(window, scene, "Liste des véhicules disponibles", false, 800, 500, true);
        
        window.show();
        
    }
    
    private void filterVehicules(String searchText) {
        filteredVehicules.setPredicate(vehicule -> {
            if(searchText == null || searchText.isEmpty()) return true;
            
            String lowerText = searchText.toLowerCase();
            return vehicule.getMarque().toLowerCase().contains(lowerText) ||
                   vehicule.getType().toLowerCase().contains(lowerText) ||
                   vehicule.getCarburant().toLowerCase().contains(lowerText);
        });
    }

    private void updateSort() {
        String criteria = sortComboBox.getValue();
        boolean ascending = ascendingRadio.isSelected();
        
        Comparator<Vehicule> comparator = switch(criteria) {
            case "Marque" -> Comparator.comparing(Vehicule::getMarque, String.CASE_INSENSITIVE_ORDER);
            case "Type" -> Comparator.comparing(Vehicule::getType, String.CASE_INSENSITIVE_ORDER);
            case "Carburant" -> Comparator.comparing(Vehicule::getCarburant, String.CASE_INSENSITIVE_ORDER);
            default -> null;
        };
        
        if(comparator != null) {
            if(!ascending) {
                comparator = comparator.reversed();
            }
            tableView.setItems(null); // Détacher les items
            sortedVehicules.setComparator(comparator); // Appliquer le tri
            tableView.setItems(sortedVehicules); // Rattacher les items
        }
    }
}

