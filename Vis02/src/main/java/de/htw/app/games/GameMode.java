package de.htw.app.games;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.*;

public class GameMode {

    @JsonProperty
    int id;

    //Class Mode/-s
    public enum possibleModes {COLOR, ORIENTATION, SIZE, GROUPING} // Statt SIZE/SHAPE = ROTATION

    possibleModes[] distractors = new possibleModes[0]; //to evade nullpointer Reference
    @JsonProperty("game_mode")
    possibleModes gameMode = null;

    //level generation data

    @JsonIgnore
    int shapeSize = 20;
    @JsonIgnore
    int numOfDistractors = 150;
    @JsonIgnore
    ArrayList<Shape> allShapes = new ArrayList<>();

    @JsonIgnore
    Pane level = new Pane();
    @JsonIgnore
    int levelDimensionX;
    @JsonIgnore
    int levelDimensionY;

    //game logic data
    @JsonIgnore
    int targetX, targetY;

    //tracking data
    float lowestTime = -1;
    float meanDistance = 0;
    int distanceCounter = 0;

    public GameMode() {
    }

    public GameMode(int levelDimensionX, int levelDimensionY) {
        this.levelDimensionX = levelDimensionX;
        this.levelDimensionY = levelDimensionY;

        level.setMaxSize(levelDimensionX, levelDimensionY);
    }

    public void generateLevel() {
    }

    public possibleModes getGameMode() {
        return gameMode;
    }

    @JsonGetter("distractors")
    public possibleModes[] getDistractors() {
        return distractors;
    }

    public Pane getLevel() {
        return level;
    }

    @JsonGetter("lowest_time")
    public float getLowestTime() {
        return lowestTime;
    }

    @JsonSetter("lowest_time")
    public void setLowestTime(float time) {
        lowestTime = time;
    }

    @JsonGetter("mean_distance")
    public float getMeanDistance() {
        if (distanceCounter == 0) return -1;

        return meanDistance / distanceCounter;
    }

    @JsonSetter("mean_distance")
    public void setMeanDistance(float meanDistance) {
        distanceCounter = 1;
        this.meanDistance = meanDistance;
    }

    public void addDistance(float distance) {
        distanceCounter++;
        this.meanDistance += distance;
    }

    public int getTargetX() {
        return targetX;
    }

    public int getTargetY() {
        return targetY;
    }

    @JsonSetter("distractors")
    public void setDistractors(possibleModes[] distractors) {
        if(distractors == null){
            this.distractors = new possibleModes[]{};
        } else{
            this.distractors = distractors;
        }
    }

    void applyModifiers() {
        for (possibleModes gameMode : distractors) {
            if (gameMode == possibleModes.COLOR) {
                applyColorDistractor(allShapes);
            } else if (gameMode == possibleModes.ORIENTATION) {
                applyOrientationDistractor(allShapes);
            } else if (gameMode == possibleModes.SIZE) {
                applySizeDistractor(allShapes);
            }
        }
    }

    public void applyColorDistractor(ArrayList<Shape> shapes) {
        Random rand = new Random();

        for (Shape shape : shapes) {
            applyRandomColor(shape, rand.nextInt(10));
        }

    }

    void applyRandomColor(Shape shape, int randomNumber) {
        switch (randomNumber) {
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

    public void applyOrientationDistractor(ArrayList<Shape> shapes) {
        Random rand = new Random();

        for (Shape shape : shapes) {
            shape.setRotate(rand.nextInt(360));
        }
    }

    public void applySizeDistractor(ArrayList<Shape> shapes) {
        Random rand = new Random();

        for (Shape shape : shapes) {
            float randomSizeModifier = rand.nextFloat() / 2.5f + .8f;    //random float between .8 and 1.2

            if (shape.getClass() == Circle.class) {
                ((Circle) shape).setRadius((shapeSize / 2.0) * randomSizeModifier);
            } else if (shape.getClass() == Rectangle.class) {
                ((Rectangle) shape).setWidth((shapeSize) * randomSizeModifier);
                ((Rectangle) shape).setHeight((shapeSize) * randomSizeModifier);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameMode gameMode1 = (GameMode) o;
        return new HashSet<>( Arrays.asList( distractors )).equals(new HashSet<>(Arrays.asList(gameMode1.distractors))) && gameMode == gameMode1.gameMode;
    }

    public boolean hasSameDistractors(possibleModes[] modes){
        if(modes == null || distractors == null) return modes == null && distractors == null;
        if(modes.length == 0 || distractors.length == 0) return modes.length == 0 && distractors.length == 0;
        return new HashSet<>( Arrays.asList( distractors )).equals(new HashSet<>(Arrays.asList(modes)));
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(gameMode);
        result = 31 * result + Arrays.hashCode(distractors);
        return result;
    }


}
