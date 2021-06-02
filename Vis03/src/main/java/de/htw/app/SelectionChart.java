package de.htw.app;

import de.htw.app.lib.Glyph;
import de.htw.app.model.Logo;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Map;

public class SelectionChart {

    Map<String, ArrayList<Glyph>> americanGlyphs;
    Map<String, ArrayList<Glyph>> europeanGlyphs;
    Map<String, ArrayList<Glyph>> japaneseGlyphs;

    ArrayList<Rectangle> borders = new ArrayList<>();   //first store and draw all borders

    Pane selectionPane = new Pane();

    ScatterChart scatterChart;

    boolean americanEnabled = true, europeanEnabled = true, japaneseEnabled = true;

    public SelectionChart(ScatterChart scatterChart, Map<String, ArrayList<Glyph>> americanGlyphs, Map<String, ArrayList<Glyph>> europeanGlyphs, Map<String, ArrayList<Glyph>> japaneseGlyphs, int width){
        this.scatterChart = scatterChart;

        this.americanGlyphs = americanGlyphs;
        this.europeanGlyphs = europeanGlyphs;
        this.japaneseGlyphs = japaneseGlyphs;

        int distanceBetweenOrigins = 20;

        HBox completeBox = new HBox();
        completeBox.setMaxWidth(width);
        completeBox.setMinWidth(width);

        ArrayList<String> addedManufacturers = new ArrayList<>();
        Pane americanPane = generateOriginBox((width-distanceBetweenOrigins)/3, "American", Color.RED);
        americanPane.setPadding(new Insets(5));

        for (Map.Entry<String,ArrayList<Glyph>> entry : americanGlyphs.entrySet()){
            for (Glyph glyph : entry.getValue()) {
                if(!addedManufacturers.contains(glyph.getCar().getManufacturer())){
                    addedManufacturers.add(glyph.getCar().getManufacturer());

                    ImageView imageView = generateGlyphImageView(glyph);

                    System.out.println("adding american car " + glyph.getCar().getManufacturer());

                    americanPane.getChildren().addAll(imageView);
                }
            }
        }

        System.out.println(americanPane.getWidth());

        addedManufacturers = new ArrayList<>();
        Pane europeanPane = generateOriginBox((width-distanceBetweenOrigins)/3, "European", Color.BLUE);
        europeanPane.setPadding(new Insets(5));

        for (Map.Entry<String,ArrayList<Glyph>> entry : europeanGlyphs.entrySet()){
            for (Glyph glyph : entry.getValue()) {
                if(!addedManufacturers.contains(glyph.getCar().getManufacturer())){
                    addedManufacturers.add(glyph.getCar().getManufacturer());

                    ImageView imageView = generateGlyphImageView(glyph);

                    europeanPane.getChildren().add(imageView);
                }
            }
        }

        addedManufacturers = new ArrayList<>();
        Pane japanesePane = generateOriginBox((width-distanceBetweenOrigins)/3, "Japanese", Color.GREEN);
        japanesePane.setPadding(new Insets(5));

        for (Map.Entry<String,ArrayList<Glyph>> entry : japaneseGlyphs.entrySet()){
            for (Glyph glyph : entry.getValue()) {
                if(!addedManufacturers.contains(glyph.getCar().getManufacturer())){
                    addedManufacturers.add(glyph.getCar().getManufacturer());

                    ImageView imageView = generateGlyphImageView(glyph);

                    japanesePane.getChildren().add(imageView);
                }
            }
        }

        completeBox.getChildren().addAll(americanPane, europeanPane, japanesePane);

        selectionPane.getChildren().addAll(completeBox);
    }

    ImageView generateGlyphImageView(Glyph glyph){

        ImageView imageView = new ImageView();

        //Setting image to the image view
        Logo logo = glyph.getLogo();

        imageView.setImage(logo.getImage());
        imageView.setId(logo.getSlug());
        //Setting the image view parameters
        imageView.setFitWidth(70);
        imageView.setPreserveRatio(true);
        //imageView.setX(glyph.getPosX() - imageView.getFitWidth()/2);
        //imageView.setY(glyph.getPosY()- imageView.getFitHeight()/2);

        //ColorAdjust colorAdjust = new ColorAdjust();
        //Setting the saturation value
        //colorAdjust.setSaturation(-1);
        //Applying coloradjust effect to the ImageView node
        //imageView.setEffect(colorAdjust);

        imageView.setOnMouseClicked(e -> {
            scatterChart.changeGlyphVisibiltyManufacturer(glyph.getCar().getManufacturer(), !glyph.getVisible());
        });

        return imageView;
    }

    Pane generateOriginBox(int width, String origin, Color color){
        Pane pane = new Pane();

        Text t = new Text(0, 0, origin);
        t.setWrappingWidth(width);
        t.setTextAlignment(TextAlignment.CENTER);
        t.setY(-5);  //ToDo: Find a way to get height of these tags

        t.setOnMouseClicked(e -> {
            boolean b = false;

            if(origin.equals("American")) {
                americanEnabled = americanEnabled == true ? false : true;
                b=americanEnabled;
            }
            else if(origin.equals("European")) {
                europeanEnabled = europeanEnabled == true ? false : true;
                b=europeanEnabled;
            }
            else if(origin.equals("Japanese")) {
                japaneseEnabled = japaneseEnabled == true ? false : true;
                b=japaneseEnabled;
            }

            scatterChart.changeGlyphVisibiltyOrigin(t.getText(), b);
        });

        pane.setBorder(new Border(new BorderStroke(color, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        pane.setPadding(new Insets(10));
        pane.getChildren().add(t);

        return pane;
    }

    public Pane getSelectionPane(){
        return selectionPane;
    }
}
