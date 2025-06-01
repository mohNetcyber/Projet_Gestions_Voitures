package main;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import dao.ClientDAO;
import dao.ContratDAO;
import model.Client;
import model.Contrat;
import model.Utilisateur;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.collections.FXCollections;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

public class ManageContratsPage {

    private final ContratDAO contratDAO = new ContratDAO();
    private final TableView<Contrat> contractTable = new TableView<>();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");



    public void display(Stage primaryStage) {
        configureTable();

        Label titleLabel = new Label("Gestion des Contrats");
        titleLabel.setId("page-title");

        Button editButton = createActionButton("Modifier");
        Button deleteButton = createActionButton("Supprimer");
        Button refreshButton = createActionButton("Actualiser");
        Button backButton = new Button("Retour");
        backButton.setId("back-button");

        HBox buttonsBox = new HBox(10, editButton, deleteButton, refreshButton);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(10));

        editButton.setOnAction(e -> handleEditAction());
        deleteButton.setOnAction(e -> handleDeleteAction());
        refreshButton.setOnAction(e -> refreshTable());
        backButton.setOnAction(e -> {
            new AdminDashboard().afficher();
            primaryStage.close();
        });

        VBox root = new VBox(15, titleLabel, contractTable, buttonsBox, backButton);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("main-layout");

        Scene scene = new Scene(root, 1100, 650);
        scene.getStylesheets().add(getClass().getResource("/css/styleAdmin.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Gestion des Contrats");

        refreshTable();
    }

    private void configureTable() {
        contractTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        contractTable.getStyleClass().add("table-view");

        addColumn("ID", c -> new SimpleIntegerProperty(c.getIdContrat()).asObject());
        addColumn("Client ID", c -> new SimpleIntegerProperty(c.getIdClient()).asObject());
        addColumn("Client", c -> {
            Client client = contratDAO.getClientByIdClient(c.getIdClient());
            return new SimpleStringProperty(client != null ? 
                client.getNom() + " " + client.getPrenom() : "N/A");
        });
        addColumn("Immatriculation", c -> new SimpleStringProperty(c.getImmatricule()));
        addColumn("Date Départ", c -> new SimpleStringProperty(formatDate(c.getDateDepart())));
        addColumn("Date Retour", c -> new SimpleStringProperty(formatDate(c.getDateRetourPrevue())));
        addColumn("Forfait (DH)", c -> new SimpleDoubleProperty(c.getForfaitJournalier()).asObject());
        addColumn("Km Départ", c -> new SimpleIntegerProperty(c.getKm_depart()).asObject());
        addColumn("Tarif Km", c -> new SimpleDoubleProperty(c.getTarifKm()).asObject());
        addColumn("Login de l'utilisateur ", c -> {
            ClientDAO clientDAO = new ClientDAO();
            Utilisateur utilisateur = clientDAO.getUserByClientId(c.getIdClient());
            if (utilisateur != null) {
                return new SimpleStringProperty(utilisateur.getLogin());
            }
            return new SimpleStringProperty("N/A");
        });
    }
    
    private <T> TableColumn<Contrat, T> addColumn(String title, 
    	    javafx.util.Callback<Contrat, ObservableValue<T>> mapper) {
    	    TableColumn<Contrat, T> column = new TableColumn<>(title);
    	    column.setCellValueFactory(cellData -> {
    	        if (cellData.getValue() != null) {
    	            return mapper.call(cellData.getValue());
    	        }
    	        return null;
    	    });
    	    column.setPrefWidth(100);
    	    contractTable.getColumns().add(column);
    	    return column;
    	}

    private String formatDate(String dateStr) {
        try {
            // Accepte un format de type "2025-05-16 00:00:00.0"
            return LocalDate.parse(dateStr.split(" ")[0]).format(formatter);
        } catch (Exception e) {
            return dateStr;
        }
    }


   

    private void refreshTable() {
        contractTable.setItems(FXCollections.observableArrayList(contratDAO.getAllContrats()));
    }

