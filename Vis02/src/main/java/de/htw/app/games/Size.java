package de.htw.app.games;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Random;

public class Size extends GameMode{

    //ToDo:
    //Larger set of colors?

    public Size(int levelDimensionX, int levelDimensionY) {
        super(levelDimensionX, levelDimensionY);
        gameMode = possibleModes.SIZE;
    }

    @Override
    public void generateLevel() {
        Random rand = new Random();

        int randomColor = rand.nextInt(2); //0 = red, 1 = blue
        int randomShape = rand.nextInt(2);  //0 = Circle, 1 = Square
        int randomSizeTarget = rand.nextInt(2); //0 = target smaller, 1 = target bigger

        int targetShapeSize = shapeSize*2;
        if(randomSizeTarget==0) targetShapeSize = shapeSize/2;

        //Random Shape
        Shape target;

        if(randomShape == 0){
            target = new Circle(targetShapeSize/2); //so shapeSize is diameter, not radius
            targetX = rand.nextInt(levelDimensionX-targetShapeSize) + targetShapeSize/2;
            targetY = rand.nextInt(levelDimensionY-targetShapeSize) + targetShapeSize/2;

            ((Circle)target).setCenterX(targetX); //random X position
            ((Circle)target).setCenterY(targetY); //random Y position
        }
        else {
            target = new Rectangle(targetShapeSize, targetShapeSize); //so shapeSize is diameter, not radius
            targetX = rand.nextInt(levelDimensionX-targetShapeSize);  //we don't need + shapeSize/2 here, because rectangles get drawn from their top left corner
            targetY = rand.nextInt(levelDimensionY-targetShapeSize);  //we don't need + shapeSize/2 here, because rectangles get drawn from their top left corner

            ((Rectangle)target).setX(targetX); //random X position
            ((Rectangle)target).setY(targetY); //random Y position

            //this is done, so that for the game the official targetX and Y is in the middle of the rectangle
            targetX += shapeSize/2;
            targetY += shapeSize/2;
        }

        target.setFill(javafx.scene.paint.Color.DODGERBLUE);
        if(randomColor == 0) target.setFill(javafx.scene.paint.Color.CRIMSON);

        level.getChildren().add(target);

        ArrayList<Shape> distractors = new ArrayList<>();

        for (int i=0; i<numOfDistractors; i++){
            Shape distractor;
            if(randomShape == 0){
                distractor = new Circle(shapeSize/2); //so shapeSize is diameter, not radius
            }
            else {
                distractor = new Rectangle(shapeSize, shapeSize); //so shapeSize is diameter, not radius
            }

            distractor.setFill(javafx.scene.paint.Color.DODGERBLUE);
            if(randomColor == 0) distractor.setFill(javafx.scene.paint.Color.CRIMSON);

            boolean intersection = true;

            while(intersection){
                if(randomShape == 0){
                    ((Circle)distractor).setCenterX(rand.nextInt(levelDimensionX-shapeSize) + shapeSize/2);
                    ((Circle)distractor).setCenterY(rand.nextInt(levelDimensionY-shapeSize) + shapeSize/2);
                }
                else {
                    ((Rectangle)distractor).setX(rand.nextInt(levelDimensionX-shapeSize));  //we don't need + shapeSize/2 here, because rectangles get drawn from their top left corner
                    ((Rectangle)distractor).setY(rand.nextInt(levelDimensionY-shapeSize));  //we don't need + shapeSize/2 here, because rectangles get drawn from their top left corner
                }

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

        allShapes.add(target);
        allShapes.addAll(distractors);

        applyModifiers();
    }

    @Override
    public float getMeanDistance() {
        return -1;
    }
}
