package de.htw.app;

import de.htw.app.model.Car;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.Arrays;

public class BarChart extends BorderPane {

    ObservableList<String> options = FXCollections.observableArrayList();
    ComboBox<String> selector = new ComboBox<>(options);
    Pane pane = new Pane();

    float ticks = 5F;

    Car averageCar, currentCar;
    boolean europeanValues;

    Paint avgColor = Color.DARKGREY;
    Paint currColor = Color.DARKGREEN;

    BarChart(Car averageCar, Car currentCar, boolean europeanValues) {

        pane.setMinSize(200, 200);
        pane.setMaxSize(200, 200);

        this.currentCar = currentCar;
        this.averageCar = averageCar;

        this.europeanValues = europeanValues;

        Arrays.stream(Car.ComparableValues.values()).forEach(o -> options.add(o.toString()));

        setTop(selector);
        BorderPane.setMargin(selector, new Insets(10));
        setCenter(pane);
        BorderPane.setMargin(pane, new Insets(10));

        VBox legend = new VBox(5);

        Label curLabel = new Label("Current");
        curLabel.setTextFill(currColor);
        legend.getChildren().addAll(new HBox(new Rectangle(20,20, currColor), curLabel));

        Label avgLabel = new Label("Average");
        avgLabel.setTextFill(avgColor);
        legend.getChildren().addAll(new HBox(new Rectangle(20,20, avgColor), avgLabel));

        setBottom(legend);

        selector.getSelectionModel().selectedItemProperty().addListener((options, oV, nV) -> {
            pane.getChildren().clear();
            generateBarChart(nV);
        });
    }

    void generateBarChart(String value) {

        Car.ComparableValues type = Car.asEnum(value);

        switch (type) {
            case Acceleration:
                 generateBars(averageCar.getAcceleration(), currentCar.getAcceleration());
                break;
            case Consumption:
                generateBars(averageCar.getConsumption(europeanValues), currentCar.getConsumption(europeanValues));
                break;
            case Cylinder:
                generateBars(new Double(averageCar.getCylinder()), new Double(currentCar.getCylinder()));
                break;
            case Displacement:
                generateBars(averageCar.getDisplacement(europeanValues), currentCar.getDisplacement(europeanValues));
                break;
            case Horsepower:
                generateBars(averageCar.getHorsepower(), currentCar.getHorsepower());
                break;
            case Weight:
                generateBars(averageCar.getWeight(europeanValues), currentCar.getWeight(europeanValues));
                break;
        }
    }

    private void generateBars(Double avgY, Double curY) {

        double maxY = Math.max(avgY, curY);

        Rectangle yAxis = new Rectangle(2, pane.getHeight());
        yAxis.setX(0);
        yAxis.setY(0);

        for (int i = 0; i <= ticks; i++) {
            double yTag = 0 + i * (maxY - 0) / ticks;

            Rectangle marker = new Rectangle(7, 2);
            marker.setX(-7);
            marker.setY(i * pane.getHeight() / ticks);

            Text t = new Text(String.valueOf(Math.round((yTag*100)/100)));
            t.setTextAlignment(TextAlignment.CENTER);
            t.setX(-45);
            t.setY(pane.getHeight() - i * pane.getHeight() / ticks + 5);


            pane.getChildren().add(t);
            pane.getChildren().add(marker);
        }


        double avgHeight = (avgY/maxY) * pane.getHeight();
        double currHeight = (curY/maxY) * pane.getHeight();

        Rectangle cur = new Rectangle(30,  currHeight, currColor);
        Rectangle avg = new Rectangle(30 , avgHeight, avgColor);

        cur.setX(10);
        avg.setX(50);

        cur.setY(pane.getHeight()-currHeight);
        avg.setY(pane.getHeight()-avgHeight);

        pane.getChildren().addAll(cur, avg, yAxis);

    }




}
