package de.htw.app.games;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Random;

public class Color extends GameMode{

    possibleModes gameMode = possibleModes.COLOR;

    public Color(int levelDimensionX, int levelDimensionY) {
        super(levelDimensionX, levelDimensionY);
    }

    @Override
    public void generateLevel() {
        level = new Pane();

        level.setMaxSize(levelDimensionX, levelDimensionY);
        Rectangle levelBorder = new Rectangle(levelDimensionX, levelDimensionY);
        levelBorder.setFill(javafx.scene.paint.Color.TRANSPARENT);
        levelBorder.setStroke(javafx.scene.paint.Color.BLACK);

        level.getChildren().addAll(levelBorder);

        Random rand = new Random();

        Circle target = new Circle(shapeSize/2, javafx.scene.paint.Color.CRIMSON); //so shapeSize is diameter, not radius
        targetX = rand.nextInt(levelDimensionX-shapeSize) + shapeSize/2;
        targetY = rand.nextInt(levelDimensionY-shapeSize) + shapeSize/2;

        target.setCenterX(targetX); //random X position
        target.setCenterY(targetY); //random Y position

        level.getChildren().add(target);

        ArrayList<Shape> distractors = new ArrayList<>();

        for (int i=0; i<numOfDistractors; i++){
            Circle distractor = new Circle(shapeSize/2, javafx.scene.paint.Color. 	DODGERBLUE); //so shapeSize is diameter, not radius

            boolean intersection = true;

            while(intersection){
                distractor.setCenterX(rand.nextInt(levelDimensionX-shapeSize) + shapeSize/2); //random X position
                distractor.setCenterY(rand.nextInt(levelDimensionY-shapeSize) + shapeSize/2); //random Y position

                intersection = false;

                //checks if distractor intersects with any other object already drawn into the scene
                if(distractor.intersects(target.getBoundsInParent())){
                    intersection = true;
                }
                else {
                    for (Shape shape : distractors) {
                        if (distractor.intersects(shape.getBoundsInParent())) {
                            intersection = true;
                            break;
                        }
                    }
                }
            }

            distractors.add(distractor);
        }

        level.getChildren().addAll(distractors);
    }

    @Override
    public float getMeanDistance() {
        return -1;
    }
}
