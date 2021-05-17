package de.htw.app;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.*;

public class Vis03 extends Application {

    private final Player player = new Player();

    //Screen item references
    Stage primaryStage;
    BorderPane root;

    @Override
    public void start(Stage primaryStage) {

        List<Car> cars = ConnectionManager.loadCSV("https://home.htw-berlin.de/~jungk/vis_ss21/ue03/cars.txt", Car.class,'\t');

        for(int i = 0; i < 10; i++){
            System.out.println(cars.get(i).getName());
        }

        root = new BorderPane();
        root.setPrefSize(1280, 720);

        //NAME SELECTOR
        TextInputDialog dialog = new TextInputDialog("walter");
        dialog.setTitle("Enter Name");
        dialog.setHeaderText("Welcome");
        dialog.setContentText("Please enter your name:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(player::setName);
        if (result.isEmpty()) {
            System.exit(0);
        }

        //INSTRUCTIONS SCREEN
        Text welcomeMessage = new Text(String.join("\n"
                , "Welcome " + player.getName() + ", \n"
                , "thank you for playing our game! \n"
                , "The goal of the game is to find the target. \n"
                , "The target is an outlier, that has e.g. another color. The specific difference is shown in the label on the top of the window.\n"
                , "We show you the playing field for different amounts of time to test your pre-attentive perception, \n"
                , "and afterwards you need to click where you think you saw the target. If you did not see it, you can always click the button \n"
                , "\"I did not see the target\"."
        ));
        welcomeMessage.setStyle("-fx-font: 14 monospaced;");

        Alert instructions = new Alert(Alert.AlertType.INFORMATION);
        instructions.getDialogPane().setContent(welcomeMessage);
        instructions.setTitle("Instructions");
        instructions.setHeaderText("Instructions");
        instructions.showAndWait();


        Scene scene = new Scene(root);
        scene.getStylesheets().add("style.css");


        this.primaryStage = primaryStage;


        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (key.getCode() == KeyCode.Z) {
                System.out.println("TODO");
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Find Waldo: Reaction Edition");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}

