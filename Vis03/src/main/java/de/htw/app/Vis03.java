package de.htw.app;

import de.htw.app.lib.ConnectionManager;
import de.htw.app.model.Logo;
import de.htw.app.model.Car;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;

public class Vis03 extends Application {

    //Screen item references
    Stage primaryStage;
    BorderPane root;

    @Override
    public void start(Stage primaryStage) {

        Stage detailWindow = new Stage();

        List<Car> cars = ConnectionManager.loadCSV("https://home.htw-berlin.de/~jungk/vis_ss21/ue03/cars.txt", Car.class, '\t');
        List<Logo> logos = ConnectionManager.loadJSON("logos/data.json", Logo.class);


        ImageView imageView = new ImageView();
        //Setting image to the image view

        assert logos != null;
        Logo logo = logos.stream()
                .filter(l -> l.getSlug().equals("amc"))
                .findFirst()
                .orElse(null);


        assert logo != null;
        imageView.setImage(logo.getThumbnail());
        imageView.setId(logo.getSlug());
        //Setting the image view parameters
        imageView.setX(10);
        imageView.setY(10);
        imageView.setFitWidth(575);
        imageView.setPreserveRatio(true);

        ColorAdjust colorAdjust = new ColorAdjust();
        //Setting the saturation value
        colorAdjust.setSaturation(-1);
        //Applying coloradjust effect to the ImageView node
        imageView.setEffect(colorAdjust);



        imageView.setOnMouseClicked(e -> {
            ImageView v = (ImageView) e.getSource();

            assert cars != null;
            Car car = cars.stream()
                    .filter(c -> c.getManufacturer().equals(v.getId()))
                    .findFirst()
                    .orElse(null);
            assert car != null;



            Parent root = new BorderPane();
            detailWindow.setTitle(car.getName());
            detailWindow.setScene(new Scene(root, 450, 450));
            detailWindow.show();
        });



        VBox testOutput = new VBox();
        for (int i = 0; i < 10; i++) {
            testOutput.getChildren().add(new Label("Name: " + cars.get(i).getName() + " MPG: " + cars.get(i).getConsumption(false) + " KML: " + cars.get(i).getConsumption(true)));
        }

        root = new BorderPane();
        root.setPrefSize(1280, 720);


        root.setCenter(testOutput);
        root.setTop(imageView);


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

