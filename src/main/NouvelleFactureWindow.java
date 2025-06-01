package main;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import dao.AgenceDAO;
import dao.ClientDAO;
import dao.ContratDAO;
import dao.FactureDAO;
import dao.UtilisateurDAO;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Agence;
import model.Client;
import model.Contrat;
import model.Facture;
import model.Utilisateur;
import model.Vehicule;
import utils.CustomStage;

public class NouvelleFactureWindow {
	private int idClient;
	private String immatriculeVehicule;
	private int nombreJours;
    private double montantJour;
    private double montantTotal;
    
    private int userID;
    private FactureCalculPane facturePane = new FactureCalculPane(new Contrat());
    
    private ScrollPane scrollPane = new ScrollPane();
    private Contrat contrat = null;

    private Client client = new Client();
    private Vehicule vehicule = new Vehicule();
    private Agence agenceDepart = new Agence();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
    private Stage window = new Stage();
    private VBox root = new VBox(15);
    private VBox v1 = new VBox(15);
    private VBox v2 = new VBox(15);
    private HBox hbox = new HBox(15);
    private HBox hboxTete = new HBox(15);
    private AgenceInfoPane agencePane;
    private ClientInfoPane clientInfoPane =new ClientInfoPane(null);
    private VehiculeInfoPane vehiculeInfoPane = new VehiculeInfoPane(null);

    public NouvelleFactureWindow(Utilisateur utilisateur) {
    	scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);  // Très important !
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Bloque scroll horizontal

        scrollPane.getStyleClass().add("scroll-pane-facture");
        root.getStyleClass().add("vbox-facture");
        scrollPane.setContent(root);
        
        window.setTitle("Nouvelle Facture");
        
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        Utilisateur utilisateurConnecte = utilisateurDAO.getUtilisateurConnecte();
        userID = utilisateurConnecte.getId();
        
        contrat = new Contrat();
        // Set default date to avoid null pointer
        contrat.setDateDepart(LocalDateTime.now().format(formatter));
        
        // Initialize agencePane with non-null values
        agencePane = new AgenceInfoPane(agenceDepart, contrat, new ArrayList<>());
        
        
        ComboBox<Client> clientComboBox = new ComboBox<>();
        clientComboBox.setPromptText("Sélectionner un client");
        ClientDAO clientDAO = new ClientDAO();
        clientComboBox.getItems().addAll(clientDAO.getClientsByUserId(utilisateur.getId()));
        
        ComboBox<Vehicule> vehiculeComboBox = new ComboBox<>();
        vehiculeComboBox.setPromptText("Sélectionner une voiture louée");

        // Date de retour réelle
        DatePicker dateRetourPicker = new DatePicker();
        dateRetourPicker.setPromptText("Date de retour réelle");

        // Km retour
        TextField kmRetourField = new TextField();
        kmRetourField.setPromptText("Kilométrage d'arrivée");

        // Boutons
        Button calculerButton = new Button("Calculer");
        Button sauvegarderButton = new Button("Sauvegarder + PDF");

        // Event Handlers
        clientComboBox.setOnAction(event -> {
            Client selectedClient = clientComboBox.getValue();
            if (selectedClient != null) {
            	clientInfoPane.update(selectedClient);
                // Populate contratComboBox based on the selected client
                vehiculeComboBox.getItems().clear();
                idClient = selectedClient.getId();
                client = clientDAO.getClientById(idClient);
                ContratDAO contratDAO = new ContratDAO();
                vehiculeComboBox.getItems().addAll(contratDAO.getVehiculeByClientId(idClient));
            }
        });
        
        vehiculeComboBox.setOnAction(event -> {
            Vehicule selectedVehicule = vehiculeComboBox.getValue();
            if (selectedVehicule != null) {
                vehicule = selectedVehicule;
                vehiculeInfoPane.update(selectedVehicule);
                immatriculeVehicule = selectedVehicule.getImmatriculation();
                
                ContratDAO contratDAO = new ContratDAO();
                Contrat newContrat = contratDAO.getContartByIdClientImmatricule(idClient, immatriculeVehicule);
                if (newContrat != null) {
                    contrat = newContrat;
                    
                    AgenceDAO agenceDAO = new AgenceDAO();
                    agenceDepart = agenceDAO.getAgenceById(contrat.getIdAgenceDepart());
                    List<Agence> agences = agenceDAO.getAllAgences();
                    
                    agencePane.update(agenceDepart, contrat, agences);
                    facturePane.update(contrat);
                }
            }
        });

        Label title = new Label("Facture du contrat #" + contrat.getIdContrat());
        
        Label dateLabel = new Label("Date Fatcure : ");
        DatePicker datePickerFacture = new DatePicker();
        hboxTete.getChildren().addAll(dateLabel, datePickerFacture);

