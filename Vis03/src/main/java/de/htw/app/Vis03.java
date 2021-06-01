package de.htw.app;

import de.htw.app.lib.ConnectionManager;
import de.htw.app.model.Logo;
import de.htw.app.model.Car;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;

//ToDo: Some cars throw errors when clicked on. Not sure why yet

public class Vis03 extends Application {

    //Screen item references
    Stage primaryStage;
    BorderPane root;

    Boolean metric = false;

    Car averageCar;

    @Override
    public void start(Stage primaryStage) {

        Stage detailWindow = new Stage();

        List<Car> cars = ConnectionManager.loadCSV("https://home.htw-berlin.de/~jungk/vis_ss21/ue03/cars.txt", Car.class, '\t');

        //ToDo: Remove this block. this was for testing only, as we get null pointer exceptions from missing
            /*
            List<Car> testCars = new ArrayList<>();
            for(int i=0; i<39; i++) {
                testCars.add(cars.get(i));
            }
            cars = testCars;
            */

        List<Logo> logos = ConnectionManager.loadJSON("logos/data.json", Logo.class);

        assert cars != null;
        assert logos != null;

        averageCar = new Car(); // Todo build averagecar


        root = new BorderPane();
        root.setPrefSize(1280, 720);

        ScatterChart scatterChart = new ScatterChart(cars, logos, 1600, 800);

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

        //root.setTop(imageView);

        root.setTop(scatterChart.getPane());
        root.setBottom(unitsButton);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("style.css");

        primaryStage.setWidth(2100);
        primaryStage.setHeight(1400);
        this.primaryStage = primaryStage;

        primaryStage.setScene(scene);
        primaryStage.setTitle("Autobahn Auto fahren - Extended Deluxe Edition");
        primaryStage.show();
    }

    GridPane generateDetails(Image manufacturer, Car car, Car averageCar, boolean metric) {
        GridPane pane = new GridPane();
        pane.setHgap(10); // set gap in pixels

        pane.add(new Label("Name"), 0, 0, 1, 1);
        pane.add(new Label("Manufacturer"), 0, 1, 1, 1);
        pane.add(new Label("Consumption"), 0, 2, 1, 1);
        pane.add(new Label("Cylinder"), 0, 3, 1, 1);
        pane.add(new Label("Displacement"), 0, 4, 1, 1);
        pane.add(new Label("Horsepower"), 0, 5, 1, 1);
        pane.add(new Label("Weight"), 0, 6, 1, 1);
        pane.add(new Label("Acceleration"), 0, 7, 1, 1);
        pane.add(new Label("Year"), 0, 8, 1, 1);
        pane.add(new Label("Origin"), 0, 9, 1, 1);

        pane.add(new Label(car.getName()), 1, 0, 1, 1);
        pane.add(new Label(car.getManufacturer()), 1, 1, 1, 1);
        pane.add(new Label(car.getConsumption(metric).toString()), 1, 2, 1, 1);
        pane.add(new Label(car.getCylinder().toString()), 1, 3, 1, 1);
        pane.add(new Label(car.getDisplacement(metric).toString()), 1, 4, 1, 1);
        pane.add(new Label(car.getHorsepower().toString()), 1, 5, 1, 1);
        pane.add(new Label(car.getWeight(metric).toString()), 1, 6, 1, 1);
        pane.add(new Label(car.getAcceleration().toString()), 1, 7, 1, 1);
        pane.add(new Label(car.getYear().toString()), 1, 8, 1, 1);
        pane.add(new Label(car.getOrigin()), 1, 9, 1, 1);


        pane.add(new Label("s"), 2, 7, 1, 1);

        if(metric){
            pane.add(new Label("kmpl"), 2, 2, 1, 1);
            pane.add(new Label("ccm"), 2, 4, 1, 1);
            pane.add(new Label("ps"), 2, 5, 1, 1);
            pane.add(new Label("kg"), 2, 6, 1, 1);
        }else{
            pane.add(new Label("mpg"), 2, 2, 1, 1);
            pane.add(new Label("cci"), 2, 4, 1, 1);
            pane.add(new Label("hp"), 2, 5, 1, 1);
            pane.add(new Label("lbs"), 2, 6, 1, 1);
        }


        ImageView imageView = new ImageView();
        imageView.setImage(manufacturer);
        imageView.setX(10);
        imageView.setY(10);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);

        pane.add(imageView, 8, 0, 1, 9);

        return pane;
    }

    public static void main(String[] args) {
        launch(args);
    }

}

