package main;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.*;
import utils.CustomStage;
import dao.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class FactureContratWindows {
	private Stage window = new Stage();
    private int nombreJours;
    private double montantJour;
    private double montantTotal;
    private final int userID;
    private ScrollPane scrollPane = new ScrollPane();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

    public FactureContratWindows(Contrat contrat, Client client, Vehicule vehicule, Agence agenceDepart) {
        Utilisateur utilisateurConnecte = new UtilisateurDAO().getUtilisateurConnecte();
        userID = utilisateurConnecte.getId();

        window = new Stage();
       
        window.setTitle("Facture - Contrat #" + contrat.getIdContrat());

        VBox root = new VBox();
        scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);  // Très important !
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Bloque scroll horizontal

        scrollPane.getStyleClass().add("scroll-pane-facture");
        root.getStyleClass().add("vbox-facture");
        scrollPane.setContent(root);


        Label title = new Label("Facture du contrat #" + contrat.getIdContrat());
        title.getStyleClass().add("label-facture");

        // Date
        HBox dateBox = new HBox();
        dateBox.getStyleClass().add("hbox-facture-date");
        Label dateLabel = new Label("Date Facture :");
        DatePicker datePickerFacture = new DatePicker();
        dateBox.getChildren().addAll(dateLabel, datePickerFacture);

        // Infos
        List<Agence> agences = new AgenceDAO().getAllAgences();
        ClientInfoPane clientPane = new ClientInfoPane(client);
        VehiculeInfoPane vehiculePane = new VehiculeInfoPane(vehicule);
        AgenceInfoPane agencePane = new AgenceInfoPane(agenceDepart, contrat, agences);
        FactureCalculPane facturePane = new FactureCalculPane(contrat);
        
        HBox infoBox = new HBox(clientPane, vehiculePane);
        infoBox.setPrefWidth(Double.MAX_VALUE);
        infoBox.getStyleClass().add("hbox-facture");

        // Boutons
        Button calculerButton = new Button("Calculer la facture");
        calculerButton.getStyleClass().add("button-facture");
        Button sauvegarderButton = new Button("Sauvegarder la facture");
        sauvegarderButton.getStyleClass().add("button-facture");

        calculerButton.setOnAction(e -> calculerFacture(contrat, agencePane, facturePane));
        sauvegarderButton.setOnAction(e -> sauvegarderFacture(contrat, client, vehicule, agenceDepart,
                agencePane, facturePane, datePickerFacture));

        root.getChildren().addAll(title, dateBox, infoBox, agencePane, facturePane, calculerButton, sauvegarderButton);
        
        Scene scene = new Scene(scrollPane, 700, 650);

        window.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        CustomStage customStage = new CustomStage();
        customStage.decorate(window, scene, "Facture - Contrat #" + contrat.getIdContrat(), false, 900, 700, true);
        window.show();
    }

    private void calculerFacture(Contrat contrat, AgenceInfoPane agencePane, FactureCalculPane facturePane) {
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
                montantTotal += Double.parseDouble(facturePane.forfaitAgenceTextField.getText());
            }

            facturePane.nbrJourTextField.setText(String.valueOf(nombreJours));
            facturePane.montantJourTextField.setText(montantJour + " dh");
            facturePane.kmTextField.setText(String.valueOf(kmParcouru));
            facturePane.montantKmTextField.setText(montantKm + " dh");
            facturePane.montantTotalTextField.setText(montantTotal + " dh");
            facturePane.forfaitJourTextField.setText(contrat.getForfaitJournalier()+"");
            facturePane.kmTarifTextField.setText(contrat.getTarifKm()+"");

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer un kilométrage valide.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }

    private void sauvegarderFacture(Contrat contrat, Client client, Vehicule vehicule, Agence agenceDepart,
                                    AgenceInfoPane agencePane, FactureCalculPane facturePane, DatePicker datePickerFacture) {
        try {
            if (facturePane.montantTotalTextField.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez d'abord calculer la facture.");
                return;
            }

            if (datePickerFacture.getValue() == null || agencePane.datePickerRetour.getValue() == null) {
                showAlert("Erreur", "Veuillez sélectionner les dates requises.");
                return;
            }

            FacturePDFGenerator.generatePDF(contrat, client, vehicule, agenceDepart,
                    nombreJours, montantJour, montantTotal,
                    Integer.parseInt(facturePane.kmTextField.getText()));
            String infoclient = client.getNom()+" "+client.getPrenom();
            Facture facture = new Facture(0, contrat.getIdContrat(),
            		infoclient,
                    datePickerFacture.getValue().toString(),
                    agencePane.datePickerRetour.getValue().toString(),
                    nombreJours,
                    Integer.parseInt(agencePane.kmArriveField.getText()),
                    Integer.parseInt(facturePane.kmTextField.getText()),
                    montantTotal,
                    userID);

            new FactureDAO().saveFacture(facture);
            showAlert("Succès", "Facture sauvegardée et PDF généré !");
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de sauvegarder la facture : " + e.getMessage());
        }
        window.close();
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
