package de.htw.app;

import de.htw.app.lib.Glyph;
import de.htw.app.model.Logo;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import de.htw.app.model.Car;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ScatterChart {

    Stage detailWindow = new Stage();

    List<Car> cars;
    List<Logo> logos;

    int width, height;
    boolean metric;

    BorderPane wholeWindow;
    Pane scatterChartPane;
    ComboBox yAxisDropdown;

    ArrayList<Glyph> allGlyphs = new ArrayList<>();
    ArrayList<Glyph> allGlyphsWithoutValue;

    Map<String, ArrayList<Glyph>> americanGlyphs = new HashMap<>();
    Map<String, ArrayList<Glyph>> europeanGlyphs = new HashMap<>();
    Map<String, ArrayList<Glyph>> japaneseGlyphs = new HashMap<>();

    public ScatterChart(List<Car> cars, List<Logo> logos, int width, int height){
        wholeWindow = new BorderPane();
        scatterChartPane = new Pane();
        this.width = width;
        this.height = height;
        this.cars = cars;
        this.logos = logos;

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
        yAxisDropdown = new ComboBox(yAxisOptions);
        yAxisDropdown.setValue("Horsepower");

        yAxisDropdown.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
                generateGlyphCoordinates(newValue.toString());
            }
        );
        HBox hbox = new HBox();
        hbox.getChildren().add(yAxisDropdown);
        hbox.setPadding(new Insets(0, 50, 0, 0));

        wholeWindow.setLeft(hbox);
        wholeWindow.setCenter(scatterChartPane);

        wholeWindow.setPadding(new Insets(20));
        scatterChartPane.setPadding(new Insets(50));
        //ToDo: Add Box with all selectable logos

        generateGlyphCoordinates(yAxisDropdown.getValue().toString());
    }

    public Pane getPane() {
        return wholeWindow;
    }

    void generateGlyphOriginPairs(){
        for(Car car : cars){
            Glyph glyph = new Glyph(car, logos);
            allGlyphs.add(glyph);

            if(car.getOrigin().equals("American")){
                if(!americanGlyphs.containsKey(car.getManufacturer())){
                    ArrayList<Glyph> list = new ArrayList<>();
                    list.add(glyph);
                    americanGlyphs.put(car.getManufacturer(), list);
                }
                else{
                    americanGlyphs.get(car.getManufacturer()).add(glyph);
                }
            }
            else if(car.getOrigin().equals("European")){
                if(!europeanGlyphs.containsKey(car.getManufacturer())){
                    ArrayList<Glyph> list = new ArrayList<>();
                    list.add(glyph);
                    europeanGlyphs.put(car.getManufacturer(), list);
                }
                else{
                    europeanGlyphs.get(car.getManufacturer()).add(glyph);
                }
            }
            else{
                if(!japaneseGlyphs.containsKey(car.getManufacturer())){
                    ArrayList<Glyph> list = new ArrayList<>();
                    list.add(glyph);
                    japaneseGlyphs.put(car.getManufacturer(), list);
                }
                else{
                    japaneseGlyphs.get(car.getManufacturer()).add(glyph);
                }
            }
        }
    }

    //Change string to the enum Flo created in car
    void generateGlyphCoordinates(String yAxisValue){
        scatterChartPane.getChildren().clear();
        allGlyphsWithoutValue = new ArrayList<>();

        double maxYValue = 0;
        double minYValue = 0;
        double maxXValue = 0;
        double minXValue = 70;
        for(Glyph glyph : allGlyphs){
            double yValueToCompare = -1;
            double xValueToCompare = glyph.getCar().getYear();

            switch (yAxisValue){
                case "Horsepower":
                    if(glyph.getCar().getHorsepower() != null)
                        yValueToCompare = glyph.getCar().getHorsepower();
                    else
                        allGlyphsWithoutValue.add(glyph);
                    break;
                case "Km/l":
                    if(glyph.getCar().getConsumption(true) != null)
                        yValueToCompare = glyph.getCar().getConsumption(metric);
                    else
                        allGlyphsWithoutValue.add(glyph);
                    break;
                case "Cylinders":
                    if(glyph.getCar().getCylinder() != null)
                        yValueToCompare = glyph.getCar().getCylinder();
                    else
                        allGlyphsWithoutValue.add(glyph);
                    break;
                case "Displacement":
                    if(glyph.getCar().getDisplacement(true) != null)
                        yValueToCompare = glyph.getCar().getDisplacement(metric);
                    else
                        allGlyphsWithoutValue.add(glyph);
                    break;
                case "Weight":
                    if(glyph.getCar().getWeight(true) != null)
                        yValueToCompare = glyph.getCar().getWeight(metric);
                    else
                        allGlyphsWithoutValue.add(glyph);
                    break;
                case "Acceleration":
                    if(glyph.getCar().getAcceleration() != null)
                        yValueToCompare = glyph.getCar().getAcceleration();
                    else
                        allGlyphsWithoutValue.add(glyph);
                    break;
            }

            if(yValueToCompare != -1 && yValueToCompare > maxYValue){   //if y is not null
                maxYValue = yValueToCompare;
            }
            else if(yValueToCompare != -1 && yValueToCompare < minYValue){
                minYValue = yValueToCompare;
            }

            if(xValueToCompare > maxXValue){
                maxXValue = xValueToCompare;
            }
            else if(xValueToCompare < minXValue){
                minXValue = xValueToCompare;
            }
        }

        //to leave a little space to the left and right of the graph
        maxXValue+=1;
        minXValue-=1;

        //normalize and set position
        for(Glyph glyph : allGlyphs){
            double x = glyph.getCar().getYear();
            double y = -1;

            switch (yAxisValue){
                case "Horsepower":
                    if(glyph.getCar().getHorsepower() != null)
                        y = glyph.getCar().getHorsepower();
                    break;
                case "Km/l":
                    if(glyph.getCar().getConsumption(true) != null)
                        y = glyph.getCar().getConsumption(metric);
                    break;
                case "Cylinders":
                    if(glyph.getCar().getCylinder() != null)
                        y = glyph.getCar().getCylinder();
                    break;
                case "Displacement":
                    if(glyph.getCar().getDisplacement(true) != null)
                        y = glyph.getCar().getDisplacement(metric);
                    break;
                case "Weight":
                    if(glyph.getCar().getWeight(true) != null)
                        y = glyph.getCar().getWeight(metric);
                    break;
                case "Acceleration":
                    if(glyph.getCar().getAcceleration() != null)
                        y = glyph.getCar().getAcceleration();
                    break;
            }

            //https://stats.stackexchange.com/questions/281162/scale-a-number-between-a-range
            x=(x-minXValue)/(maxXValue-minXValue)*(width-0)+0;
            if(y>=0) y=(y-minYValue)/(maxYValue-minYValue)*(height-0)+0;   //if y is not null   //ToDo: Reverse y here, as picture is drawn top left to bottom right
            else y=-5000;   //ToDo: Placeholder. Maybe don't display it at all if that's the case
            y = height - y;

            glyph.setPosition(x, y);
        }

        generateChartAxis(minXValue, maxXValue, minYValue, maxYValue);
        drawGlyphs();
    }

    void generateChartAxis(double minX, double maxX, double minY, double maxY){
        Rectangle yAxis = new Rectangle(2, height);
        yAxis.setX(0);
        yAxis.setY(0);

        Rectangle xAxis = new Rectangle(width, 2);
        xAxis.setX(0);
        xAxis.setY(height);

        int numOfTags = 14;
        //x Axis
        for(int i=0; i<=numOfTags; i++){
            double xTag = minX + i*(maxX-minX)/numOfTags;

            Text t = new Text(i*width/numOfTags, height+20, String.valueOf((int)xTag));
            t.setWrappingWidth(15);
            t.setTextAlignment(TextAlignment.CENTER);
            t.setX(i*width/numOfTags-t.getWrappingWidth()/2);

            Rectangle marker = new Rectangle(2, 7);
            marker.setX(i*width/numOfTags);
            marker.setY(height);
            scatterChartPane.getChildren().add(t);
            scatterChartPane.getChildren().add(marker);
        }

        //y Axis
        for(int i=0; i<=numOfTags; i++){
            double yTag = minY + i*(maxY-minY)/numOfTags;

            Text t = new Text(String.valueOf((int)yTag));
            //t.setWrappingWidth(15);
            t.setTextAlignment(TextAlignment.CENTER);
            t.setX(-35);
            t.setY(height - i*height/numOfTags + 5/*-t.getWrappingHeight()/2*/);  //ToDo: Find a way to get height of these tags

            Rectangle marker = new Rectangle(7, 2);
            marker.setX(-7);
            marker.setY(i*height/numOfTags);
            scatterChartPane.getChildren().add(t);
            scatterChartPane.getChildren().add(marker);
        }

        scatterChartPane.getChildren().add(yAxis);
        scatterChartPane.getChildren().add(xAxis);
    }

    void drawGlyphs(){
        for (Map.Entry<String,ArrayList<Glyph>> entry : americanGlyphs.entrySet()){
            for (Glyph glyph : entry.getValue()) {
                if(glyph.getVisible() && !allGlyphsWithoutValue.contains(glyph)){
                    ImageView imageView = addGlyphImageView(glyph);

                    scatterChartPane.getChildren().add(imageView);
                }
            }
        }
        for (Map.Entry<String,ArrayList<Glyph>> entry : europeanGlyphs.entrySet()){
            for (Glyph glyph : entry.getValue()) {
                if(glyph.getVisible() && !allGlyphsWithoutValue.contains(glyph)){
                    ImageView imageView = addGlyphImageView(glyph);

                    scatterChartPane.getChildren().add(imageView);
                }
            }
        }
        for (Map.Entry<String,ArrayList<Glyph>> entry : japaneseGlyphs.entrySet()){
            for (Glyph glyph : entry.getValue()) {
                if(glyph.getVisible() && !allGlyphsWithoutValue.contains(glyph)){
                    ImageView imageView = addGlyphImageView(glyph);

                    scatterChartPane.getChildren().add(imageView);
                }
            }
        }
    }

    ImageView addGlyphImageView(Glyph glyph){
        ImageView imageView = new ImageView();

        //Setting image to the image view
        Logo logo = glyph.getLogo();

        imageView.setImage(logo.getImage());
        imageView.setId(logo.getSlug());
        //Setting the image view parameters
        imageView.setFitWidth(70);
        imageView.setPreserveRatio(true);
        imageView.setX(glyph.getPosX() - imageView.getFitWidth()/2);
        imageView.setY(glyph.getPosY()- imageView.getFitHeight()/2);

        ColorAdjust colorAdjust = new ColorAdjust();
        //Setting the saturation value
        colorAdjust.setSaturation(-1);
        //Applying coloradjust effect to the ImageView node
        imageView.setEffect(colorAdjust);

        imageView.setOnMouseClicked(e -> {
            ImageView v = (ImageView) e.getSource();

            Car car = glyph.getCar();

            detailWindow.setTitle(car.getName());
            detailWindow.setScene(new Scene(generateDetails(v.getImage(), car), 450, 450));
            detailWindow.show();
        });

        return imageView;
    }

    //call this on click on a whole region
    void changeGlyphVisibilty(Map<String, ArrayList<Glyph>> origin, boolean visibility){
        for (Map.Entry<String,ArrayList<Glyph>> entry : origin.entrySet()){
            for (Glyph glyph : entry.getValue()) {
                glyph.setVisible(visibility);
            }
        }
    }

    //call this on click on a Manufacturer Logo
    void changeGlyphVisibilty(ArrayList<Glyph> manufacturer, boolean visibility){
        for (Glyph glyph : manufacturer) {
            glyph.setVisible(visibility);
        }
    }


    GridPane generateDetails(Image manufacturer, Car car) {
        GridPane pane = new GridPane();
        pane.setHgap(10); // set gap in pixels
        pane.setVgap(10); // set gap in pixels


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
        pane.add(new Label(car.getConsumption(metric) == null ? "not available" : car.getConsumption(metric).toString()), 1, 2, 1, 1);
        pane.add(new Label(car.getCylinder() == null ? "not available" : car.getCylinder().toString()), 1, 3, 1, 1);
        pane.add(new Label(car.getDisplacement(metric) == null ? "not available" : car.getDisplacement(metric).toString()), 1, 4, 1, 1);
        pane.add(new Label(car.getHorsepower() == null ? "not available" : car.getHorsepower().toString()), 1, 5, 1, 1);
        pane.add(new Label(car.getWeight(metric) == null ? "not available" : car.getWeight(metric).toString()), 1, 6, 1, 1);
        pane.add(new Label(car.getAcceleration() == null ? "not available" : car.getAcceleration().toString()), 1, 7, 1, 1);
        pane.add(new Label(car.getYear() == null ? "not available" : car.getYear().toString()), 1, 8, 1, 1);
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

    public void changedMetric(boolean metric){
        this.metric = metric;
        generateGlyphCoordinates(yAxisDropdown.getValue().toString());
    }

}
