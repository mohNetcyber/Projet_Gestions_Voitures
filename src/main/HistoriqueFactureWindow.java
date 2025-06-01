package main;

import javafx.beans.property.SimpleStringProperty;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Client;
import model.Contrat;
import model.Facture;
import model.Utilisateur;
import utils.CustomStage;
import dao.ContratDAO;
import dao.FactureDAO;
import dao.UtilisateurDAO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoriqueFactureWindow {
	Stage window = new Stage();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
    private Map<Integer, String> clientNamesCache = new HashMap<>();
    
    private void preloadClientData(List<Facture> factures) {
        FactureDAO factureDAO = new FactureDAO();
        ContratDAO contratDAO = new ContratDAO();
        
        for (Facture facture : factures) {
            try {
                Contrat contrat = factureDAO.getContratByIdFacture(facture.getID_Facture());
                if (contrat != null) {
                    Client client = contratDAO.getClientByIdContrat(contrat.getIdContrat());
                    if (client != null) {
                        clientNamesCache.put(facture.getID_Facture(), 
                            facture.getNomPrenomClient());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public HistoriqueFactureWindow() {
        TableView<Facture> tableView = new TableView<>();
        tableView.getStyleClass().add("table-view");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        
        
        
        // Define columns
        TableColumn<Facture, Integer> idContratCol = new TableColumn<>("ID Contrat");
        idContratCol.setCellValueFactory(new PropertyValueFactory<>("ID_Contrat"));
        idContratCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        
        TableColumn<Facture, String> dateFactureCol = new TableColumn<>("Date Facture");
        dateFactureCol.setCellValueFactory(new PropertyValueFactory<>("date_Facture")); // Add this line
        dateFactureCol.setCellFactory(col -> new TableCell<Facture, String>() {
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
                        setText(date); // Show raw date if parsing fails
                        System.err.println("Erreur de parsing : " + e.getMessage());
                    }
                }
            }
        });
        dateFactureCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        
        TableColumn<Facture, String> nomPrenomClient = new TableColumn<>("Nom et Prénom du client");
        nomPrenomClient.setCellValueFactory(cellData -> {
            Facture facture = cellData.getValue();
            String clientName = clientNamesCache.get(facture.getID_Facture());
            return new SimpleStringProperty(clientName != null ? clientName : "N/A");
        });


        nomPrenomClient.setMaxWidth(1f * Integer.MAX_VALUE * 25);

        
        TableColumn<Facture, Integer> nbreJoursCol = new TableColumn<>("Nombres de jours");
        nbreJoursCol.setCellValueFactory(new PropertyValueFactory<>("nbresJours"));
        nbreJoursCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        
        
        TableColumn<Facture, Integer> kmParcouruCol = new TableColumn<>("Km Parcourru");
        kmParcouruCol.setCellValueFactory(new PropertyValueFactory<>("km_parcouru"));
        kmParcouruCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        
        TableColumn<Facture, Double> montantTotalCol = new TableColumn<>("Montant Total");
        montantTotalCol.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        montantTotalCol.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        
        // Add columns to the table
        tableView.getColumns().addAll(idContratCol, dateFactureCol, nomPrenomClient, nbreJoursCol, kmParcouruCol, montantTotalCol);
        
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        Utilisateur utilisateurConnecte = utilisateurDAO.getUtilisateurConnecte();
        // Fetch data and populate the table
        try {
        	FactureDAO factureDAO = new FactureDAO();
        	List<Facture> factures = factureDAO.getFacturesByUser(utilisateurConnecte.getId());
        	preloadClientData(factures);
        	ObservableList<Facture> observableFactures = FXCollections.observableArrayList(factures);
        	tableView.setItems(observableFactures);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Layout
        VBox vbox = new VBox(tableView);
        Scene scene = new Scene(vbox, 800, 400);
        scene.getStylesheets().add("css/style.css");
        window.setTitle("Factures");
        window.setScene(scene);
        
        CustomStage customStage = new CustomStage();
        customStage.decorate(window, scene, "Historique des factures", false, 900, 500, true);
        window.getIcons().add(new Image("file: historique.png"));
        window.show();
    }
}

