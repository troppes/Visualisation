package de.htw.app.lib;

import de.htw.app.model.Car;
import de.htw.app.model.Logo;
import java.util.*;

public class Glyph {

    double posX;
    double posY;

    boolean visible = true;

    Logo logo;
    Car car;

    public Glyph(Car car, List<Logo> logos){
        this.car = car;

        String carManufacturer = car.getManufacturer();
        //fetch logo by string here somehow
        logo = logos.stream()
                .filter(l -> l.getSlug().equals(car.getManufacturer()))
                .findFirst()
                .orElse(null);

        if(logo==null) System.out.println("Manufacturer Logo missing: " + car.getManufacturer());
    }

    public void setPosition(double x, double y){
        posX = x;
        posY = y;
    }

    public void setVisible(boolean visible){
        this.visible = visible;
    }

    public boolean getVisible(){
        return visible;
    }

    public Car getCar(){
        return car;
    }

    public Logo getLogo(){
        return logo;
    }

    public double getPosX(){
        return posX;
    }

    public double getPosY(){
        return posY;
    }
}
