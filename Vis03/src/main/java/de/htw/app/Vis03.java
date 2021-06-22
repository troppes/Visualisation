package de.htw.app;

import de.htw.app.lib.ConnectionManager;
import de.htw.app.model.Logo;
import de.htw.app.model.Car;
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

    Boolean metric = false;

    @Override
    public void start(Stage primaryStage) {

        List<Car> cars = ConnectionManager.loadCSV("https://home.htw-berlin.de/~jungk/vis_ss21/ue03/cars.txt", Car.class, '\t');

        List<Logo> logos = ConnectionManager.loadJSON("logos/data.json", Logo.class);

        assert cars != null;
        assert logos != null;

        root = new BorderPane();
        root.setPrefSize(1280, 720);

        ScatterChart scatterChart = new ScatterChart(cars, logos, 1600, 800);
        //ScatterChart scatterChart = new ScatterChart(cars, logos, 1080, 800);

        Button unitsButton = new Button("Set units to metric");
        unitsButton.setOnAction(e -> {
            if(metric){
                unitsButton.setText("Set units to metric");
                metric = false;
            }else{
                unitsButton.setText("Set units to american");
                metric = true;
            }
            scatterChart.changedMetric(metric);
        });

        root.setTop(scatterChart.getPane());
        root.setBottom(unitsButton);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("style.css");

        primaryStage.setWidth(2000);
        primaryStage.setHeight(1400);
        this.primaryStage = primaryStage;

        primaryStage.setScene(scene);
        primaryStage.setTitle("Autobahn Auto fahren - Extended Deluxe Edition");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}

