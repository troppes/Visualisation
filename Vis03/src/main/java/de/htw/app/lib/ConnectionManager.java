package de.htw.app.lib;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.htw.app.model.Logo;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ConnectionManager {

    /**
     * @param url The url for posting (without any ?-Params)
     * @param go  The object to send
     * @return The id of the posted object
     * @throws IOException If the object cant be sent
     */
    public static int POSTRequest(String url, Object go) throws IOException {

        url += "?fields=id";
        ObjectMapper objectMapper = new ObjectMapper();
        String POST_PARAMS = "{}";
        try {
            // convert user object to json string and return it
            POST_PARAMS = objectMapper.writeValueAsString(go);
        } catch (JsonGenerationException | JsonMappingException e) {
            // catch various errors
            e.printStackTrace();
        }

        URL obj = new URL(url);
        HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
        postConnection.setRequestMethod("POST");
        postConnection.setRequestProperty("Content-Type", "application/json");
        postConnection.setDoOutput(true);
        OutputStream os = postConnection.getOutputStream();
        os.write(POST_PARAMS.getBytes());
        os.flush();
        os.close();
        int responseCode = postConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            // Slice because CMS sets data prefix
            ReturnItem item = objectMapper.readValue(response.substring(0, response.length() - 1).substring(8), ReturnItem.class);
            return item.id;
        } else {
            System.out.println("POST Response Code :  " + responseCode);
            System.out.println("POST Response Message : " + postConnection.getResponseMessage());
            throw new IOException("Posting the data did not work!");
        }
    }

    /**
     *
     * @param url Url for the GET request
     * @param valueType Class of the object for deserialization
     * @param <T> Return Class
     * @param emptyStringAsNull If set to true, empty strings will be parsed as null
     * @return List of elements from given class
     */
    public static <T> List<T> GETRequest(String url, Class<T> valueType, boolean emptyStringAsNull) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;

                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                ObjectMapper mapper = new ObjectMapper();
                if(emptyStringAsNull){
                    mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
                    mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
                }


                return mapper.readValue(response.substring(0, response.length() - 1).substring(8),
                        mapper.getTypeFactory().constructCollectionType(List.class, valueType));
            } else {
                throw new IOException("Get Request for the GameObjects failed!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param url Url for the GET request
     * @param valueType Class of the object for deserialization
     * @param valueSeparator The separator between the values
     * @param <T> Return Class
     * @return List of elements from given class
     */
    public static <T> List<T> loadCSV(String url, Class<T> valueType, char valueSeparator) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;

                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine).append('\n');
                }
                in.close();

                CsvMapper csvMapper = new CsvMapper();
                CsvSchema csvSchema = csvMapper.schemaFor(valueType)
                        .withHeader()
                        .withColumnSeparator(valueSeparator)
                        .withLineSeparator("\n");

                MappingIterator<T> results = csvMapper.readerFor(valueType).with(csvSchema).readValues(String.valueOf(response));

                return results.readAll();

            } else {
                throw new IOException("Get Request for the GameObjects failed!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param path Path to the JSON
     * @param valueType Class of the object for deserialization
     * @param <T> Return Class
     * @return List of elements from given class
     */
    public static <T> List<T> loadJSON(String path, Class<T> valueType) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            InputStream is = ConnectionManager.class.getClassLoader().getResourceAsStream(path);
            JsonNode json = mapper.readTree(is);

            List<T> list = new ArrayList<>();
            for (JsonNode node : json) {
                list.add(mapper.treeToValue(node, valueType));
            }

            return list;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

class ReturnItem {
    @JsonProperty
    int id;
}

