package de.htw.app;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import de.htw.app.games.GameMode;

import java.util.ArrayList;
import java.util.Objects;

public class Player {

    private int id = -1;

    @JsonProperty
    private String name;

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

    @JsonIgnore
    public void setGames(ArrayList<GameMode> games) {
        this.games = games;
    }

    @JsonGetter("games")
    public ArrayList<GameMode> getGames() {
        return games;
    }

    public void setMeanTime(float meanTime) {
        this.meanTime = meanTime;
    }

    public void addDistance(float distance) {
        ++distanceCounter;
        meanDistance += distance;
    }

    public float getMeanTime() {
        return meanTime;
    }

    public void setMeanDistance(float meanDistance) {
        distanceCounter = 1;
        this.meanDistance = meanDistance;
    }

    public float getMeanDistance() {
        if(distanceCounter==0) return -1;
        return meanDistance/distanceCounter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player that = (Player) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
