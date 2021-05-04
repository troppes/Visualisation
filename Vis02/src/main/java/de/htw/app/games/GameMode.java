package de.htw.app.games;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public abstract class GameMode {
    //Class Mode/-s
    enum possibleModes {COLOR, ORIENTATION, SIZE, GROUPING } // Statt SIZE/SHAPE = ROTATION
    possibleModes[] distractors = null;
    possibleModes gameMode = null;

    //level generation data
    int shapeSize = 20;
    int numOfDistractors = 150;

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

}
