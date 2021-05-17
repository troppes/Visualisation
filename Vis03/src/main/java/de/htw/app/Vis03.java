package de.htw.app;
import de.htw.app.lib.ConnectionManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.*;

public class Vis03 extends Application {

    //Screen item references
    Stage primaryStage;
    BorderPane root;

    @Override
    public void start(Stage primaryStage) {

        List<Car> cars = ConnectionManager.loadCSV("https://home.htw-berlin.de/~jungk/vis_ss21/ue03/cars.txt", Car.class,'\t');


        VBox testOutput = new VBox();
        for(int i = 0; i < 10; i++){
            testOutput.getChildren().add(new Label("Name: "+cars.get(i).getName()+" MPG: "+cars.get(i).getConsumption(false)+" KML: "+cars.get(i).getConsumption(true)));
        }

        root = new BorderPane();
        root.setPrefSize(1280, 720);

        root.setCenter(testOutput);


        Scene scene = new Scene(root);
        scene.getStylesheets().add("style.css");


        this.primaryStage = primaryStage;

        primaryStage.setScene(scene);
        primaryStage.setTitle("Find Waldo: Reaction Edition");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}

