package de.htw.app.games;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Random;

public class Orientation extends GameMode{

    //ToDo:
    //Larger set of colors?


    public Orientation(int levelDimensionX, int levelDimensionY) {
        super(levelDimensionX, levelDimensionY);
        gameMode = possibleModes.ORIENTATION;
    }

    @Override
    public void generateLevel() {
        Random rand = new Random();

        int randomRotationTarget = rand.nextInt(2);  //0 = Target 0 degrees, 1 = Target 45 Degrees
        int randomColor = rand.nextInt(2); //0 = red, 1 = blue

        Rectangle target = new Rectangle(shapeSize, shapeSize); //so shapeSize is diameter, not radius
        targetX = rand.nextInt(levelDimensionX-shapeSize);  //we don't need + shapeSize/2 here, because rectangles get drawn from their top left corner
        targetY = rand.nextInt(levelDimensionY-shapeSize);  //we don't need + shapeSize/2 here, because rectangles get drawn from their top left corner

        target.setX(targetX); //random X position
        target.setY(targetY); //random Y position

        //this is done, so that for the game the official targetX and Y is in the middle of the rectangle
        targetX += shapeSize/2;
        targetY += shapeSize/2;

        target.setFill(javafx.scene.paint.Color.DODGERBLUE);
        if(randomColor == 0) target.setFill(javafx.scene.paint.Color.CRIMSON);

        if(randomRotationTarget==1) {
            target.setRotate(45);
        }

        level.getChildren().add(target);

        ArrayList<Shape> distractors = new ArrayList<>();

        for (int i=0; i<numOfDistractors; i++){
            Rectangle distractor = new Rectangle(shapeSize, shapeSize); //so shapeSize is diameter, not radius

            distractor.setFill(javafx.scene.paint.Color.DODGERBLUE);
            if(randomColor == 0) distractor.setFill(javafx.scene.paint.Color.CRIMSON);

            boolean intersection = true;

            while(intersection){
                distractor.setX(rand.nextInt(levelDimensionX-shapeSize));  //we don't need + shapeSize/2 here, because rectangles get drawn from their top left corner
                distractor.setY(rand.nextInt(levelDimensionY-shapeSize));  //we don't need + shapeSize/2 here, because rectangles get drawn from their top left corner

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

            if(randomRotationTarget==0) { //ToDo: put back in
                distractor.setRotate(45);
            }

            distractors.add(distractor);
        }

        level.getChildren().addAll(distractors);

        allShapes.add(target);
        allShapes.addAll(distractors);

        applyModifiers();
    }
}