    private void handleEditAction() {
        Contrat selected = contractTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openEditDialog(selected);
        } else {
            showAlert("Aucun contrat sélectionné", "Veuillez sélectionner un contrat à modifier.");
        }
    }

    private void handleDeleteAction() {
        Contrat selected = contractTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            boolean confirmed = showConfirmation("Confirmation", "Supprimer ce contrat ?");
            if (confirmed) {
                contratDAO.supprimerContrat(selected.getIdContrat());
                refreshTable();
            }
        } else {
            showAlert("Aucun contrat sélectionné", "Veuillez sélectionner un contrat à supprimer.");
        }
    }

    private void openEditDialog(Contrat contrat) {
        Stage dialogStage = new Stage();
        VBox dialogVBox = new VBox(10);
        dialogVBox.setPadding(new Insets(10));
        dialogVBox.getStyleClass().add("form-root");
        
        String pattern = "yyyy-MM-dd";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        StringConverter<LocalDate> converter = new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, formatter) : null;
            }
        };

        
        // Champs avec DatePicker
        DatePicker dateDepartPicker = new DatePicker();
        dateDepartPicker.setConverter(converter);
        dateDepartPicker.setPromptText(pattern);

        DatePicker dateRetourPicker = new DatePicker();
        dateRetourPicker.setConverter(converter);
        dateRetourPicker.setPromptText(pattern);

     // Parsing individuel de chaque date
        LocalDate dateDepart = tryParseDate(contrat.getDateDepart());
        LocalDate dateRetour = tryParseDate(contrat.getDateRetourPrevue());

        dateDepartPicker.setValue(dateDepart != null ? dateDepart : LocalDate.now());
        dateRetourPicker.setValue(dateRetour != null ? dateRetour : LocalDate.now());


        // Champs numériques
        TextField forfaitField = new TextField(String.valueOf(contrat.getForfaitJournalier()));
        TextField kmDepartField = new TextField(String.valueOf(contrat.getKm_depart()));
        TextField tarifKmField = new TextField(String.valueOf(contrat.getTarifKm()));

        styleFormFields(forfaitField, kmDepartField, tarifKmField);
        dateDepartPicker.getStyleClass().add("form-field");
        dateRetourPicker.getStyleClass().add("form-field");

        Button saveButton = new Button("Enregistrer");
        saveButton.getStyleClass().add("form-button");
        saveButton.setOnAction(e -> {
            try {
                if (dateDepartPicker.getValue() == null || dateRetourPicker.getValue() == null) {
                    throw new IllegalArgumentException("Les dates ne peuvent pas être vides.");
                }

                double forfait = Double.parseDouble(forfaitField.getText());
                int kmDepart = Integer.parseInt(kmDepartField.getText());
                double tarifKm = Double.parseDouble(tarifKmField.getText());

                contrat.setDateDepart(dateDepartPicker.getValue().toString());
                contrat.setDateRetourPrevue(dateRetourPicker.getValue().toString());
                contrat.setForfaitJournalier(forfait);
                contrat.setKm_depart(kmDepart);
                contrat.setTarifKm(tarifKm);

                if(contratDAO.modifierContrat(contrat)) {

                    refreshTable();
                    showAlert("Succès :", "Contrat modifié !");
                };
                dialogStage.close();

            } catch (NumberFormatException ex) {
                showAlert("Erreur de saisie", "Les champs 'Forfait', 'Km départ' et 'Tarif Km' doivent être numériques.");
            } catch (IllegalArgumentException ex) {
                showAlert("Champ invalide", ex.getMessage());
            }
        });

        dialogVBox.getChildren().addAll(
            createLabel("Date départ:"), dateDepartPicker,
            createLabel("Date retour prévue:"), dateRetourPicker,
            createLabel("Forfait journalier:"), forfaitField,
            createLabel("Kilométrage départ:"), kmDepartField,
            createLabel("Tarif kilométrique:"), tarifKmField,
            saveButton
        );

        ScrollPane scrollPane = new ScrollPane(dialogVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");

        Scene dialogScene = new Scene(scrollPane, 400, 500);
        dialogScene.getStylesheets().add("/css/styleAdmin.css");
        dialogStage.setScene(dialogScene);
        dialogStage.setTitle("Modifier Contrat");
        dialogStage.getIcons().add(new Image("file:admin.png"));
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.show();
    }
    
    private LocalDate tryParseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr.split(" ")[0]);
        } catch (Exception e) {
            return null;
        }
    }


    private void styleFormFields(TextField... fields) {
        for (TextField field : fields) {
            field.getStyleClass().add("form-field");
        }
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("action-button");
        return button;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}
