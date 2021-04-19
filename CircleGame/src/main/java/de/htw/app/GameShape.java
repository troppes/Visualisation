package de.htw.app;

public class GameShape {
    private float ratio;
    private float x;
    private shape myShape;
    private float estimated_ratio;

    enum shape {CIRCLE, SQUARE}

    public GameShape(float ratio, float x, shape shape, float estimated_ratio) {
        this.ratio = ratio;
        this.x = x;
        this.myShape = shape;
        this.estimated_ratio = estimated_ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public shape getMyShape() {
        return myShape;
    }

    public void setMyShape(shape myShape) {
        this.myShape = myShape;
    }

    public float getEstimated_ratio() {
        return estimated_ratio;
    }

    public void setEstimated_ratio(float estimated_ratio) {
        this.estimated_ratio = estimated_ratio;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public shape getShape() {
        return myShape;
    }

    public void setShape(shape myShape) {
        this.myShape = myShape;
    }
}
