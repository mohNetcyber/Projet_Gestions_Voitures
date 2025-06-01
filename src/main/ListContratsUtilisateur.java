package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Agence;
import model.Client;
import model.Contrat;
import model.Utilisateur;
import model.Vehicule;
import utils.CustomStage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import dao.AgenceDAO;
import dao.ClientDAO;
import dao.ContratDAO;
import dao.FactureDAO;
import dao.UtilisateurDAO;
import dao.VehiculeDAO;

public class ListContratsUtilisateur {
    private VBox root = new VBox(15);
    private Scene scene = new Scene(root, 800, 500);
    private Stage window = new Stage();
    private TableView<Contrat> tableView = new TableView<>();
    private TableColumn<Contrat, String> vehiculeCol = new TableColumn<>("Véhicule");
    private TableColumn<Contrat, String> dateDebutCol = new TableColumn<>("Date Début");
    private TableColumn<Contrat, String> dateRetourCol = new TableColumn<>("Date Retour");
    private TableColumn<Contrat, Double> forfaitCol = new TableColumn<>("Forfait");
    private TableColumn<Contrat, String> kmDepartCol = new TableColumn<>("Kilométrage");
    private TableColumn<Contrat, String> kmTarifCol = new TableColumn<>("Tarif Km");
    private TableColumn<Contrat, Void> actionCol = new TableColumn<>("📄 Factures");
    private TableColumn<Contrat, Void> actionDeleteCol = new TableColumn<>("🗑 Supprimer");
    
    private ObservableList<Contrat> contratList;

    public ListContratsUtilisateur() {
        initWindow();
        addStylestoNode();
        setupTable();
        CustomStage customStage = new CustomStage();
        customStage.decorate(window, scene, "📄 Contrats de l'Utilisateur ", false, 800, 500, true);
        window.show();
    }

    private void initWindow() {
        window.setTitle("");
        window.setScene(scene);
    }

    private void setupTable() {

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    	
        vehiculeCol.setCellValueFactory(new PropertyValueFactory<>("immatricule"));
        dateDebutCol.setCellValueFactory(new PropertyValueFactory<>("dateDepart"));
        dateRetourCol.setCellValueFactory(new PropertyValueFactory<>("dateRetourPrevue"));
        forfaitCol.setCellValueFactory(new PropertyValueFactory<>("forfaitJournalier"));
        kmDepartCol.setCellValueFactory(new PropertyValueFactory<>("km_depart"));
        kmTarifCol.setCellValueFactory(new PropertyValueFactory<>("tarifKm"));
        
        
        vehiculeCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        dateDebutCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        dateRetourCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        forfaitCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        kmDepartCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        kmTarifCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

        dateDebutCol.setCellFactory(col -> new TableCell<Contrat, String>() {
            @Override
            protected void updateItem(String date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    try {
                        LocalDateTime parsedDate = LocalDateTime.parse(date, formatter);
                        setText(parsedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    } catch (Exception e) {
                        setText("Format incorrect");
                        System.err.println("Erreur de parsing : " + e.getMessage());
                    }
                }
            }
        });

        dateRetourCol.setCellFactory(col -> new TableCell<Contrat, String>() {
            @Override
            protected void updateItem(String date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    try {
                        LocalDateTime parsedDate = LocalDateTime.parse(date, formatter);
                        setText(parsedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    } catch (Exception e) {
                        setText("Format incorrect");
                        System.err.println("Erreur de parsing : " + e.getMessage());
                    }
                }
            }
        });
        
        actionCol.setCellFactory(col -> new TableCell<>() {

            private final Button factureButton = new Button("Factures");

            {
                factureButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 13px;");
                factureButton.setOnAction(event -> {
                    Contrat selectedContrat = getTableView().getItems().get(getIndex());
                    int idclient = selectedContrat.getIdClient();
                    ClientDAO clientDAO = new ClientDAO();
                    Client client = clientDAO.getClientById(idclient);

                    String immatricule = selectedContrat.getImmatricule();
                    VehiculeDAO vehiculeDAO = new VehiculeDAO();
                    Vehicule vehicule = vehiculeDAO.getVehiculeByImmatricule(immatricule);

                    int idagence = selectedContrat.getIdAgenceDepart();
                    AgenceDAO agenceDAO = new AgenceDAO();
                    Agence agence = agenceDAO.getAgenceById(idagence);

                    if (selectedContrat != null) {
                        new FactureContratWindows(selectedContrat, client, vehicule, agence);
                        
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Contrat contrat = getTableView().getItems().get(getIndex());
                    FactureDAO factureDAO = new FactureDAO();
                    UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
                    Utilisateur utilisateurConnecte = utilisateurDAO.getUtilisateurConnecte();
                    boolean factureExiste = factureDAO.factureExistePourContrat(contrat.getIdContrat(), utilisateurConnecte.getId());

                    setGraphic(factureExiste ? null : factureButton);
                }
            }
        });

        
        actionDeleteCol = new TableColumn<>("🗑 Supprimer");

        actionDeleteCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-size: 13px;");
                deleteButton.setOnAction(event -> {
                    Contrat contrat = getTableView().getItems().get(getIndex());
                    
                    // Vérifier si une facture existe pour ce contrat
                    FactureDAO factureDAO = new FactureDAO();
                    UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
                    Utilisateur utilisateurConnecte = utilisateurDAO.getUtilisateurConnecte();
                    boolean factureExiste = factureDAO.factureExistePourContrat(contrat.getIdContrat(), utilisateurConnecte.getId()  );

                    if (!factureExiste) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Suppression impossible");
                        alert.setHeaderText("Contrat sans facture");
                        alert.setContentText("Vous ne pouvez pas supprimer ce contrat tant qu'aucune facture n'a été générée.");
                        alert.showAndWait();
                        return;
                    }

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirmation de suppression");
                    confirm.setHeaderText("Supprimer le contrat ?");
                    confirm.setContentText("Êtes-vous sûr de vouloir supprimer ce contrat ?");
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                        	VehiculeDAO vehiculeDAO = new VehiculeDAO();
                            ContratDAO contratDAO = new ContratDAO();
                            contratDAO.supprimerContrat(contrat.getIdContrat());
                            vehiculeDAO.setVehiculeDisponible(contrat.getImmatricule());
                            contratList.remove(contrat);
                            tableView.refresh();
                        } 
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });
		actionDeleteCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);

        tableView.getColumns().addAll(vehiculeCol, dateDebutCol, dateRetourCol, forfaitCol,kmDepartCol, kmTarifCol, actionCol, actionDeleteCol);

        // Fetch connected user and their contracts
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        Utilisateur utilisateurConnecte = utilisateurDAO.getUtilisateurConnecte();
        ContratDAO contratDAO = new ContratDAO();
        contratList = FXCollections.observableArrayList(contratDAO.getContratsByUserId(utilisateurConnecte.getId()));
        tableView.setItems(contratList);

        root.getChildren().add(tableView);
    }
    

    private void showFacture(Contrat contrat) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Facture");
        alert.setHeaderText("Facture pour le contrat");
        alert.setContentText("Détails de la facture pour le contrat ID: " + contrat.getIdContrat());
        alert.showAndWait();
    }
    
    private void addStylestoNode() {
    	scene.getStylesheets().add("css/style.css");
		root.getStyleClass().add("root-liste");
		tableView.getStyleClass().add("table-view");
		
    }
}
