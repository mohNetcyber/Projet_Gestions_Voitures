package main;

import dao.UtilisateurDAO;
import dao.VehiculeDAO;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminDashboard {
	Stage primaryStage = new Stage();
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root);
    Label header = new Label("Administrator Dashboard");
    HBox headerBox = new HBox();
    VBox menu = new VBox();
    Button manageUsersButton = new Button("Gestion des Utilisateurss");
    Button manageVehiclesButton = new Button("Gestion des Vehicules");
    Button manageAgence = new Button("Gestion des agences");
    Button logoutButton = new Button("Se déconnecter");
    Button manageContratsButton = new Button("Gestion des Contrats");
    
    // DAO
    VehiculeDAO vehiculeDAO = new VehiculeDAO();
    UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public void afficher() {
        root = new BorderPane();
        root.getStyleClass().add("root");

        // Header
        header.setText("Administrator Dashboard");
        header.getStyleClass().add("header-label");

        headerBox = new HBox(header);
        headerBox.getStyleClass().add("header-box");
        root.setTop(headerBox);

        // Cards avec données dynamiques
        VBox statsBox = buildStatsCards();
        root.setCenter(statsBox);

        // Menu horizontal en bas
        HBox bottomMenu = new HBox(manageUsersButton, manageVehiclesButton, manageAgence, manageContratsButton, logoutButton);
        bottomMenu.getStyleClass().add("bottom-menu");
        for (Button b : new Button[]{manageUsersButton, manageVehiclesButton, manageAgence, manageContratsButton, logoutButton}) {
            b.getStyleClass().add("menu-button");
        }
        root.setBottom(bottomMenu);

        // Actions
        manageUsersButton.setOnAction(e -> new ManageUsersPage().display(primaryStage));
        manageVehiclesButton.setOnAction(e -> new ManageVehiculePage().display(primaryStage));
        manageAgence.setOnAction(e -> {
        	new ManageAgenceWindow(primaryStage);
        });
        manageContratsButton.setOnAction(e -> new ManageContratsPage().display(primaryStage));
        	
        logoutButton.setOnAction(e ->{
        	Stage stageMain = new Stage();
        	MainWindow mainWindow = new MainWindow();
        	mainWindow.start(stageMain);
        	primaryStage.close();
        });

        // Scene
        scene = new Scene(root, 1100, 650);
        scene.getStylesheets().add("css/styleAdmin.css");
        primaryStage.setTitle("Administrator Dashboard");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("file:admin.png"));
        primaryStage.show();
    }

    private VBox buildStatsCards() {
        // Appel DAO dynamique
        int totalVehicules = vehiculeDAO.countAllVehicules();
        int louees = vehiculeDAO.countVehiculesLoues();
        int users = utilisateurDAO.countAllUsers();

        Label totalVehiculesLabel = new Label("Total voitures : " + totalVehicules);
        Label loueesLabel = new Label("Voitures louées : " + louees);
        Label usersLabel = new Label("Utilisateurs : " + users);

        VBox card1 = createCard(totalVehiculesLabel);
        VBox card2 = createCard(loueesLabel);
        VBox card3 = createCard(usersLabel);

        HBox statsBox = new HBox(20, card1, card2, card3);
        statsBox.getStyleClass().add("stats-box");
        statsBox.setAlignment(Pos.CENTER);
        return new VBox(statsBox);
    }

    private VBox createCard(Label label) {
        label.getStyleClass().add("card-label");
        VBox card = new VBox(label);
        card.getStyleClass().add("card");
        return card;
    }

}