package de.htw.app.games;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Random;

public abstract class GameMode {
    //Class Mode/-s
    public enum possibleModes {COLOR, ORIENTATION, SIZE, GROUPING } // Statt SIZE/SHAPE = ROTATION
    possibleModes[] distractors = new possibleModes[0]; //to evade nullpointer Reference
    possibleModes gameMode = null;

    //level generation data
    int shapeSize = 20;
    int numOfDistractors = 150;
    ArrayList<Shape> allShapes = new ArrayList<>();

    Pane level = new Pane();
    int levelDimensionX;
    int levelDimensionY;

    //game logic data
    int targetX, targetY;

    //tracking data
    float lowestTime = -1;

    public GameMode(int levelDimensionX, int levelDimensionY){
        this.levelDimensionX = levelDimensionX;
        this.levelDimensionY = levelDimensionY;

        level.setMaxSize(levelDimensionX, levelDimensionY);
        Rectangle levelBorder = new Rectangle(levelDimensionX, levelDimensionY);
        levelBorder.setFill(javafx.scene.paint.Color.TRANSPARENT);
        levelBorder.setStroke(javafx.scene.paint.Color.BLACK);

        level.getChildren().addAll(levelBorder);
    }
    public void generateLevel() {}
    public float getMeanDistance() {return -1;}
    public Pane getLevel() {return level;}
    public float getLowestTime() {return lowestTime;}
    public void setLowestTime(float time) {lowestTime = time;}
    public int getTargetX(){return targetX;}
    public int getTargetY(){return targetY;}
    public void setDisctractors(possibleModes[] disctractors){this.distractors=disctractors;}

    void applyModifiers(){
        for (possibleModes gameMode : distractors){
            if(gameMode == possibleModes.COLOR){
                applyColorDistractor(allShapes);
            }
            else if(gameMode == possibleModes.ORIENTATION){
                applyOrientationDistractor(allShapes);
            }
            else if(gameMode == possibleModes.SIZE){
                applySizeDistractor(allShapes);
            }
        }
    }

    public void applyColorDistractor(ArrayList<Shape> shapes){
        Random rand = new Random();

        for (Shape shape : shapes) {
            applyRandomColor(shape, rand.nextInt(10));
        }

    }
    void applyRandomColor(Shape shape, int randomNumber){
        switch(randomNumber){
            case 0:
                shape.setFill(javafx.scene.paint.Color.CRIMSON);
                break;
            case 1:
                shape.setFill(javafx.scene.paint.Color.DODGERBLUE);
                break;
            case 2:
                shape.setFill(javafx.scene.paint.Color.BLUEVIOLET);
                break;
            case 3:
                shape.setFill(javafx.scene.paint.Color.DARKCYAN);
                break;
            case 4:
                shape.setFill(javafx.scene.paint.Color.DARKGOLDENROD);
                break;
            case 5:
                shape.setFill(javafx.scene.paint.Color.DARKGREEN);
                break;
            case 6:
                shape.setFill(javafx.scene.paint.Color.DEEPPINK);
                break;
            case 7:
                shape.setFill(javafx.scene.paint.Color.LIGHTCORAL);
                break;
            case 8:
                shape.setFill(javafx.scene.paint.Color.BLACK);
                break;
            case 9:
                shape.setFill(javafx.scene.paint.Color.ORANGERED);
                break;
        }

    }

    public void applyOrientationDistractor(ArrayList<Shape> shapes){
        Random rand = new Random();

        for (Shape shape : shapes) {
            shape.setRotate(rand.nextInt(360));
        }
    }

    public void applySizeDistractor(ArrayList<Shape> shapes) {
        Random rand = new Random();

        for (Shape shape : shapes) {
            float randomSizeModifier = rand.nextFloat()/2.5f+.8f;    //random float between .8 and 1.2

            if(shape.getClass() == Circle.class){
                ((Circle)shape).setRadius((shapeSize/2)*randomSizeModifier);
            }
            else if(shape.getClass() == Rectangle.class){
                ((Rectangle)shape).setWidth((shapeSize)*randomSizeModifier);
                ((Rectangle)shape).setHeight((shapeSize)*randomSizeModifier);
            }
        }
    }
}