        calculerButton.getStyleClass().add("button-facture");
        calculerButton.setOnAction(e -> {
        	
            try {
                if (agencePane.kmArriveField.getText().isEmpty()) {
                    showAlert("Erreur", "Veuillez entrer le kilométrage d'arrivée.");
                    return;
                }
                int kmArrive = Integer.parseInt(agencePane.kmArriveField.getText());

                if (kmArrive < contrat.getKm_depart()) {
                    showAlert("Erreur", "Le kilométrage d'arrivée doit être supérieur ou égal au kilométrage de départ !");
                    return;
                }

                if (agencePane.datePickerRetour.getValue() == null) {
                    showAlert("Erreur", "Veuillez sélectionner une date de retour réel.");
                    return;
                }

                int kmParcouru = kmArrive - contrat.getKm_depart();
                double montantKm = kmParcouru * contrat.getTarifKm();

                
                LocalDateTime dateTimeDepart = LocalDateTime.parse(contrat.getDateDepart(), formatter);
                LocalDateTime dateTimeRetourPrevu = LocalDateTime.parse(contrat.getDateRetourPrevue(), formatter);
                LocalDate dateRetourReel = agencePane.datePickerRetour.getValue();

                nombreJours = (int) ChronoUnit.DAYS.between(dateTimeDepart.toLocalDate(), dateRetourReel);
                if (nombreJours <= 0) {
                    showAlert("Erreur", "La date de retour réel doit être après la date de départ !");
                    return;
                }

                // Calculate base amount
                montantJour = nombreJours * contrat.getForfaitJournalier();

                // Check if return date is later than planned
                if (dateRetourReel.isAfter(dateTimeRetourPrevu.toLocalDate())) {
                    // Apply 30% penalty on the daily rate
                    double penalite = montantJour * 0.30;
                    montantJour += penalite;
                    
                    // Show penalty alert
                    showAlert("Information", "Une pénalité de 30% a été appliquée pour retard de retour (" + penalite + " dh)");
                }

                montantJour = nombreJours * contrat.getForfaitJournalier();
                montantTotal = montantKm + montantJour;

                if (!facturePane.forfaitAgenceTextField.getText().isEmpty()) {
                    double forfaitAgence = Double.parseDouble(facturePane.forfaitAgenceTextField.getText());
                    montantTotal += forfaitAgence;
                }

                facturePane.nbrJourTextField.setText(String.valueOf(nombreJours));
                facturePane.montantJourTextField.setText(montantJour + " dh");
                facturePane.kmTextField.setText(String.valueOf(kmParcouru));
                facturePane.montantKmTextField.setText(montantKm + " dh");
                facturePane.montantTotalTextField.setText(montantTotal + " dh");

            } catch (NumberFormatException ex) {
                showAlert("Erreur", "Veuillez entrer un kilométrage valide (nombre entier).");
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Erreur", "Une erreur s'est produite : " + ex.getMessage());
            }
        });
        
        v1.getChildren().addAll(clientComboBox, clientInfoPane);
        v1.getStyleClass().add("vbox-facture");
        v2.getChildren().addAll(vehiculeComboBox, vehiculeInfoPane);
        v2.getStyleClass().add("vbox-facture");
        hbox.getChildren().addAll(v1, v2);
        hbox.getStyleClass().add("hbox-facture");

        sauvegarderButton.getStyleClass().add("button-facture");
        sauvegarderButton.setOnAction(e -> {
            try {
                // Vérifier que les calculs sont faits
                if (facturePane.montantTotalTextField.getText().isEmpty()) {
                    showAlert("Erreur", "Veuillez d'abord calculer la facture.");
                    return;
                }

                // Vérifier que la date de facture est sélectionnée
                if (datePickerFacture.getValue() == null) {
                    showAlert("Erreur", "Veuillez sélectionner une date de facture.");
                    return;
                }

                // Vérifier que la date de retour réel est sélectionnée
                if (agencePane.datePickerRetour.getValue() == null) {
                    showAlert("Erreur", "Veuillez sélectionner une date de retour réel.");
                    return;
                }

                // Générer le PDF
                
                FacturePDFGenerator.generatePDF(contrat, client, vehicule, agenceDepart,
                        nombreJours, montantJour, montantTotal,
                        Integer.parseInt(facturePane.kmTextField.getText()));
                
                ContratDAO contratDao = new ContratDAO();
                Client client1 = contratDao.getClientByIdContrat(contrat.getIdContrat());
                
                
                // Enregistrer en base
                FactureDAO factureDAO = new FactureDAO();
                factureDAO.saveFacture(new Facture(0, contrat.getIdContrat(),
                		client1.getNom()+" "+client1.getPrenom(),
                        datePickerFacture.getValue().toString(),
                        agencePane.datePickerRetour.getValue().toString(),
                        nombreJours,
                        Integer.parseInt(agencePane.kmArriveField.getText()),
                        Integer.parseInt(facturePane.kmTextField.getText()),
                        montantTotal,
                        userID));

                showAlert("Succès", "Facture sauvegardée et PDF généré !");
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Erreur", "Impossible de sauvegarder la facture : " + ex.getMessage());
            }
        });

        root.setPadding(new Insets(20));
        root.getChildren().addAll(
        	hboxTete,
            hbox, 
            agencePane,
            facturePane,
            calculerButton,
            sauvegarderButton
        );

        Scene scene = new Scene(scrollPane, 700, 650);
        window.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        CustomStage customStage = new CustomStage();
        customStage.decorate(window, scene, "Nouvelle Facture", false, 900, 700, true);
        window.initModality(Modality.APPLICATION_MODAL);
        window.show();
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
