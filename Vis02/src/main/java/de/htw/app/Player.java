package de.htw.app;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import de.htw.app.games.GameMode;

import java.util.ArrayList;

public class Player {

    private int id = -1;

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

    @JsonSetter
    public void setId(int id) {
        this.id = id;
    }

    @JsonIgnore
    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setGames(ArrayList<GameMode> games) {
        this.games = games;
    }

    public void setMeanTime(float meanTime) {
        this.meanTime = meanTime;
    }

    public void addDistance(float distance) {
        ++distanceCounter;
        meanDistance += distance;
    }

    public float getMeanTime() {
        if(distanceCounter==0) return -1;
        return meanTime/distanceCounter;
    }

    public float getMeanDistance() {
        return meanDistance;
    }
}
