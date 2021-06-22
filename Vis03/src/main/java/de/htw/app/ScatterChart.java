package de.htw.app;

import de.htw.app.lib.Glyph;
import de.htw.app.model.Logo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import de.htw.app.model.Car;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ScatterChart {

    Stage detailWindow = new Stage();

    List<Car> cars;
    List<Logo> logos;

    Car averageCar;

    int width, height;
    boolean metric;

    BorderPane wholeWindow;
    Pane scatterChartPane;
    ComboBox<String> yAxisDropdown;

    ArrayList<Glyph> allGlyphs = new ArrayList<>();
    ArrayList<Glyph> allGlyphsWithoutValue;

    Map<String, ArrayList<Glyph>> americanGlyphs = new HashMap<>();
    Map<String, ArrayList<Glyph>> europeanGlyphs = new HashMap<>();
    Map<String, ArrayList<Glyph>> japaneseGlyphs = new HashMap<>();

    ArrayList<Rectangle> borders = new ArrayList<>();

    public ScatterChart(List<Car> cars, List<Logo> logos, int width, int height) {
        wholeWindow = new BorderPane();
        scatterChartPane = new Pane();

        this.width = width;
        this.height = height;
        this.cars = cars;
        this.logos = logos;

        //Caclulate averageCar
        generateAverageCar();

        generateGlyphOriginPairs();

        //dropdown on the lefthand side of the screen
        ObservableList<String> yAxisOptions =
                FXCollections.observableArrayList(
                        "Horsepower",
                        "Km/l",
                        "Cylinders",
                        "Displacement",
                        "Weight",
                        "Acceleration"
                );
        yAxisDropdown = new ComboBox<>(yAxisOptions);
        yAxisDropdown.setValue("Horsepower");

        yAxisDropdown.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                    generateGlyphCoordinates(newValue);
                }
        );
        HBox hbox = new HBox();
        hbox.getChildren().add(yAxisDropdown);
        hbox.setPadding(new Insets(0, 50, 0, 0));

        SelectionChart selectionChart = new SelectionChart(this, americanGlyphs, europeanGlyphs, japaneseGlyphs, width);

        wholeWindow.setLeft(hbox);
        wholeWindow.setCenter(scatterChartPane);
        wholeWindow.setBottom(selectionChart.getSelectionPane());

        wholeWindow.setPadding(new Insets(20));
        scatterChartPane.setPadding(new Insets(50));


        generateGlyphCoordinates(yAxisDropdown.getValue());
    }

    private void generateAverageCar() {

        double consumption = 0;
        int consumptionC = 0;
        int cylinder = 0;
        int cylinderC = 0;
        double displacement = 0;
        int displacementC = 0;
        double horsepower = 0;
        int horsepowerC = 0;
        double weight = 0;
        int weightC = 0;
        double acceleration = 0;
        int accelerationC = 0;

        for (Car car : cars) {
            if (car.getConsumption(false) != null) {
                consumption += car.getConsumption(false);
                consumptionC++;
            }
            if (car.getCylinder() != null){
                cylinder += car.getCylinder();
                cylinderC++;
            }
            if (car.getDisplacement(false) != null){
                displacement += car.getDisplacement(false);
                displacementC++;
            }
            if (car.getHorsepower() != null){
                horsepower += car.getHorsepower();
                horsepowerC++;
            }
            if (car.getWeight(false) != null){
                weight += car.getWeight(false);
                weightC++;
            }
            if (car.getAcceleration() != null){
                acceleration += car.getAcceleration();
                accelerationC++;
            }
        }
        consumption = (double) Math.round(consumption/consumptionC * 100) / 100;
        cylinder = cylinder/cylinderC;
        displacement = (double) Math.round(displacement/displacementC * 100) / 100;
        horsepower = (double) Math.round(horsepower/horsepowerC * 100) / 100;
        weight = (double) Math.round(weight/weightC * 100) / 100;
        acceleration = (double) Math.round(acceleration/accelerationC * 100) / 100;

        averageCar = new Car(consumption, cylinder, displacement, horsepower, weight, acceleration);
    }

    public Pane getPane() {
        return wholeWindow;
    }

    void generateGlyphOriginPairs() {
        for (Car car : cars) {
            Glyph glyph = new Glyph(car, logos);
            allGlyphs.add(glyph);

            if (car.getOrigin().equals("American")) {
                if (!americanGlyphs.containsKey(car.getManufacturer())) {
                    ArrayList<Glyph> list = new ArrayList<>();
                    list.add(glyph);
                    americanGlyphs.put(car.getManufacturer(), list);
                } else {
                    americanGlyphs.get(car.getManufacturer()).add(glyph);
                }
            } else if (car.getOrigin().equals("European")) {
                if (!europeanGlyphs.containsKey(car.getManufacturer())) {
                    ArrayList<Glyph> list = new ArrayList<>();
                    list.add(glyph);
                    europeanGlyphs.put(car.getManufacturer(), list);
                } else {
                    europeanGlyphs.get(car.getManufacturer()).add(glyph);
                }
            } else {
                if (!japaneseGlyphs.containsKey(car.getManufacturer())) {
                    ArrayList<Glyph> list = new ArrayList<>();
                    list.add(glyph);
                    japaneseGlyphs.put(car.getManufacturer(), list);
                } else {
                    japaneseGlyphs.get(car.getManufacturer()).add(glyph);
                }
            }
        }
    }

    //Change string to the enum Flo created in car
    void generateGlyphCoordinates(String yAxisValue) {
        scatterChartPane.getChildren().clear();
        allGlyphsWithoutValue = new ArrayList<>();
        borders = new ArrayList<>();

        double maxYValue = 0;
        double minYValue = 0;
        double maxXValue = 0;
        double minXValue = 70;
        for (Glyph glyph : allGlyphs) {
            double yValueToCompare = -1;
            double xValueToCompare = glyph.getCar().getYear();

            switch (yAxisValue) {
                case "Horsepower":
                    if (glyph.getCar().getHorsepower() != null)
                        yValueToCompare = glyph.getCar().getHorsepower();
                    else
                        allGlyphsWithoutValue.add(glyph);
                    break;
                case "Km/l":
                    if (glyph.getCar().getConsumption(true) != null)
                        yValueToCompare = glyph.getCar().getConsumption(metric);
                    else
                        allGlyphsWithoutValue.add(glyph);
                    break;
                case "Cylinders":
                    if (glyph.getCar().getCylinder() != null)
                        yValueToCompare = glyph.getCar().getCylinder();
                    else
                        allGlyphsWithoutValue.add(glyph);
                    break;
                case "Displacement":
                    if (glyph.getCar().getDisplacement(true) != null)
                        yValueToCompare = glyph.getCar().getDisplacement(metric);
                    else
                        allGlyphsWithoutValue.add(glyph);
                    break;
                case "Weight":
                    if (glyph.getCar().getWeight(true) != null)
                        yValueToCompare = glyph.getCar().getWeight(metric);
                    else
                        allGlyphsWithoutValue.add(glyph);
                    break;
                case "Acceleration":
                    if (glyph.getCar().getAcceleration() != null)
                        yValueToCompare = glyph.getCar().getAcceleration();
                    else
                        allGlyphsWithoutValue.add(glyph);
                    break;
            }

            if (yValueToCompare != -1 && yValueToCompare > maxYValue) {   //if y is not null
                maxYValue = yValueToCompare;
            } else if (yValueToCompare != -1 && yValueToCompare < minYValue) {
                minYValue = yValueToCompare;
            }

            if (xValueToCompare > maxXValue) {
                maxXValue = xValueToCompare;
            } else if (xValueToCompare < minXValue) {
                minXValue = xValueToCompare;
            }
        }

        //to get some spacing to the top
        switch (yAxisValue) {
            case "Horsepower":
                maxYValue += 10;
                break;
            case "Km/l":
                maxYValue += 2;
                break;
            case "Cylinders":
                maxYValue = 14;
                break;
            case "Displacement":
                maxYValue += 20;
                break;
            case "Weight":
                maxYValue += 200;
                break;
            case "Acceleration":
                maxYValue += 2;
                break;
        }

        //to leave a little space to the left and right of the graph
        maxXValue += 1;
        minXValue -= 1;

        //normalize and set position
        for (Glyph glyph : allGlyphs) {
            double x = glyph.getCar().getYear();
            double y = -1;

            switch (yAxisValue) {
                case "Horsepower":
                    if (glyph.getCar().getHorsepower() != null)
                        y = glyph.getCar().getHorsepower();
                    break;
                case "Km/l":
                    if (glyph.getCar().getConsumption(true) != null)
                        y = glyph.getCar().getConsumption(metric);
                    break;
                case "Cylinders":
                    if (glyph.getCar().getCylinder() != null)
                        y = glyph.getCar().getCylinder();
                    break;
                case "Displacement":
                    if (glyph.getCar().getDisplacement(true) != null)
                        y = glyph.getCar().getDisplacement(metric);
                    break;
                case "Weight":
                    if (glyph.getCar().getWeight(true) != null)
                        y = glyph.getCar().getWeight(metric);
                    break;
                case "Acceleration":
                    if (glyph.getCar().getAcceleration() != null)
                        y = glyph.getCar().getAcceleration();
                    break;
            }

            //https://stats.stackexchange.com/questions/281162/scale-a-number-between-a-range
            x = (x - minXValue) / (maxXValue - minXValue) * (width - 0) + 0;
            if (y >= 0)
                y = (y - minYValue) / (maxYValue - minYValue) * (height - 0) + 0;   //if y is not null
            else y = -5000;
            y = height - y;

            glyph.setPosition(x, y);
        }

        generateScatterChartAxis(minXValue, maxXValue, minYValue, maxYValue);
        drawGlyphs();
    }

    void generateScatterChartAxis(double minX, double maxX, double minY, double maxY) {
        Rectangle yAxis = new Rectangle(2, height);
        yAxis.setX(0);
        yAxis.setY(0);

        Rectangle xAxis = new Rectangle(width, 2);
        xAxis.setX(0);
        xAxis.setY(height);

        int numOfTags = 14;
        //x Axis
        for (int i = 0; i <= numOfTags; i++) {
            double xTag = minX + i * (maxX - minX) / numOfTags;

            Text t = new Text(i * width / numOfTags, height + 20, String.valueOf((int) xTag));
            t.setWrappingWidth(15);
            t.setTextAlignment(TextAlignment.CENTER);
            t.setX(i * width / numOfTags - t.getWrappingWidth() / 2);

            Rectangle marker = new Rectangle(2, 7);
            marker.setX(i * width / numOfTags);
            marker.setY(height);
            scatterChartPane.getChildren().add(t);
            scatterChartPane.getChildren().add(marker);
        }

        Text t = new Text(width, height + 50, "Construction year");
        t.setX(width-50);
        t.setFont(new Font("Arial", 15));

        scatterChartPane.getChildren().add(t);

        //y Axis
        for (int i = 0; i <= numOfTags; i++) {
            double yTag = minY + i * (maxY - minY) / numOfTags;

            t = new Text(String.valueOf((int) yTag));
            //t.setWrappingWidth(15);
            t.setTextAlignment(TextAlignment.CENTER);
            t.setX(-35);
            t.setY(height - i * height / numOfTags + 5/*-t.getWrappingHeight()/2*/);

            Rectangle marker = new Rectangle(7, 2);
            marker.setX(-7);
            marker.setY(i * height / numOfTags);
            scatterChartPane.getChildren().add(t);
            scatterChartPane.getChildren().add(marker);
        }

        scatterChartPane.getChildren().add(yAxis);
        scatterChartPane.getChildren().add(xAxis);
    }

    void drawGlyphs() {
        ArrayList<ImageView> images = new ArrayList<>();

        for (Map.Entry<String, ArrayList<Glyph>> entry : americanGlyphs.entrySet()) {
            for (Glyph glyph : entry.getValue()) {
                if (glyph.getVisible() && !allGlyphsWithoutValue.contains(glyph)) {
                    ImageView imageView = addGlyphImageView(glyph);

                    images.add(imageView);
                }
            }
        }
        for (Map.Entry<String, ArrayList<Glyph>> entry : europeanGlyphs.entrySet()) {
            for (Glyph glyph : entry.getValue()) {
                if (glyph.getVisible() && !allGlyphsWithoutValue.contains(glyph)) {
                    ImageView imageView = addGlyphImageView(glyph);

                    images.add(imageView);
                }
            }
        }
        for (Map.Entry<String, ArrayList<Glyph>> entry : japaneseGlyphs.entrySet()) {
            for (Glyph glyph : entry.getValue()) {
                if (glyph.getVisible() && !allGlyphsWithoutValue.contains(glyph)) {
                    ImageView imageView = addGlyphImageView(glyph);

                    images.add(imageView);
                }
            }
        }

        scatterChartPane.getChildren().addAll(borders);
        scatterChartPane.getChildren().addAll(images);
    }

    ImageView addGlyphImageView(Glyph glyph) {
        ImageView imageView = new ImageView();

        //Setting image to the image view
        Logo logo = glyph.getLogo();

        imageView.setImage(logo.getImage());
        imageView.setId(logo.getSlug());
        //Setting the image view parameters
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        //imageView.setPreserveRatio(true);
        imageView.setX(glyph.getPosX() - imageView.getFitWidth() / 2);
        imageView.setY(glyph.getPosY() - imageView.getFitHeight() / 2);

        ColorAdjust colorAdjust = new ColorAdjust();
        //Setting the saturation value
        colorAdjust.setSaturation(-1);
        //Applying coloradjust effect to the ImageView node
        imageView.setEffect(colorAdjust);

        //Adding image view border
        Rectangle rect = new Rectangle();

        if (glyph.getCar().getOrigin().equals("American"))
            rect = new Rectangle(imageView.boundsInParentProperty().get().getWidth() + 4, imageView.boundsInParentProperty().get().getHeight() + 4, Color.RED);
        else if (glyph.getCar().getOrigin().equals("European"))
            rect = new Rectangle(imageView.boundsInParentProperty().get().getWidth() + 4, imageView.boundsInParentProperty().get().getHeight() + 4, Color.BLUE);
        else if (glyph.getCar().getOrigin().equals("Japanese"))
            rect = new Rectangle(imageView.boundsInParentProperty().get().getWidth() + 4, imageView.boundsInParentProperty().get().getHeight() + 4, Color.GREEN);

        rect.setX(imageView.getX() - 2);
        rect.setY(imageView.getY() - 2);
        borders.add(rect);

        //making sure that pngs have a white background, too
        rect = new Rectangle(imageView.boundsInParentProperty().get().getWidth(), imageView.boundsInParentProperty().get().getHeight(), Color.WHITE);

        rect.setX(imageView.getX());
        rect.setY(imageView.getY());
        borders.add(rect);


        imageView.setOnMouseClicked(e -> {
            ImageView v = (ImageView) e.getSource();

            Car car = glyph.getCar();

            detailWindow.setTitle(car.getName());
            detailWindow.setScene(new Scene(generateDetails(v.getImage(), car, averageCar), 600, 400));
            detailWindow.show();
        });

        return imageView;
    }

    //call this on click on a whole region
    void changeGlyphVisibiltyOrigin(String origin, boolean visibility) {
        if (origin.equals("American")) {
            for (Map.Entry<String, ArrayList<Glyph>> entry : americanGlyphs.entrySet()) {
                for (Glyph glyph : entry.getValue()) {
                    glyph.setVisible(visibility);
                }
            }
        } else if (origin.equals("European")) {
            for (Map.Entry<String, ArrayList<Glyph>> entry : europeanGlyphs.entrySet()) {
                for (Glyph glyph : entry.getValue()) {
                    glyph.setVisible(visibility);
                }
            }
        } else if (origin.equals("Japanese")) {
            for (Map.Entry<String, ArrayList<Glyph>> entry : japaneseGlyphs.entrySet()) {
                for (Glyph glyph : entry.getValue()) {
                    glyph.setVisible(visibility);
                }
            }
        }

        generateGlyphCoordinates(yAxisDropdown.getValue());
    }

    //call this on click on a Manufacturer Logo
    void changeGlyphVisibiltyManufacturer(String manufacturer, boolean visibility) {
        for (Map.Entry<String, ArrayList<Glyph>> entry : americanGlyphs.entrySet()) {
            for (Glyph glyph : entry.getValue()) {
                if (glyph.getCar().getManufacturer().equals(manufacturer)) glyph.setVisible(visibility);
            }
        }
        for (Map.Entry<String, ArrayList<Glyph>> entry : europeanGlyphs.entrySet()) {
            for (Glyph glyph : entry.getValue()) {
                if (glyph.getCar().getManufacturer().equals(manufacturer)) glyph.setVisible(visibility);
            }
        }
        for (Map.Entry<String, ArrayList<Glyph>> entry : japaneseGlyphs.entrySet()) {
            for (Glyph glyph : entry.getValue()) {
                if (glyph.getCar().getManufacturer().equals(manufacturer)) glyph.setVisible(visibility);
            }
        }

        generateGlyphCoordinates(yAxisDropdown.getValue().toString());
    }


    GridPane generateDetails(Image manufacturer, Car car, Car averageCar) {
        GridPane pane = new GridPane();
        pane.setHgap(10); // set gap in pixels
        pane.setVgap(10); // set gap in pixels


        pane.add(new Label("Name"), 0, 1, 1, 1);
        pane.add(new Label("Manufacturer"), 0, 2, 1, 1);
        pane.add(new Label("Consumption"), 0, 3, 1, 1);
        pane.add(new Label("Cylinder"), 0, 4, 1, 1);
        pane.add(new Label("Displacement"), 0, 5, 1, 1);
        pane.add(new Label("Horsepower"), 0, 6, 1, 1);
        pane.add(new Label("Weight"), 0, 7, 1, 1);
        pane.add(new Label("Acceleration"), 0, 8, 1, 1);
        pane.add(new Label("Year"), 0, 9, 1, 1);
        pane.add(new Label("Origin"), 0, 10, 1, 1);

        pane.add(new Label(car.getName()), 1, 1, 1, 1);
        pane.add(new Label(car.getManufacturer()), 1, 2, 1, 1);
        pane.add(new Label(car.getConsumption(metric) == null ? "not available" : car.getConsumption(metric).toString()), 1, 3, 1, 1);
        pane.add(new Label(car.getCylinder() == null ? "not available" : car.getCylinder().toString()), 1, 4, 1, 1);
        pane.add(new Label(car.getDisplacement(metric) == null ? "not available" : car.getDisplacement(metric).toString()), 1, 5, 1, 1);
        pane.add(new Label(car.getHorsepower() == null ? "not available" : car.getHorsepower().toString()), 1, 6, 1, 1);
        pane.add(new Label(car.getWeight(metric) == null ? "not available" : car.getWeight(metric).toString()), 1, 7, 1, 1);
        pane.add(new Label(car.getAcceleration() == null ? "not available" : car.getAcceleration().toString()), 1, 8, 1, 1);
        pane.add(new Label(car.getYear() == null ? "not available" : car.getYear().toString()), 1, 9, 1, 1);
        pane.add(new Label(car.getOrigin()), 1, 10, 1, 1);


        pane.add(new Label("s"), 2, 8, 1, 1);

        if (metric) {
            pane.add(new Label("kmpl"), 2, 3, 1, 1);
            pane.add(new Label("ccm"), 2, 5, 1, 1);
            pane.add(new Label("ps"), 2, 6, 1, 1);
            pane.add(new Label("kg"), 2, 7, 1, 1);
        } else {
            pane.add(new Label("mpg"), 2, 3, 1, 1);
            pane.add(new Label("cci"), 2, 5, 1, 1);
            pane.add(new Label("hp"), 2, 6, 1, 1);
            pane.add(new Label("lbs"), 2, 7, 1, 1);
        }


        ImageView imageView = new ImageView();
        imageView.setImage(manufacturer);
        imageView.setX(10);
        imageView.setY(10);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);

        pane.add(new BarChart(averageCar, car, metric), 10, 0, 1, 12);

        pane.add(new Label("Logo"), 0, 0, 1, 1);
        pane.add(imageView, 1, 0, 1, 1);


        return pane;
    }

    public void changedMetric(boolean metric) {
        this.metric = metric;
        generateGlyphCoordinates(yAxisDropdown.getValue());
    }

}
