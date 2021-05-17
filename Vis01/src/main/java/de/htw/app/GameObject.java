package de.htw.app;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class GameObject {
    private int id = -1;
    private String user;
    private float average_x;
    private GameShape[] shapes_played;
    private int clicks;

    public GameObject(){}

    public GameObject(String user, float average_x, int clicks){
        this.user = user;
        this.average_x = average_x;
        this.clicks = clicks;
        this.shapes_played = null;
    }

    public GameObject(String user, float average_x, int clicks, GameShape[] shapes_played) {
        this.user = user;
        this.average_x = average_x;
        this.clicks = clicks;
        this.shapes_played = shapes_played;
    }


    @JsonProperty
    public void setId(int id) {
        this.id = id;
    }

    @JsonIgnore
    public int getId() {
        return id;
    }

    public int getClicks() {
        return clicks;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public float getAverage_x() {
        return average_x;
    }

    public void setAverage_x(float average_x) {
        this.average_x = average_x;
    }

    @JsonProperty
    public GameShape[] getShapes_played() {
        return shapes_played;
    }

    @JsonIgnore
    public void setShapes_played(GameShape[] shapes_played) {
        this.shapes_played = shapes_played;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameObject that = (GameObject) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
