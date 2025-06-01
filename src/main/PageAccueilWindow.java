package main;

import dao.UtilisateurDAO;
import javafx.animation.FadeTransition;
import javafx.scene.effect.DropShadow;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import model.Utilisateur;
import utils.CustomStage;

import java.util.*;

public class PageAccueilWindow {
	private Stage stage = new Stage();
	private ScrollPane scrollPane = new ScrollPane();
    private final GridPane grid = new GridPane();
    private final BorderPane root = new BorderPane();
    private final Scene scene = new Scene(root, 900, 600);
    private final Button menuIconButton = new Button("☰ Menu");
    private final VBox menuSidebar = new VBox(10);
    private final DropShadow hoverShadow = new DropShadow(10, Color.GRAY);

    private Utilisateur utilisateurConnecte;

    private final Map<String, Runnable> cardActions = new LinkedHashMap<>();

    public PageAccueilWindow() {
        utilisateurConnecte = new UtilisateurDAO().getUtilisateurConnecte();
        if (utilisateurConnecte == null) {
            throw new IllegalStateException("No user connected");
        }
        initializeCardActions();
    }

    public void afficher(Utilisateur utilisateur) {
        utilisateurConnecte = utilisateur;

        setupLayout();
        createMenu();
        createCards();
        addStyles();
        addEvents();

        Label title = new Label("Bienvenue dans l'application de location de voitures");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#f5f5dc"));

        stage = new Stage();
        CustomStage customStage = new CustomStage();

        customStage.decorate(
            stage,
            scene,
            "Page d'accueil - Location de voitures",
            false,  
            1100,    
            650,    
            true   
        );

        stage.getIcons().add(new Image("file:icone.png"));
        stage.show();
    }

    private void initializeCardActions() {
        cardActions.put("Ajouter Client", () -> new FormClientWindow());
        cardActions.put("Liste des Clients", () -> new ListClientWindow());
        cardActions.put("Lister les vehicules", () -> new ListVehiculesDisponibles());
        cardActions.put("Mes contrats", () -> new ListContratsUtilisateur());
        cardActions.put("Historique des Factures", () -> new HistoriqueFactureWindow());
        cardActions.put("Nouvelle Facture", () -> new NouvelleFactureWindow(utilisateurConnecte));
    }

    private void setupLayout() {
        String nomUtilisateur = utilisateurConnecte.getNom();
        Label welcomeLabel = new Label("Bienvenue sur votre page d'accueil, " + nomUtilisateur);
        welcomeLabel.getStyleClass().add("welcome-label");

        VBox contentBox = new VBox(20, welcomeLabel, grid);
        contentBox.setPadding(new Insets(30));
        contentBox.setAlignment(Pos.TOP_CENTER);

        scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        root.setCenter(scrollPane);
        root.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 100%, #1e1e1e, #0d0d0d);");

        grid.setPadding(new Insets(30));
        grid.setHgap(30);
        grid.setVgap(30);
        grid.setAlignment(Pos.CENTER);
    }


    private void createMenu() {
        HBox topBar = new HBox(menuIconButton);
        topBar.getStyleClass().add("top-bar");
        root.setTop(topBar);

        menuSidebar.setPadding(new Insets(10));
        menuSidebar.getStyleClass().add("menu-sidebar");

        VBox clientsSection = createMenuSection("Clients", "Ajouter Client", "Liste des Clients");
        VBox contratSection = createMenuSection("Contrat", "Mes contrats");
        VBox vehiculeSection = createMenuSection("Véhicules", "Lister les vehicules");
        VBox historiqueSection = createMenuSection("Historique", "Historique des Factures");
        
        
        Button quitterBtn = new Button("Fermer l'application");
        quitterBtn.setOnAction(e -> System.exit(0));
        quitterBtn.getStyleClass().add("menu-item");
        
        Button seDeconnecterBtn = new Button("Se déconnecter");
        seDeconnecterBtn.setOnAction(e -> {
        	Stage stageMain = new Stage();
        	MainWindow mainWindow = new MainWindow();
        	mainWindow.start(stageMain);
        	stage.close();
        	
        });
        seDeconnecterBtn.getStyleClass().add("menu-item");

        menuSidebar.getChildren().addAll(
                clientsSection, contratSection, vehiculeSection, historiqueSection, seDeconnecterBtn, quitterBtn
        );
    }

    private VBox createMenuSection(String headerText, String... buttonLabels) {
        Label header = new Label(headerText);
        header.getStyleClass().add("menu-header");

        VBox section = new VBox(5);
        section.getChildren().add(header);
        section.getStyleClass().add("menu-section");

        for (String label : buttonLabels) {
            Button btn = new Button(label);
            btn.setOnAction(e -> runAction(label));
            section.getChildren().add(btn);
        }
        return section;
    }

    private void createCards() {
        int row = 0, col = 0;

        for (Map.Entry<String, Runnable> entry : cardActions.entrySet()) {
            VBox card = createCard(entry.getKey(), entry.getValue());
            grid.add(card, col, row);

            col = (col + 1) % 2;
            if (col == 0) row++;
        }
    }

    private VBox createCard(String label, Runnable action) {
        Button button = new Button(label);
        button.setOnAction(e -> action.run());
        button.getStyleClass().add("card-button");

        Text description = new Text(getDescriptionForButton(label));
        description.getStyleClass().add("card-text");
        description.setTextAlignment(TextAlignment.CENTER);
        description.setWrappingWidth(180);

        VBox card = new VBox(15, description, button);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setPrefSize(220, 220);
        card.getStyleClass().add("card");
        
        card.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(javafx.util.Duration.millis(200), card);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
            card.setEffect(hoverShadow);
        });

        card.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(javafx.util.Duration.millis(200), card);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
            card.setEffect(null);
        });

        FadeTransition fadeIn = new FadeTransition(javafx.util.Duration.millis(500), card);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        return card;
    }

    private String getDescriptionForButton(String label) {
        return switch (label) {
            case "Ajouter Client" -> "Ajouter un nouveau client dans le système";
            case "Liste des Clients" -> "Consulter et gérer la liste des clients";
            case "Lister les vehicules" -> "Voir tous les véhicules disponibles";
            case "Mes contrats" -> "Voir les contrats de mes client";
            case "Historique des Factures" -> "Consulter l'historique des factures";
            case "Nouvelle Facture" -> "Facture du client";
            default -> "";
        };
    }

    private void addStyles() {
        scene.getStylesheets().add("css/style.css");
        grid.getStyleClass().add("grid-accueil");
        scrollPane.getStyleClass().add("scroll-pane-accueil");
        root.getStyleClass().add("root-accueil");
        menuIconButton.getStyleClass().add("menu-icon-button");
    }

    private void addEvents() {
        menuIconButton.setOnAction(e -> toggleMenuSidebar());
    }

    private void toggleMenuSidebar() {
        if (root.getLeft() == null) {
            root.setLeft(menuSidebar);
        } else {
            root.setLeft(null);
        }
    }

    private void runAction(String label) {
        Runnable action = cardActions.get(label);
        if (action != null) {
            System.out.println(label + " clicked");
            action.run();
        }
    }
}
