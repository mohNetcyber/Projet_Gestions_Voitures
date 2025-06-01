package main;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import dao.UtilisateurDAO;
import model.Utilisateur;
import utils.CustomStage;

import main.AddUserWindow; 

public class MainWindow extends Application {
 private Stage window;
 private BorderPane rootacc = new BorderPane();
 private Scene scene = new Scene(rootacc);
 private TextField loginField = new TextField();
 private PasswordField passwordField = new PasswordField();
 private Button loginButton = new Button("Se connecter");
 private Button signupButton = new Button("S'inscrire"); // Nouveau bouton

 private RadioButton userRadio = new RadioButton("Utilisateur");
 private RadioButton adminRadio = new RadioButton("Administrateur");
 private ToggleGroup roleGroup = new ToggleGroup();

 @Override
 public void start(Stage window) {
     this.window = window;
     StackPane centeredPane = new StackPane(createLoginForm());
     centeredPane.setAlignment(Pos.CENTER);
     rootacc.setCenter(centeredPane);

     addStylesToNodes();

     scene.getStylesheets().add("css/style.css");
     window.setScene(scene);
     window.setWidth(1100);
     window.setHeight(700);
     window.setTitle("Gestion de voitures");
     window.getIcons().add(new Image("file:icone.png"));

     CustomStage customStage = new CustomStage();
     customStage.decorate(window, scene, "Gestion des voitures ", false, 1100, 700, true);
     window.show();
 }

 private VBox createLoginForm() {
     VBox card = new VBox(10);
     card.setAlignment(Pos.CENTER);
     card.setPrefSize(300, 200);
     card.getStyleClass().add("card-login");

     VBox form = new VBox(10);
     form.setAlignment(Pos.CENTER);

     loginField.setPromptText("Entrer votre login");
     passwordField.setPromptText("Entrer votre mot de passe");

     loginButton.setPrefWidth(200);
     signupButton.setPrefWidth(200);

     loginButton.setOnAction(e -> handleLogin());
     signupButton.setOnAction(e -> handleSignup()); // Action ajoutée

     HBox radioBox = createRadioButtons();

     form.getChildren().addAll(
         new Label("Login"), loginField,
         new Label("Mot de passe"), passwordField,
         loginButton,
         signupButton, // Ajout du bouton "S'inscrire"
         new Label("Choisissez une option :"), radioBox
     );

     ImageView logo = new ImageView(new Image("file:icone.png"));
     logo.setFitHeight(80);
     logo.setPreserveRatio(true);

     card.getChildren().addAll(logo, form);
     return card;
 }

 private HBox createRadioButtons() {
     userRadio.setToggleGroup(roleGroup);
     adminRadio.setToggleGroup(roleGroup);
     userRadio.setSelected(true);

     HBox radioBox = new HBox(30, userRadio, adminRadio);
     radioBox.setAlignment(Pos.CENTER);
     radioBox.getStyleClass().add("radio-box-login");
     radioBox.setPadding(new Insets(10, 0, 0, 0));
     return radioBox;
 }

 private void handleLogin() {
     String login = loginField.getText().trim();
     String password = passwordField.getText().trim();

     if (login.isEmpty() || password.isEmpty()) {
         showAlert("Erreur", "Veuillez remplir tous les champs !");
         return;
     }

     if (roleGroup.getSelectedToggle() == null) {
         showAlert("Erreur", "Veuillez sélectionner une option !");
         return;
     }

     String role = ((RadioButton) roleGroup.getSelectedToggle()).getText().toLowerCase();
     UtilisateurDAO dao = new UtilisateurDAO();
     Utilisateur user = dao.authenticate(login, password, role);

     if (user != null) {
         System.out.println("Bienvenue " + user.getPrenom());
         if (role.equals("utilisateur")) {
             new PageAccueilWindow().afficher(user);
             window.close();
         } else if (role.equals("administrateur")) {
             new AdminDashboard().afficher();
             window.close();
         }
     } else {
         showAlert("Erreur", "Login ou mot de passe incorrect !");
     }
 }

 private void handleSignup() {
	    ObservableList<Utilisateur> userList = FXCollections.observableArrayList();
	    AddUserWindow addUser = new AddUserWindow(userList);
	    addUser.displayuser(); // Assuming you have a show() method
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
     rootacc.getStyleClass().add("root-login");
     loginButton.getStyleClass().add("button-login");
     signupButton.getStyleClass().add("button-login"); 
 }

 public static void main(String[] args) {
     launch(args);
 }
}
