package main;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import model.Agence;
import dao.AgenceDAO;

public class ManageAgenceWindow {
	
    private VBox root = new VBox();
    private ScrollPane scrollPane = new ScrollPane(root);
    private Scene scene = new Scene(scrollPane, 1100, 650);
    private Stage window = new Stage();
    private TableView<Agence> tableView = new TableView<>();
    private ObservableList<Agence> agenceList;
    private TextField searchField;
    private FilteredList<Agence> filteredAgences;
    private SortedList<Agence> sortedAgences;
    private Button backButton = new Button("Retour");

    private void initWindow() {
    	root.setSpacing(20);
    	scrollPane.setFitToWidth(true);
    	scrollPane.setFitToHeight(true);
    	scene.getStylesheets().add(getClass().getResource("/css/styleAdmin.css").toExternalForm()); 
        window.setTitle("Liste des Agences");
        window.setScene(scene);
    }
    
    
    private Agence openAddAgenceForm()
    {	
    	ScrollPane scrollPane = new ScrollPane();
        Stage addWindow = new Stage();
        VBox root = new VBox(15);
        root.getStyleClass().add("form-root");
        root.setPadding(new Insets(20));
        scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        Scene scene = new Scene(scrollPane, 450, 350);
        scene.getStylesheets().add(getClass().getResource("/css/styleAdmin.css").toExternalForm());
        addWindow.setTitle("🏢 Ajouter une Agence");

        // Champs de saisie avec labels
        Label nomLabel = new Label("🏢 Nom de l'Agence :");
        nomLabel.getStyleClass().add("form-label");
        TextField nomField = new TextField();
        nomField.setPromptText("Ex: Agence Fes Monfleurit");
        nomField.getStyleClass().add("form-field");
        
        Label adresseLabel = new Label("📍 Adresse :");
        adresseLabel.getStyleClass().add("form-label");
        TextField adresseField = new TextField();
        adresseField.setPromptText("Ex: Avenue Beyrouth");
        adresseField.getStyleClass().add("form-field");
        
        Label villeLabel = new Label("🌍 Ville :");
        villeLabel.getStyleClass().add("form-label");
        TextField villeField = new TextField();
        villeField.setPromptText("Ex: Fes");
        villeField.getStyleClass().add("form-field");
        
        Label telephoneLabel = new Label("📞 Téléphone :");
        telephoneLabel.getStyleClass().add("form-label");
        TextField telephoneField = new TextField();
        telephoneField.setPromptText("Ex: 0778 987416");
        telephoneField.getStyleClass().add("form-field");
        
        Label emailLabel = new Label("📧 Email :");
        emailLabel.getStyleClass().add("form-label");
        TextField emailField = new TextField();
        emailField.setPromptText("Ex: agence@location.com");
        emailField.getStyleClass().add("form-field");
        // Bouton d'ajout stylisé
        Button addButton = new Button("✅ Ajouter");
        addButton.getStyleClass().add("form-button");
        
        addButton.setOnAction(event -> {
            Agence newAgence = new Agence(
                0, // L'ID sera généré en base de données
                nomField.getText(),
                adresseField.getText(),
                villeField.getText(),
                telephoneField.getText(),
                emailField.getText()
            );
            
            AgenceDAO agenceDAO = new AgenceDAO();

            if (agenceDAO.ajouterAgence(newAgence)) {
                showAlert("Succès", "Agence ajoutée avec succès !");
                agenceList.add(newAgence); 
                addWindow.close();
            } else {
                showAlert("Erreur", "Échec de l'ajout de l'agence.");
            }

        });

        root.getChildren().addAll(nomLabel, nomField, adresseLabel, adresseField, villeLabel, villeField, telephoneLabel, telephoneField, emailLabel, emailField, addButton);
        addWindow.setScene(scene);
        addWindow.getIcons().add(new Image("file:admin.png"));
        addWindow.initModality(Modality.APPLICATION_MODAL);

        addWindow.showAndWait();
        return null;
    }

    
    
