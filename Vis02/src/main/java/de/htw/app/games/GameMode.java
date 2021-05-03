package de.htw.app.games;
import javafx.scene.layout.Pane;

public interface GameMode {

    enum possibleModes {COLOR, ORIENTATION, SIZE, GROUPING } // Statt SIZE/SHAPE = ROTATION

    // TimeSlots 2 1 .5 .3 .2 .1

    possibleModes[] distractors = null;
    possibleModes gameMode = null;
    float lowestTime = -1;

    void generateLevel();
    float getMeanDistance();
    Pane getLevel();
    float getLowestTime();
    void setLowestTime(float time);

}
