package utils;


import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Cursor;

public class CustomStage {
    private double xOffset = 0;
    private double yOffset = 0;

    private double prevX, prevY, prevWidth, prevHeight;
    private boolean maximized = false;
    private static final int RESIZE_PADDING = 5;
    private boolean isResizing = false;
    private Cursor originalCursor;
    
    
    public void decorate(Stage stage, Scene scene, String windowTitle, boolean startMaximized, double width, double height, boolean center) {
        stage.initStyle(StageStyle.UNDECORATED);

        // Title Bar
        HBox titleBar = new HBox();
        titleBar.getStyleClass().add("title-bar");

        Label titleLabel = new Label(windowTitle);
        titleLabel.getStyleClass().add("window-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button minimizeButton = new Button("—");
        Button toggleButton = new Button("🗖");
        Button closeButton = new Button("X");

        minimizeButton.getStyleClass().add("button-stage");
        toggleButton.getStyleClass().add("button-stage");
        closeButton.getStyleClass().add("button-stage");

        minimizeButton.setOnAction(e -> stage.setIconified(true));
        toggleButton.setOnAction(e -> {
            if (maximized) {
                restoreStage(stage);
                toggleButton.setText("🗖");
            } else {
                saveCurrentBounds(stage);
                maximizeStage(stage);
                toggleButton.setText("🗗");
            }
            maximized = !maximized;
        });
        closeButton.setOnAction(e -> stage.close());

        titleBar.getChildren().addAll(titleLabel, spacer, minimizeButton, toggleButton, closeButton);
        titleBar.setAlignment(Pos.CENTER_LEFT);

        // Drag
        titleBar.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        titleBar.setOnMouseDragged((MouseEvent event) -> {
            if (!maximized) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });

        Parent originalRoot = scene.getRoot();
        VBox contentWrapper = new VBox(originalRoot);
        contentWrapper.setAlignment(Pos.CENTER); // Centrage vertical si nécessaire
        contentWrapper.setFillWidth(true);       // Permet de s'étendre en largeur

        VBox newRoot = new VBox(titleBar, contentWrapper);
        VBox.setVgrow(contentWrapper, Priority.ALWAYS);

        newRoot.getStyleClass().add("window-root");
        Scene newScene = new Scene(newRoot);
        
        newScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setScene(newScene);
        
        newScene.setOnMouseMoved(event -> {
            if (!maximized) {
                double x = event.getX();
                double y = event.getY();
                double widthh = stage.getWidth();
                double heightt = stage.getHeight();
                
                if (x < RESIZE_PADDING && y < RESIZE_PADDING) {
                    scene.setCursor(Cursor.NW_RESIZE);
                } else if (x < RESIZE_PADDING && y > heightt - RESIZE_PADDING) {
                    scene.setCursor(Cursor.SW_RESIZE);
                } else if (x > width - RESIZE_PADDING && y < RESIZE_PADDING) {
                    scene.setCursor(Cursor.NE_RESIZE);
                } else if (x > width - RESIZE_PADDING && y > heightt - RESIZE_PADDING) {
                    scene.setCursor(Cursor.SE_RESIZE);
                } else if (x < RESIZE_PADDING || x > widthh - RESIZE_PADDING) {
                    scene.setCursor(Cursor.H_RESIZE);
                } else if (y < RESIZE_PADDING || y > heightt - RESIZE_PADDING) {
                    scene.setCursor(Cursor.V_RESIZE);
                } else {
                    scene.setCursor(Cursor.DEFAULT);
                }
            }
        });

        newScene.setOnMouseMoved(event -> {
            if (!maximized) {
                double x = event.getX();
                double y = event.getY();
                double widthh = stage.getWidth();
                double heightt = stage.getHeight();
                
                if (x < RESIZE_PADDING && y < RESIZE_PADDING) {
                    newScene.setCursor(Cursor.NW_RESIZE);
                } else if (x < RESIZE_PADDING && y > heightt - RESIZE_PADDING) {
                    newScene.setCursor(Cursor.SW_RESIZE);
                } else if (x > width - RESIZE_PADDING && y < RESIZE_PADDING) {
                    newScene.setCursor(Cursor.NE_RESIZE);
                } else if (x > width - RESIZE_PADDING && y > heightt - RESIZE_PADDING) {
                    newScene.setCursor(Cursor.SE_RESIZE);
                } else if (x < RESIZE_PADDING || x > widthh - RESIZE_PADDING) {
                    newScene.setCursor(Cursor.H_RESIZE);
                } else if (y < RESIZE_PADDING || y > heightt - RESIZE_PADDING) {
                    newScene.setCursor(Cursor.V_RESIZE);
                } else {
                    newScene.setCursor(Cursor.DEFAULT);
                }
            }
        });

        newScene.setOnMouseDragged(event -> {
            if (!maximized && newScene.getCursor() != Cursor.DEFAULT) {
                double x = event.getSceneX();
                double y = event.getSceneY();
                double widthh = stage.getWidth();
                double heightt = stage.getHeight();
                
                if (newScene.getCursor() == Cursor.H_RESIZE) {
                    if (x < widthh / 2) {
                        double newWidth = widthh - x;
                        if (newWidth >= stage.getMinWidth()) {
                            stage.setX(event.getScreenX());
                            stage.setWidth(newWidth);
                        }
                    } else {
                        stage.setWidth(x);
                    }
                } else if (newScene.getCursor() == Cursor.V_RESIZE) {
                    if (y < heightt / 2) {
                        double newHeight = heightt - y;
                        if (newHeight >= stage.getMinHeight()) {
                            stage.setY(event.getScreenY());
                            stage.setHeight(newHeight);
                        }
                    } else {
                        stage.setHeight(y);
                    }
                }
            }
        });

        
        if (startMaximized) {
            saveCurrentBounds(stage);
            maximizeStage(stage);
            maximized = true;
        } else {
            stage.setWidth(width);
            stage.setHeight(height);
            if (center) centerStage(stage);
        }
        stage.setResizable(true);
        // Min size
        stage.setMinWidth(500);
        stage.setMinHeight(400);
    }

    private void centerStage(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }

    private void maximizeStage(Stage stage) {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
    }

    private void restoreStage(Stage stage) {
        stage.setX(prevX);
        stage.setY(prevY);
        stage.setWidth(prevWidth);
        stage.setHeight(prevHeight);
    }

    private void saveCurrentBounds(Stage stage) {
        prevX = stage.getX();
        prevY = stage.getY();
        prevWidth = stage.getWidth();
        prevHeight = stage.getHeight();
    }
}
