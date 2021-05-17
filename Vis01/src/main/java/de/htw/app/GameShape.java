package de.htw.app;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class GameShape {
    private float ratio;
    private float x;
    private shape myShape;
    private float estimated_ratio;

    enum shape {CIRCLE, SQUARE}

    public GameShape(){}

    public GameShape(float ratio, float x, shape shape, float estimated_ratio) {
        this.ratio = ratio;
        this.x = x;
        this.myShape = shape;
        this.estimated_ratio = estimated_ratio;
    }

    @JsonGetter("shape")
    public shape getMyShape() {
        return myShape;
    }

    @JsonSetter("shape")
    public void setMyShape(shape myShape) {
        this.myShape = myShape;
    }

    @JsonGetter("estimated_ratio")
    public float getEstimated_ratio() {
        return estimated_ratio;
    }

    @JsonSetter("estimated_ratio")
    public void setEstimated_ratio(float estimated_ratio) {
        this.estimated_ratio = estimated_ratio;
    }

    @JsonGetter("ratio")
    public float getRatio() {
        return ratio;
    }

    @JsonSetter("ratio")
    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    @JsonGetter("x")
    public float getX() {
        return x;
    }

    @JsonSetter("x")
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return "GameShape{" +
                "ratio=" + ratio +
                ", x=" + x +
                ", myShape=" + myShape +
                ", estimated_ratio=" + estimated_ratio +
                '}';
    }
}
