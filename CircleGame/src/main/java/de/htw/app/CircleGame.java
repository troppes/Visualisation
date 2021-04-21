package de.htw.app;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class CircleGame extends Application {

    //Game UI
    Button bigPlusButton, plusButton, minusButton, bigMinusButton;
    //dynamic game elements
    Label heading;
    //game data
    float unitSize = 100;
    Shape unitShape;
    Shape generatedShape;
    float ratio;
    GameShape.shape currentShape;
    String name = "Player";
    HBox circlesBox;
    //tracking data
    ArrayList<GameShape> playedShapes = new ArrayList<>();
    int shapesCount = 0;
    float totalX = 0;
    int clicksOverOptimum = 0, clicksForPrompt = 0, optimalClicksForPrompt = 0;

    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();
        root.setPrefSize(1280, 720);

        //UI
        bigPlusButton = new Button("++");
        plusButton = new Button("+");
        minusButton = new Button("-");
        bigMinusButton = new Button("--");
        Button submitButton = new Button("Submit");

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);
        buttonBox.getChildren().add(bigPlusButton);
        buttonBox.getChildren().add(plusButton);
        buttonBox.getChildren().add(minusButton);
        buttonBox.getChildren().add(bigMinusButton);
        buttonBox.getChildren().add(submitButton);
        buttonBox.setAlignment(Pos.CENTER);
        root.setBottom(buttonBox);

        Button exitButton = new Button("Exit");
        root.setRight(exitButton);
        exitButton.setOnAction(actionEvent -> System.exit(0));

        heading = new Label("Ratio");
        heading.setFont(new Font("Arial", 30));
        HBox headingBox = new HBox();
        headingBox.getChildren().add(heading);
        headingBox.setAlignment(Pos.CENTER);
        root.setTop(headingBox);

        //circles hbox init
        circlesBox = new HBox();
        circlesBox.setSpacing(10);

        generateNewGame();

        TextInputDialog dialog = new TextInputDialog("walter");
        dialog.setTitle("Welcome to this experience!");
        dialog.setContentText("Please enter your name:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> name = s);

        //circles hbox elements
        circlesBox.setAlignment(Pos.CENTER);

        root.setCenter(circlesBox);

        //SubmitButton Listener
        submitButton.setOnAction(actionEvent -> submitEstimate());

        Scene scene = new Scene(root);

        //Keyboard Inputs
        //+ W up
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (key.getCode() == KeyCode.W) {
                modifySize(generatedShape, 10);
            }
        });
        //- S down
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (key.getCode() == KeyCode.S) {
                modifySize(generatedShape, -10);
            }
        });
        //Enter
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (key.getCode() == KeyCode.ENTER) {
                submitEstimate();
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("X-Finder");
        primaryStage.show();

    }

    @Override
    public void stop() throws Exception {
        finishGame();
        super.stop();
    }

    void generateNewGame() {
        //Generate new game
        //random Shape and Ratio
        Random random = new Random();

        int randomShape = random.nextInt(2);
        float randomRatio = .1f + random.nextFloat() * (5 - .1f);   //min + r.nextFloat() * (max - min);
        randomRatio = ((int) (randomRatio * 10)) / 10f;    //Shorten float to first decimal place

        if (randomShape == 0)
            currentShape = GameShape.shape.CIRCLE;
        else
            currentShape = GameShape.shape.SQUARE;

        ratio = randomRatio;

        heading.setText("Ratio 1:" + ratio);

        //Todo: Set Radius to already generated average x IF enough shapes have been processed
        if (currentShape == GameShape.shape.CIRCLE) {
            if (unitShape != null) {
                circlesBox.getChildren().remove(unitShape);
                circlesBox.getChildren().remove(generatedShape);
            }

            unitShape = new Circle(0, 0, unitSize);
            generatedShape = new Circle(0, 0, unitSize);

            Color red = Color.rgb(161, 26, 16);
            unitShape.setFill(red);
            generatedShape.setFill(red);
        } else {
            if (unitShape != null) {
                circlesBox.getChildren().remove(unitShape);
                circlesBox.getChildren().remove(generatedShape);
            }

            unitShape = new Rectangle(unitSize, unitSize);
            generatedShape = new Rectangle(unitSize, unitSize);

            Color blue = Color.rgb(26, 86, 161);
            unitShape.setFill(blue);
            generatedShape.setFill(blue);
        }

        circlesBox.getChildren().add(unitShape);
        circlesBox.getChildren().add(generatedShape);

        //BigPlusButton Listener
        bigPlusButton.setOnAction(actionEvent -> modifySize(generatedShape, 10));
        //PlusButton Listener
        plusButton.setOnAction(actionEvent -> modifySize(generatedShape, 2));
        //MinusButton Listener
        minusButton.setOnAction(actionEvent -> modifySize(generatedShape, -2));
        //BigMinusButton Listener
        bigMinusButton.setOnAction(actionEvent -> modifySize(generatedShape, -10));

        //ToDo: Test this cause idk if it works or not
        //reset clicks and recalculate new click optimum
        clicksForPrompt = 0;
        if (randomShape == 0) {   //circle
            float goalCircleArea = (float) (Math.PI * Math.pow(ratio * unitSize, 2));
            float correctLength = (float) Math.sqrt(goalCircleArea / Math.PI);             //this is the optimal length for radius of the circle

            optimalClicksForPrompt = (int) ((correctLength - unitSize) / 10);
        } else {                   //rect
            float correctLength = (float) Math.sqrt(ratio * 100);             //this is the optimal length for one side of the square

            optimalClicksForPrompt = (int) ((correctLength - unitSize) / 10);
        }
    }

    //returns the area of a shape
    float shapeArea(Shape shape) {
        //circle area
        if (shape.getClass() == Circle.class)
            return (float) (Math.PI * Math.pow(((Circle) shape).getRadius(), 2));

        //rect area
        return (float) (((Rectangle) shape).getHeight() * ((Rectangle) shape).getWidth());
    }

    //returns x for the formula
    float calculateX(float goalArea, float actualArea) {
        System.out.println("area: " + goalArea + " actual area: " + actualArea + " x: " + (Math.log(goalArea) / Math.log(actualArea)));

        return (float) (Math.log(goalArea) / Math.log(actualArea));
    }

    //changed the size of shapes according to the @modifier
    void modifySize(Shape shape, float modifier) {
        if (shape.getClass() == Circle.class)
            ((Circle) shape).setRadius(((Circle) shape).getRadius() + modifier);
        else {
            ((Rectangle) shape).setHeight(((Rectangle) shape).getHeight() + modifier);
            ((Rectangle) shape).setWidth(((Rectangle) shape).getWidth() + modifier);
        }

        clicksForPrompt++;
    }

    float calculateRatio() {
        float unitArea, generatedArea;

        if (currentShape == GameShape.shape.CIRCLE) {
            unitArea = unitSize * (float) (Math.PI * Math.pow(((Circle) generatedShape).getRadius(), 2));
            generatedArea = (float) (((Circle) generatedShape).getRadius() * (float) (Math.PI * Math.pow(((Circle) generatedShape).getRadius(), 2)));
        } else {
            unitArea = unitSize * unitSize;
            generatedArea = (float) (((Rectangle) generatedShape).getWidth() * ((Rectangle) generatedShape).getHeight());
        }

        return generatedArea / unitArea;
    }

    void submitEstimate() {
        //Tracking data for shapes
        float x = calculateX(shapeArea(unitShape) * ratio, shapeArea(generatedShape));
        GameShape shape = new GameShape(ratio, x, currentShape, calculateRatio());
        playedShapes.add(shape);

        //Tracking data for user
        totalX += x;
        shapesCount += 1;

        /* old way that only start counting extra clicks after the optimal click number has been surpassed
        if (clicksForPrompt >= optimalClicksForPrompt) {
            clicksOverOptimum += clicksForPrompt - optimalClicksForPrompt;
        }
        */

        //also counts the clicks that are too few as "extra clicks"
        //Basically measures the click distance to the optimal number of clicks
        clicksOverOptimum += Math.abs(clicksForPrompt - optimalClicksForPrompt);

        //draw new game
        generateNewGame();
    }

    void finishGame() {
        /*
        try {
            if (playedShapes.size() != 0) {
                float averageX = totalX / shapesCount;

                GameShape[] gameShapes = new GameShape[playedShapes.size()];
                gameShapes = playedShapes.toArray(gameShapes);

                GameObject go = new GameObject(name, averageX, clicksOverOptimum, gameShapes);
                POSTRequest(go);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    private void POSTRequest(GameObject go) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        String POST_PARAMS = "{}";
        try {
            // convert user object to json string and return it
            POST_PARAMS = objectMapper.writeValueAsString(go);
        } catch (JsonGenerationException | JsonMappingException e) {
            // catch various errors
            e.printStackTrace();
        }
        URL obj = new URL("https://cms.reitz.dev/items/shape_game/");
        HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
        postConnection.setRequestMethod("POST");
        postConnection.setRequestProperty("Content-Type", "application/json");
        postConnection.setDoOutput(true);
        OutputStream os = postConnection.getOutputStream();
        os.write(POST_PARAMS.getBytes());
        os.flush();
        os.close();
        int responseCode = postConnection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            System.out.println("POST Response Code :  " + responseCode);
            System.out.println("POST Response Message : " + postConnection.getResponseMessage());
            throw new IOException("Posting the data did not work!");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
