package de.htw.app;

import de.htw.app.lib.Glyph;
import de.htw.app.model.Logo;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Map;

public class SelectionChart {

    Map<String, ArrayList<Glyph>> americanGlyphs;
    Map<String, ArrayList<Glyph>> europeanGlyphs;
    Map<String, ArrayList<Glyph>> japaneseGlyphs;

    Pane selectionPane = new Pane();

    ScatterChart scatterChart;

    boolean americanEnabled = true, europeanEnabled = true, japaneseEnabled = true;

    public SelectionChart(ScatterChart scatterChart, Map<String, ArrayList<Glyph>> americanGlyphs, Map<String, ArrayList<Glyph>> europeanGlyphs, Map<String, ArrayList<Glyph>> japaneseGlyphs, int width){
        this.scatterChart = scatterChart;
        int imageWidth = 70;
        int imageDistance = 75;
        int x = imageWidth*2/3;
        int y = 10;
        int leftSpacing = 170;

        this.americanGlyphs = americanGlyphs;
        this.europeanGlyphs = europeanGlyphs;
        this.japaneseGlyphs = japaneseGlyphs;

        int distanceBetweenOrigins = 100;

        HBox completeBox = new HBox();
        completeBox.setMaxWidth(width+leftSpacing);
        completeBox.setMinWidth(width+leftSpacing);
        completeBox.setSpacing(distanceBetweenOrigins);
        completeBox.setPadding(new Insets(0, 0, 0, leftSpacing));

        ArrayList<String> addedManufacturers = new ArrayList<>();
        Pane americanPane = generateOriginBox((width-leftSpacing)/3, "American", Color.RED);

        for (Map.Entry<String,ArrayList<Glyph>> entry : americanGlyphs.entrySet()){
            for (Glyph glyph : entry.getValue()) {
                if(!addedManufacturers.contains(glyph.getCar().getManufacturer())){
                    addedManufacturers.add(glyph.getCar().getManufacturer());

                    if(x+imageWidth>(width-distanceBetweenOrigins)/3) {
                        x = imageWidth*2/3;
                        y += 75;
                    }

                    ImageView imageView = generateGlyphImageView(glyph, x, y, imageWidth);

                    americanPane.getChildren().addAll(imageView);

                    x+=imageDistance;
                }
            }
        }

        x = imageWidth*2/3;
        y = 10;

        addedManufacturers = new ArrayList<>();
        Pane europeanPane = generateOriginBox((width-leftSpacing)/3, "European", Color.BLUE);

        for (Map.Entry<String,ArrayList<Glyph>> entry : europeanGlyphs.entrySet()){
            for (Glyph glyph : entry.getValue()) {
                if(!addedManufacturers.contains(glyph.getCar().getManufacturer())){
                    addedManufacturers.add(glyph.getCar().getManufacturer());

                    if(x+imageWidth>(width-distanceBetweenOrigins)/3) {
                        x = imageWidth*2/3;
                        y += 75;
                    }

                    ImageView imageView = generateGlyphImageView(glyph, x, y, imageWidth);

                    europeanPane.getChildren().add(imageView);

                    x+=imageDistance;
                }
            }
        }

        x = imageWidth*2/3;
        y = 10;

        addedManufacturers = new ArrayList<>();
        Pane japanesePane = generateOriginBox((width-leftSpacing)/3, "Japanese", Color.GREEN);

        for (Map.Entry<String,ArrayList<Glyph>> entry : japaneseGlyphs.entrySet()){
            for (Glyph glyph : entry.getValue()) {
                if(!addedManufacturers.contains(glyph.getCar().getManufacturer())){
                    addedManufacturers.add(glyph.getCar().getManufacturer());

                    if(x+imageWidth>(width-distanceBetweenOrigins)/3) {
                        x = imageWidth*2/3;
                        y += 75;
                    }

                    ImageView imageView = generateGlyphImageView(glyph, x, y, imageWidth);

                    japanesePane.getChildren().add(imageView);

                    x+=imageDistance;
                }
            }
        }

        completeBox.getChildren().addAll(americanPane, europeanPane, japanesePane);

        selectionPane.getChildren().addAll(completeBox);
    }

    ImageView generateGlyphImageView(Glyph glyph, int posX, int posY, int imageWidth){

        ImageView imageView = new ImageView();

        //Setting image to the image view
        Logo logo = glyph.getLogo();

        imageView.setImage(logo.getImage());
        imageView.setId(logo.getSlug());
        //Setting the image view parameters
        imageView.setFitWidth(imageWidth);
        imageView.setPreserveRatio(true);
        imageView.setX(posX - imageView.getFitWidth()/2);
        imageView.setY(posY- imageView.getFitHeight()/2);

        System.out.println("image X: " + imageView.getX());

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
        t.setFont(new Font("Arial", 20));
        t.setWrappingWidth(width);
        t.setTextAlignment(TextAlignment.CENTER);
        t.setY(-5);
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
