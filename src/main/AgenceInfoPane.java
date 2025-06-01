package main;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Agence;
import model.Contrat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class AgenceInfoPane extends VBox {
    public ComboBox<Agence> agenceComboBox;
    public DatePicker datePickerRetour;
    public TextField kmArriveField;
    public TextField agenceDepartField;
    public TextField kmDepartField;
    public TextField dateDepartField;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
    private DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public AgenceInfoPane(Agence agenceDepart, Contrat contrat, List<Agence> agences) {
        setSpacing(15);
        getStyleClass().add("agence-pane");
        Label titleDepart = new Label("Informations Agence de départ");
        titleDepart.getStyleClass().add("label-title-pane");
        
        Label titleArrivee = new Label("Informations Agence d'arrivée");
        titleArrivee.getStyleClass().add("label-title-pane");
        
        agenceDepartField = new TextField();
        agenceDepartField = createStyledField();
        agenceDepartField.setEditable(false);
        kmDepartField = new TextField();
        kmDepartField = createStyledField();
        kmDepartField.setEditable(false);
        dateDepartField = new TextField();
        dateDepartField = createStyledField();
        dateDepartField.setEditable(false);

        // Ligne 1
        HBox hboxDepart = new HBox(10);
        hboxDepart.getChildren().addAll(
    		createStyledLabel("Agence de départ :"), agenceDepartField,
    		createStyledLabel("Date départ :"), dateDepartField,
    		createStyledLabel("Kilométrage départ :"), kmDepartField
        );

        // Ligne 2
        HBox hboxArrivee = new HBox(10);
        agenceComboBox = new ComboBox<>();
        agenceComboBox.getItems().addAll(agences);
        agenceComboBox.getStyleClass().add("pane-combobox");
        agenceComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Agence agence, boolean empty) {
                super.updateItem(agence, empty);
                setText(empty || agence == null ? null : agence.getNomAgence());
            }
        });
        agenceComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Agence agence) {
                return agence == null ? "" : agence.getNomAgence();
            }

            @Override
            public Agence fromString(String string) {
                return null;
            }
        });

        datePickerRetour = new DatePicker();
        datePickerRetour.getStyleClass().add("pane-datepicker");
        kmArriveField = new TextField();
        kmArriveField = createStyledField();
        kmArriveField.setEditable(true);
        kmArriveField.setPromptText("Ex: 90000");

        hboxArrivee.getChildren().addAll(
        	createStyledLabel("Agence d'arrivée :"), agenceComboBox,
        	createStyledLabel("Date retour :"), datePickerRetour,
        	createStyledLabel("Kilométrage d'arrivée :"), kmArriveField
        );

        getChildren().addAll(titleDepart, hboxDepart, titleArrivee, hboxArrivee);

        update(agenceDepart, contrat, agences); // initialisation
    }

    public void update(Agence agenceDepart, Contrat contrat, List<Agence> agences) {
        agenceComboBox.getItems().clear();
        agenceComboBox.getItems().addAll(agences);

        if (agenceDepart != null) {
            agenceDepartField.setText(agenceDepart.getNomAgence());
        } else {
            agenceDepartField.setText("");
        }

        if (contrat != null) {
            kmDepartField.setText(String.valueOf(contrat.getKm_depart()));
            String dateDepart = contrat.getDateDepart();
            LocalDateTime parsedDate = LocalDateTime.parse(dateDepart, formatter);
            dateDepartField.setText(parsedDate.format(displayFormatter)); // Now using displayFormatter
        } else {
            kmDepartField.setText("");
            dateDepartField.setText("");
        }

        kmArriveField.clear();
        datePickerRetour.setValue(null);
        agenceComboBox.getSelectionModel().clearSelection();
        
        if (agenceDepart != null) {
			agenceComboBox.getSelectionModel().select(agenceDepart);
		}
    }
    
    private TextField createStyledField() {
        TextField tf = new TextField();
        tf.setEditable(false);
        tf.getStyleClass().add("pane-textfield");
        return tf;
    }
    
    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("label-pane");
        return label;
    }
}
