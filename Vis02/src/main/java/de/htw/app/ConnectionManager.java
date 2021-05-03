package de.htw.app;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConnectionManager {


    //todo add return type
    private void POSTRequest(String url ,Object go) throws IOException {

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
            //currentPlayer = objectMapper.readValue(response.substring(0, response.length()-1).substring(8), GameObject.class);
        }
        if (responseCode != HttpURLConnection.HTTP_OK) {
            System.out.println("POST Response Code :  " + responseCode);
            System.out.println("POST Response Message : " + postConnection.getResponseMessage());
            throw new IOException("Posting the data did not work!");
        }
    }

    // todo maybe generify
    private String getRequest(String url) {
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
