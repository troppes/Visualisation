package de.htw.app.lib.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class DoubleDeserializer extends JsonDeserializer<Double> {

    @Override
    public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String doubleStr = p.getText();

        if (doubleStr.isEmpty() || doubleStr.equals("NA")) {
            return null;
        }

        return Double.parseDouble(doubleStr);
    }
}