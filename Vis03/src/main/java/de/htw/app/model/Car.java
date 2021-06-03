package de.htw.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.htw.app.lib.deserializer.DoubleDeserializer;


@JsonPropertyOrder({
        "name",
        "manufacturer",
        "consumption",
        "cylinder",
        "displacement",
        "horsepower",
        "weight",
        "acceleration",
        "year",
        "origin"
})


public class Car {


    public enum ComparableValues {
        Consumption,
        Cylinder,
        Displacement,
        Horsepower,
        Weight,
        Acceleration
    }

    @JsonProperty
    private String name;
    @JsonProperty
    private String manufacturer;
    @JsonProperty
    @JsonDeserialize(using = DoubleDeserializer.class)
    private Double consumption;
    @JsonProperty
    private Integer cylinder;
    @JsonProperty
    @JsonDeserialize(using = DoubleDeserializer.class)
    private Double displacement;
    @JsonProperty
    @JsonDeserialize(using = DoubleDeserializer.class)
    private Double horsepower;
    @JsonProperty
    @JsonDeserialize(using = DoubleDeserializer.class)
    private Double weight;
    @JsonProperty
    @JsonDeserialize(using = DoubleDeserializer.class)
    private Double acceleration;
    @JsonProperty
    private Integer year;
    @JsonProperty
    private String origin;

    public Car(){}

    public Car(Double consumption, Integer cylinder, Double displacement, Double horsepower, Double weight, Double acceleration) {
        this.consumption = consumption;
        this.cylinder = cylinder;
        this.displacement = displacement;
        this.horsepower = horsepower;
        this.weight = weight;
        this.acceleration = acceleration;
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public Double getConsumption(boolean kml) {
        if(consumption == null) return null;
        if(kml) return consumption/2.3521458;
        return consumption;
    }

    public Integer getCylinder() {
        return cylinder;
    }

    public Double getDisplacement(boolean inCCM) {
        if(displacement == null) return null;
        if(inCCM) return displacement*16.387;
        return displacement;
    }

    public Double getHorsepower() {
        return horsepower;
    }

    public Double getWeight(boolean kg) {
        if(weight == null) return null;
        if(kg) return weight/2.205;
        return weight;
    }

    public Double getAcceleration() {
        return acceleration;
    }

    public Integer getYear() {
        return year;
    }

    public String getOrigin() {
        return origin;
    }

    public static ComparableValues asEnum(String str) {
        for (ComparableValues me : ComparableValues.values()) {
            if (me.name().equalsIgnoreCase(str))
                return me;
        }
        return ComparableValues.Horsepower;
    }

    @Override
    public String toString() {
        return "Car{" +
                "name='" + name + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", consumption=" + consumption +
                ", cylinder=" + cylinder +
                ", displacement=" + displacement +
                ", horsepower=" + horsepower +
                ", weight=" + weight +
                ", acceleration=" + acceleration +
                ", year=" + year +
                ", origin='" + origin + '\'' +
                '}';
    }
}
