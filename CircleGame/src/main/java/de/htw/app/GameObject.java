package de.htw.app;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class GameObject {
    private String user;
    private float average_x;
    @JsonIgnore
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

    public GameShape[] getShapes_played() {
        return shapes_played;
    }

    public void setShapes_played(GameShape[] shapes_played) {
        this.shapes_played = shapes_played;
    }
}
