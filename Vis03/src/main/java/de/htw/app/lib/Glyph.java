package de.htw.app.lib;

import de.htw.app.model.Car;
import de.htw.app.model.Logo;

public class Glyph {

    Logo logo;
    Car car;

    public Glyph(Car car){
        this.car = car;

        String carManufacturer = car.getManufacturer();
        //fetch logo by string here somehow

    }
}
