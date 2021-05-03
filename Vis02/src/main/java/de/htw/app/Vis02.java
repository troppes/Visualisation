package de.htw.app;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Date;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class Vis02 extends Application {

    private Player player = new Player();
    Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();
        root.setPrefSize(1280, 720);


        TextInputDialog dialog = new TextInputDialog("walter");
        dialog.setTitle("Enter Name");
        dialog.setHeaderText("Welcome");
        dialog.setContentText("Please enter your name:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> player.name = s);
        if (result.isEmpty()) {
            System.exit(0);
        }

        Text welcomeMessage = new Text(String.join("\n"
                , "Welcome " + player.name + ","
                , "thank you for playing our Game! \n"
                , "Controls:"
                , "w or Button + = Increase shape size"
                , "s or Button - = Decrease shape size"
                , "e or Button ++ = Increase shape size * 5"
                , "d or Button -- = Decrease shape size * 5 \n"
                , "You can play as many shapes as you want, but after you have played three shapes, you can press the FINISH button to see your results."
        ));
        welcomeMessage.setStyle("-fx-font: 14 monospaced;");

        Alert instructions = new Alert(Alert.AlertType.INFORMATION);
        instructions.getDialogPane().setContent(welcomeMessage);
        instructions.setTitle("Instructions");
        instructions.setHeaderText("Instructions");
        instructions.showAndWait();

        Button b = new Button();

        Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.millis(1), event -> root.setCenter(null)));
        fiveSecondsWonder.setCycleCount(1);
        fiveSecondsWonder.play();


        b.setOnAction(event -> {
            root.setCenter(new Rectangle(250, 250));
            fiveSecondsWonder.play();
        });

        root.setBottom(b);


        Scene scene = new Scene(root);

        this.primaryStage = primaryStage;

        primaryStage.setScene(scene);
        primaryStage.setTitle("Vis02");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
