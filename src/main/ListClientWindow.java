package main;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import model.Client;
import model.Utilisateur;
import utils.CustomStage;
import dao.ClientDAO;
import dao.UtilisateurDAO;

public class ListClientWindow {
    private VBox root = new VBox(15);
    private Scene scene = new Scene(root, 900, 600);
    private Stage window = new Stage();
    private TableView<Client> tableView = new TableView<>();
    private ObservableList<Client> clientList;

    private Button addButton = new Button("➕ Ajouter Client");
    private Button modifyButton = new Button("✏️ Modifier");
    private Button deleteButton = new Button("🗑️ Supprimer");
    
    
    
    public ListClientWindow() {
        initializeWindow();
        loadClients();
        setupTable();
        setupButtons();
        applyStyles();
        
        CustomStage customStage = new CustomStage();
        customStage.decorate(window, scene, "Liste des Clients", false, 900, 600, true);
        
        window.show();
    }

    private void initializeWindow() {
        window.setTitle("Liste des Clients");
        window.setScene(scene);
        window.getIcons().add(new Image("file:icone.png"));
    }

    private void loadClients() {
        ClientDAO clientDAO = new ClientDAO();
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        Utilisateur utilisateur = utilisateurDAO.getUtilisateurConnecte();

        if (utilisateur != null) {
            clientList = FXCollections.observableArrayList(clientDAO.getClientsByUserId(utilisateur.getId()));
            tableView.setItems(clientList);
        } else {
            showAlert("Erreur", "Aucun utilisateur connecté.");
        }
    }

    private void setupTable() {
    	tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

    	 TextField searchField = new TextField();
	    searchField.setPromptText("🔍 Rechercher un client...");
	    searchField.getStyleClass().add("search-field-cl");
	    
	    // Create title label
	    Label titleLabel = new Label("Liste des Clients");
	    titleLabel.getStyleClass().add("title-label");
	    
	    
	    FilteredList<Client> filteredClients = new FilteredList<>(clientList, p -> true);
	    searchField.textProperty().addListener((observable, oldValue, newValue) -> {
	        filteredClients.setPredicate(client -> {
	            if (newValue == null || newValue.isEmpty()) {
	                return true;
	            }

	            String lowerCaseSearch = newValue.toLowerCase();

	            return client.getNom().toLowerCase().contains(lowerCaseSearch)
	                || client.getPrenom().toLowerCase().contains(lowerCaseSearch)
	                || client.getAdresse().toLowerCase().contains(lowerCaseSearch)
	                || client.getTelephone().contains(lowerCaseSearch);
	        });
	    });
	    
	    SortedList<Client> sortedClients = new SortedList<>(filteredClients);
	    sortedClients.comparatorProperty().bind(tableView.comparatorProperty());
	    tableView.setItems(sortedClients);

    	
        TableColumn<Client, String> nomCol = new TableColumn<>("Nom");
        TableColumn<Client, String> prenomCol = new TableColumn<>("Prénom");
        TableColumn<Client, String> adresseCol = new TableColumn<>("Adresse");
        TableColumn<Client, String> telephoneCol = new TableColumn<>("Téléphone");

        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        adresseCol.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        telephoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        
        nomCol.setMaxWidth(1f * Integer.MAX_VALUE * 25); // 25%
        prenomCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        adresseCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        telephoneCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);

        
        tableView.getColumns().addAll(nomCol, prenomCol, adresseCol, telephoneCol);
        root.getChildren().addAll(titleLabel, searchField, tableView);

    }

    private void setupButtons() {
        HBox buttonBox = new HBox(10, addButton, modifyButton, deleteButton);
        root.getChildren().add(buttonBox);

        addButton.setOnAction(e -> {
            new FormClientWindow(); // Doit mettre à jour clientList si un client est ajouté
        });

        modifyButton.setOnAction(e -> {
            Client selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                openEditForm(selected);
                tableView.refresh();
            }
        });

        deleteButton.setOnAction(e -> {
            Client selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer ce client ?", ButtonType.OK, ButtonType.CANCEL);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        ClientDAO clientDAO = new ClientDAO();
                        if (clientDAO.supprimerClient(selected.getId())) {
                            clientList.remove(selected);
                        } else {
                            showAlert("Erreur", "Échec de la suppression.");
                        }
                    }
                });
            }
        });
    }

    private void openEditForm(Client client) {
        Stage editStage = new Stage();
        editStage.getIcons().add(new Image("file:icone.png"));
        VBox form = new VBox(10);
        form.getStyleClass().add("form-root");
        form.setPadding(new Insets(20));

        TextField nomField = new TextField(client.getNom());
        nomField.getStyleClass().add("form-textfield");
        TextField prenomField = new TextField(client.getPrenom());
        prenomField.getStyleClass().add("form-textfield");
        TextField adresseField = new TextField(client.getAdresse());
        adresseField.getStyleClass().add("form-textfield");
        TextField telephoneField = new TextField(client.getTelephone());
        telephoneField.getStyleClass().add("form-textfield");

        Button saveBtn = new Button("✅ Sauvegarder");
        saveBtn.getStyleClass().add("form-button");
        saveBtn.setOnAction(e -> {
            client.setNom(nomField.getText());
            client.setPrenom(prenomField.getText());
            client.setAdresse(adresseField.getText());
            client.setTelephone(telephoneField.getText());

            ClientDAO dao = new ClientDAO();
            if (dao.modifierClient(client)) {
                tableView.refresh();
                editStage.close();
            } else {
                showAlert("Erreur", "La modification a échoué.");
            }
        });

        form.getChildren().addAll(
            new Label("Nom :"), nomField,
            new Label("Prénom :"), prenomField,
            new Label("Adresse :"), adresseField,
            new Label("Téléphone :"), telephoneField,
            saveBtn
        );

        Scene editScene = new Scene(form, 400, 400);
        editScene.getStylesheets().add("css/style.css");
        editStage.setScene(editScene);
        editStage.setTitle("Modifier Client");
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.initOwner(window);
        
        CustomStage customStage = new CustomStage();
        customStage.decorate(editStage, editScene, "Modifier Client", false, 400, 600, true);
        
        editStage.showAndWait();
    }

    private void applyStyles() {
        scene.getStylesheets().add("css/style.css");
        root.getStyleClass().add("root-liste");
        tableView.getStyleClass().add("table-view");
        addButton.getStyleClass().add("button-table");
        modifyButton.getStyleClass().add("button-table");
        deleteButton.getStyleClass().add("button-table");
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