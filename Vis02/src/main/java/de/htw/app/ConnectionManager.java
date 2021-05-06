package de.htw.app;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class ConnectionManager {


    /**
     *
     * @param url The url for posting (without any ?-Params)
     * @param go The object to send
     * @return The id of the posted object
     * @throws IOException If the object cant be sent
     */
    public static int POSTRequest(String url ,Object go) throws IOException {

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
        System.out.println(POST_PARAMS);
        System.out.println(url);

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
            ReturnItem item = objectMapper.readValue(response.substring(0, response.length()-1).substring(8), ReturnItem.class);
            return item.id;
        } else{
            System.out.println("POST Response Code :  " + responseCode);
            System.out.println("POST Response Message : " + postConnection.getResponseMessage());
            throw new IOException("Posting the data did not work!");
        }
    }

    // todo maybe generify
    private static String getRequest(String url) {
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
                // Slice since cms gives us a wrapper
                return response.substring(0, response.length()-1).substring(8);
            } else {
                throw new IOException("Get Request for the GameObjects failed!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

class ReturnItem {
    @JsonProperty
    int id;
}

