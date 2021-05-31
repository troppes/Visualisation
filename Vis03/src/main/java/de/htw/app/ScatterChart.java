package de.htw.app;

import de.htw.app.lib.Glyph;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import de.htw.app.model.Car;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ScatterChart {

    public enum origin {AMERICAN, EUROPEAN, JAPANESE};

    int width, height;

    Pane scatterChartPane;

    ArrayList<Glyph> allGlyphs = new ArrayList<Glyph>();

    Map<String, ArrayList<Glyph>> americanGlyphs = new HashMap<>();
    Map<String, ArrayList<Glyph>> europeanGlyphs = new HashMap<>();
    Map<String, ArrayList<Glyph>> japaneseGlyphs = new HashMap<>();

    public ScatterChart(Car[] cars, int width, int height){
        scatterChartPane = new Pane();
        this.width = width;
        this.height = height;

        generateGlyphOriginPairs(cars);

        //dropdown on the lefthand side of the screen
        ObservableList<String> yAxisOptions =
                FXCollections.observableArrayList(
                        "Horsepower",
                        "Km/l",
                        "Cylinders",
                        "Displacement",
                        "PS",
                        "Weight",
                        "Acceleration"
                );
        final ComboBox yAxisDropdown = new ComboBox(yAxisOptions);

        yAxisDropdown.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
                generateGlyphCoordinates(newValue.toString());
            }
        );

        yAxisDropdown.valueProperty();
    }

    void generateGlyphOriginPairs(Car[] cars){
        for(Car car : cars){
            Glyph glyph = new Glyph(car);
            allGlyphs.add(glyph);

            if(car.getOrigin() == "American"){
                if(!americanGlyphs.containsKey(car.getManufacturer())){
                    ArrayList<Glyph> list = new ArrayList<>();
                    list.add(glyph);
                    americanGlyphs.put(car.getManufacturer(), list);
                }
                else{
                    americanGlyphs.get(car.getManufacturer()).add(glyph);
                }
            }
            else if(car.getOrigin() == "European"){
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

    void generateGlyphCoordinates(String yAxisValue){
        generateChartAxis(yAxisValue);

        for(Glyph glyph : allGlyphs){

        }
    }

    void generateChartAxis(String yAxisValue){
        Rectangle yAxis = new Rectangle(1, height);
        yAxis.setX(0);
        yAxis.setY(0);

        Rectangle xAxis = new Rectangle(width, 1);
        xAxis.setX(0);
        xAxis.setY(width);

        scatterChartPane.getChildren().add(yAxis);
        scatterChartPane.getChildren().add(xAxis);
    }
}
