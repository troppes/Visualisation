package de.htw.app;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.htw.app.games.GameMode;

import java.util.ArrayList;

public class Player {

    @JsonProperty
    private String name;
    @JsonProperty
    private ArrayList<GameMode> games;

    @JsonProperty("mean_distance")
    private float meanDistance;
    @JsonProperty("mean_time")
    private float meanTime;

    @JsonIgnore
    private float distanceCounter = 0;


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setGames(ArrayList<GameMode> games) {
        this.games = games;
    }

    public void setMeanDistance(float meanDistance) {
        this.meanDistance = meanDistance;
    }

    public void setMeanTime(float meanTime) {
        this.meanTime = meanTime;
    }

    public void addDistance(float distance) {
        setMeanDistance(distance / ++distanceCounter);
    }
}
