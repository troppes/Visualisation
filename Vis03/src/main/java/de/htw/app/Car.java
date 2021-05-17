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
    //Car	Manufacturer	MPG	Cylinders	Displacement	Horsepower	Weight	Acceleration	Model Year	Origin
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
