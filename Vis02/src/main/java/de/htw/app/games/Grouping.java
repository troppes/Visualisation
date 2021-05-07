package de.htw.app.games;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Random;

public class Grouping extends GameMode{

    public Grouping(int levelDimensionX, int levelDimensionY) {
        super(levelDimensionX, levelDimensionY);
        gameMode = possibleModes.GROUPING;

        numOfDistractors/=10;
    }

    @Override
    public void generateLevel() {
        Random rand = new Random();

        int randomColor = rand.nextInt(2); //0 = red, 1 = blue
        int randomShape = rand.nextInt(2);  //0 = Circle, 1 = Square
        int randomTargetGrouping = rand.nextInt(3)+1;  //1, 2, 3
        int randomDisctractionGrouping = rand.nextInt(3)+1;  //1, 2, 3

        while (randomDisctractionGrouping==randomTargetGrouping) randomDisctractionGrouping = rand.nextInt(3)+1;

        int groupingSize = (int)(shapeSize*1.75);

        Circle targetGrouping = new Circle(groupingSize, Color.TRANSPARENT); //so shapeSize is diameter, not radius
        targetX = rand.nextInt(levelDimensionX-groupingSize*2) + groupingSize;
        targetY = rand.nextInt(levelDimensionY-groupingSize*2) + groupingSize;

        targetGrouping.setCenterX(targetX); //random X position
        targetGrouping.setCenterY(targetY); //random Y position

        level.getChildren().add(targetGrouping);

        //generates shapes within the grouping
        level.getChildren().addAll(generateGroupingElements(randomTargetGrouping, randomShape, randomColor, groupingSize, targetX, targetY));

        ArrayList<Shape> distractors = new ArrayList<>();
        int cantFindPositionCounter;

        loop:
        for (int i=0; i<numOfDistractors; i++){
            cantFindPositionCounter = 0;

            Circle distractorGrouping = new Circle(groupingSize, Color.TRANSPARENT); //so shapeSize is diameter, not radius

            boolean intersection = true;

            while(intersection){
                cantFindPositionCounter++;

                distractorGrouping.setCenterX(rand.nextInt(levelDimensionX-groupingSize*2) + groupingSize); //random X position
                distractorGrouping.setCenterY(rand.nextInt(levelDimensionY-groupingSize*2) + groupingSize); //random Y position

                intersection = false;

                //checks if distractor intersects with any other object already drawn into the scene
                if(distractorGrouping.intersects(targetGrouping.getBoundsInParent())){
                    intersection = true;
                }
                else {
                    for (Shape shape : distractors) {
                        if (distractorGrouping.intersects(shape.getBoundsInParent())) {
                            intersection = true;
                            break;
                        }
                    }
                }

                if(cantFindPositionCounter >= 100) break loop;   //if random grouping position can't be found for 100 times then the loop gets terminated, as no available spots seem to exist anymore
            }

            distractors.add(distractorGrouping);
        }

        level.getChildren().addAll(distractors);

        //add all elements inside of the groupings
        for (Shape distractorGrouping : distractors) level.getChildren().addAll(generateGroupingElements(randomDisctractionGrouping, randomShape, randomColor, groupingSize, (int)((Circle)distractorGrouping).getCenterX(), (int)((Circle)distractorGrouping).getCenterY()));

        applyModifiers();
    }

    ArrayList<Shape> generateGroupingElements(int groupingNumber, int randomShape, int randomColor, int groupingSize,  int groupingX, int groupingY){
        Random rand = new Random();
        ArrayList<Shape> shapes = new ArrayList<>();

        //adjusting grouping size, x and Y, so that objects don't go over the groupings borders
        if(randomShape==0){
            groupingSize -= shapeSize/2;
        }
        else{   //X and Y have to be adjusted, cause Squares get drawn from the upper left corner
            groupingSize -= shapeSize/2;
            groupingX -= shapeSize/2;
            groupingY -= shapeSize/2;
        }

        for (int i=0; i<groupingNumber; i++) {
            Shape groupingItem;

            if(randomShape == 0){
                groupingItem = new Circle(shapeSize / 2.0); //so shapeSize is diameter, not radius
            }
            else {
                groupingItem = new Rectangle(shapeSize, shapeSize); //so shapeSize is diameter, not radius
            }

            groupingItem.setFill(Color.DODGERBLUE);
            if(randomColor == 0) groupingItem.setFill(Color.CRIMSON);

            boolean intersection = true;

            while(intersection){

                double randomNormalizedValue = rand.nextDouble();

                double a = randomNormalizedValue * 2 * Math.PI;
                double r = groupingSize * Math.sqrt(randomNormalizedValue);

                double x = r * Math.cos(a);
                double y = r * Math.sin(a);

                if(randomShape == 0){
                    ((Circle)groupingItem).setCenterX(x + groupingX); //random X position
                    ((Circle)groupingItem).setCenterY(y + groupingY); //random Y position
                }
                else {
                    ((Rectangle)groupingItem).setX(x + groupingX); //random X position
                    ((Rectangle)groupingItem).setY(y + groupingY); //random Y position
                }

                intersection = false;

                //checks if distractor intersects with any other object already drawn into the grouping
                for (Shape shape : shapes) {
                    if (groupingItem.intersects(shape.getBoundsInParent())) {
                        intersection = true;
                        break;
                    }
                }
            }

            shapes.add(groupingItem);
        }

        allShapes.addAll(shapes);

        return shapes;
    }
}
