package de.htw.app;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.htw.app.lib.deserializer.DoubleDeserializer;


@JsonPropertyOrder({
        "name",
        "manufacturer",
        "mpg",
        "cylinder",
        "displacement",
        "horsepower",
        "weight",
        "acceleration",
        "year",
        "origin"
})
public class Car {
    @JsonProperty
    private String name;
    @JsonProperty
    private String manufacturer;
    @JsonProperty
    @JsonDeserialize(using = DoubleDeserializer.class)
    private Double mpg;
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

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public Double getMpg() {
        return mpg;
    }

    public Double getKml() {
        if(mpg == null) return null;
        return mpg/2.3521458;
    }

    public Integer getCylinder() {
        return cylinder;
    }

    public Double getDisplacement() {
        return displacement;
    }

    public Double getDisplacementInCCM() {
        if(displacement == null) return null;
        return displacement*16.387;
    }

    public Double getHorsepower() {
        return horsepower;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getWeightInKG() {
        if(weight == null) return null;
        return weight/2.205;
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

    @Override
    public String toString() {
        return "Car{" +
                "name='" + name + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", mpg=" + mpg +
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
