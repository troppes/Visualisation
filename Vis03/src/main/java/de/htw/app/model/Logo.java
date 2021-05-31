package de.htw.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.image.Image;

import java.util.Map;

public class Logo {

    @JsonProperty("name")
    private String name;
    @JsonProperty("slug")
    private String slug;

    @JsonIgnore
    private Image thumbnail;
    @JsonIgnore
    private Image image;

    @JsonProperty("image")
    private void unpackNameFromNestedObject(Map<String, String> brand) {
        setThumbnail(brand.get("localThumb"));
        setImage(brand.get("localOptimized"));
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = new Image(thumbnail);
    }

    public void setImage(String image) {
        this.image = new Image(image);
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public Image getImage() {
        return image;
    }

    public Image getThumbnail() {
        return thumbnail;
    }
}