    private Agence openModifyAgenceForm(Agence agence) {
        Stage editWindow = new Stage();
        VBox root = new VBox(15);
        root.setCenterShape(true);
        root.setPadding(new Insets(20));
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.getStyleClass().add("scroll-pane");
        Scene scene = new Scene(scrollPane, 400, 350);
        editWindow.setTitle("🏢 Modifier Agence");

        // Labels et champs de saisie avec icônes
        Label idLabel = new Label("🆔 ID Agence :");
        idLabel.getStyleClass().add("form-label");
        TextField idField = new TextField(String.valueOf(agence.getIdAgence()));
        idField.setDisable(true); // ID non modifiable
        idField.getStyleClass().add("form-field");
        
        Label nomLabel = new Label("🏢 Nom de l'Agence :");
        nomLabel.getStyleClass().add("form-label");
        TextField nomField = new TextField(agence.getNomAgence());
        nomField.getStyleClass().add("form-field");
        
        Label adresseLabel = new Label("📍 Adresse :");
        adresseLabel.getStyleClass().add("form-label");
        TextField adresseField = new TextField(agence.getAdresse());
        adresseField.getStyleClass().add("form-field");
        
        Label villeLabel = new Label("🌍 Ville :");
        villeLabel.getStyleClass().add("form-label");
        TextField villeField = new TextField(agence.getVille());
        villeField.getStyleClass().add("form-field");
        
        Label telephoneLabel = new Label("📞 Téléphone :");
        telephoneLabel.getStyleClass().add("form-label");
        TextField telephoneField = new TextField(agence.getTelephone());
        telephoneField.getStyleClass().add("form-field");
        
        Label emailLabel = new Label("📧 Email :");
        emailLabel.getStyleClass().add("form-label");
        TextField emailField = new TextField(agence.getEmail());
        emailField.getStyleClass().add("form-field");

        // Bouton de sauvegarde stylisé
        Button saveButton = new Button("✅ Sauvegarder");
        saveButton.getStyleClass().add("form-button");
        saveButton.setOnAction(event -> {
        	if (nomField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty()) {
        	    showAlert("Erreur", "Le nom et l'email sont obligatoires.");
        	    return;
        	}

            agence.setNomAgence(nomField.getText());
            agence.setAdresse(adresseField.getText());
            agence.setVille(villeField.getText());
            agence.setTelephone(telephoneField.getText());
            agence.setEmail(emailField.getText());
            tableView.refresh();
            editWindow.close(); // Fermer après modification
        });

        root.getChildren().addAll(idLabel, idField, nomLabel, nomField, adresseLabel, adresseField, villeLabel, villeField, telephoneLabel, telephoneField, emailLabel, emailField, saveButton);
        root.getStyleClass().add("form-root");
        scene.getStylesheets().add(getClass().getResource("/css/styleAdmin.css").toExternalForm());
        editWindow.setScene(scene);
        editWindow.initModality(Modality.APPLICATION_MODAL);
        editWindow.getIcons().add(new Image("file:admin.png"));
        editWindow.showAndWait();

        return agence; // Retourne l'agence modifiée
    }

    
    private void setupTable() {

    	tableView = new TableView<>();
    	tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    	VBox.setVgrow(tableView, Priority.ALWAYS); // Add this line
    	tableView.getStyleClass().add("table-view");
    	searchField = new TextField();
	    searchField.setPromptText("🔍 Rechercher une agence...");
	    searchField.getStyleClass().add("search-field");
	    VBox.setMargin(searchField, new Insets(0, 0, 10, 0));    
    	
        TableColumn<Agence, Integer> idCol = new TableColumn<>("ID");
        TableColumn<Agence, String> nomCol = new TableColumn<>("Nom");
        TableColumn<Agence, String> adresseCol = new TableColumn<>("Adresse");
        TableColumn<Agence, String> villeCol = new TableColumn<>("Ville");
        TableColumn<Agence, String> telephoneCol = new TableColumn<>("Téléphone");
        TableColumn<Agence, String> emailCol = new TableColumn<>("Email");

        idCol.setCellValueFactory(new PropertyValueFactory<>("idAgence"));
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nomAgence"));
        adresseCol.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        villeCol.setCellValueFactory(new PropertyValueFactory<>("ville"));
        telephoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        idCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        nomCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        adresseCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        villeCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        emailCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        
        tableView.getColumns().addAll(idCol, nomCol, adresseCol, villeCol, telephoneCol, emailCol);

        // Charger les agences
        AgenceDAO agenceDAO = new AgenceDAO();
        agenceList = FXCollections.observableArrayList(agenceDAO.getAllAgences());
        filteredAgences = new FilteredList<>(agenceList, p -> true);
        searchField.setPromptText("Recherche par nom, agence...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredAgences.setPredicate(agence -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseSearch = newValue.toLowerCase();
                
                return agence.getNomAgence().toLowerCase().contains(lowerCaseSearch)
                    || agence.getVille().toLowerCase().contains(lowerCaseSearch)
                    || agence.getAdresse().toLowerCase().contains(lowerCaseSearch)
                    || agence.getEmail().toLowerCase().contains(lowerCaseSearch)
                    || agence.getTelephone().contains(lowerCaseSearch);
            });
        });
        
        // Bind sorted list to table
        sortedAgences = new SortedList<>(filteredAgences);
        sortedAgences.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedAgences);
        
        Label titleLabel = new Label("Liste Agences");
        titleLabel.setId("page-title");
        root.getChildren().addAll(titleLabel, searchField, tableView);
    }
    
    private void setupButtons() {
        Button addButton = new Button("Ajouter");
        Button deleteButton = new Button("Supprimer");
        Button modifyButton = new Button("Modifier");
        addButton.getStyleClass().add("action-button");
        modifyButton.getStyleClass().add("action-button");
        deleteButton.getStyleClass().add("action-button");
        
        // Add Button Logic
        addButton.setOnAction(e -> {
            Agence newAgence = openAddAgenceForm();
            if (newAgence != null) {
                AgenceDAO agenceDAO = new AgenceDAO();
                boolean isAdded = agenceDAO.ajouterAgence(newAgence);
                if (isAdded) {
                    agenceList.add(newAgence);
                    showAlert("Succès", "Agence ajoutée avec succès !");
                    tableView.refresh();
                } else {
                    showAlert("Erreur", "Échec de l'ajout de l'agence.");
                }
            }
        });

        // Delete Button Logic
        deleteButton.setOnAction(e -> {
            Agence selectedAgence = tableView.getSelectionModel().getSelectedItem();
            if (selectedAgence != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Êtes-vous sûr de vouloir supprimer cette agence ?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        AgenceDAO agenceDAO = new AgenceDAO();
                        boolean isDeleted = agenceDAO.supprimerAgence(selectedAgence.getIdAgence());
                        if (isDeleted) {
                            agenceList.remove(selectedAgence);
                            tableView.refresh();
                            showAlert("Succès", "Agence supprimée avec succès !");
                        } else {
                            showAlert("Erreur", "Échec de la suppression de l'agence.");
                        }
                    }
                });
            } else {
                showAlert("Erreur", "Veuillez sélectionner une agence à supprimer.");
            }
        });

        // Modify Button Logic
        modifyButton.setOnAction(e -> {
            Agence selectedAgence = tableView.getSelectionModel().getSelectedItem();
            if (selectedAgence != null) {
                Agence updatedAgence = openModifyAgenceForm(selectedAgence);
                if (updatedAgence != null) {
                    AgenceDAO agenceDAO = new AgenceDAO();
                    boolean isUpdated = agenceDAO.updateAgence(updatedAgence);
                    if (isUpdated) {
                        int index = agenceList.indexOf(selectedAgence);
                        agenceList.set(index, updatedAgence);
                        tableView.refresh();
                        showAlert("Succès", "Agence modifiée avec succès !");
                    } else {
                        showAlert("Erreur", "Échec de la modification de l'agence.");
                    }
                }
            } else {
                showAlert("Erreur", "Veuillez sélectionner une agence à modifier.");
            }
        });
        backButton.setId("back-button");
        backButton.setOnAction(e -> {
            new AdminDashboard().afficher();
            window.close();
        });
        HBox buttonBox = new HBox(10, addButton, modifyButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);
        VBox.setMargin(buttonBox, new Insets(10, 0, 10, 0));
        VBox.setMargin(backButton, new Insets(10, 0, 0, 0));
        root.getChildren().addAll(buttonBox, backButton);
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
    	scene.getStylesheets().add(getClass().getResource("/css/styleAdmin.css").toExternalForm());;
    	root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("main-layout");
		scrollPane.getStyleClass().add("scroll-pane");
		tableView.getStyleClass().add("table-view");
	}


    public ManageAgenceWindow(Stage primaryStage) {
    	this.window = primaryStage;
        initWindow();
        setupTable();
        setupButtons();
        addStylesToNodes();
        window.getIcons().add(new Image("file:admin.png"));
        window.show();
    }
}
