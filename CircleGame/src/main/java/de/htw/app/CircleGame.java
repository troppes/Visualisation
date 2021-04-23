package de.htw.app;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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
    Button finishButton;
    Stage primaryStage;
    //tracking data
    ArrayList<GameShape> playedShapes = new ArrayList<>();
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
        finishButton = new Button("Finish");
        finishButton.setDisable(true);
        finishButton.setOnAction(e -> finishGame());

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);
        buttonBox.getChildren().add(bigPlusButton);
        buttonBox.getChildren().add(plusButton);
        buttonBox.getChildren().add(minusButton);
        buttonBox.getChildren().add(bigMinusButton);
        buttonBox.getChildren().add(submitButton);
        buttonBox.getChildren().add(finishButton);
        buttonBox.setAlignment(Pos.CENTER);
        root.setBottom(buttonBox);

        heading = new Label("Ratio");
        heading.setFont(new Font("Arial", 30));
        HBox headingBox = new HBox();
        headingBox.getChildren().add(heading);
        headingBox.setAlignment(Pos.CENTER);
        root.setTop(headingBox);

        //circles Height-Box init
        circlesBox = new HBox();
        circlesBox.setSpacing(10);

        generateNewGame();

        TextInputDialog dialog = new TextInputDialog("walter");
        dialog.setTitle("Enter Name");
        dialog.setHeaderText("Welcome");
        dialog.setContentText("Please enter your name:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> name = s);
        if(result.isEmpty()){
            System.exit(0);
        }

        Text welcomeMessage = new Text(String.join("\n"
                , "Welcome " + name + ","
                , "thank you for playing our Game! \n"
                , "Controls:"
                , "w or Button + = Increase shape size"
                , "s or Button - = Decrease shape size"
                , "Button ++ = Increase shape size * 5"
                , "Button -- = Decrease shape size * 5 \n"
                , "You can play as many shapes as you want, but after you have played three shapes, you can press the FINISH button to see your results."
        ));
        welcomeMessage.setStyle("-fx-font: 14 monospaced;");

        Alert instructions = new Alert(Alert.AlertType.INFORMATION);
        instructions.getDialogPane().setContent(welcomeMessage);
        instructions.setTitle("Instructions");
        instructions.setHeaderText("Instructions");
        instructions.showAndWait();

        //circles Height-Box elements
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
        this.primaryStage = primaryStage;

        primaryStage.setScene(scene);
        primaryStage.setTitle("X-Factor: RELOADED");
        primaryStage.show();

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

        //reset clicks and recalculate new click optimum
        clicksForPrompt = 0;
        float unitArea, goalArea, correctLength;

        if (randomShape == 0) {   //circle
            unitArea = (float) (Math.PI * Math.pow(((Circle) unitShape).getRadius(), 2));
            goalArea = unitArea * ratio;

            correctLength = (float) Math.sqrt(goalArea / Math.PI);             //this is the optimal length for radius of the circle

        } else {                   //rect
            unitArea = unitSize * unitSize;
            goalArea = unitArea * ratio;

            correctLength = (float) Math.sqrt(goalArea);             //this is the optimal length for one side of the square
        }

        optimalClicksForPrompt = Math.abs(((int)((correctLength-unitSize)/ 10)*5));                 //number of clicks on the ++ and -- buttons
        optimalClicksForPrompt += Math.abs((int)(((correctLength-unitSize)%10)/2));                 //number of clicks on the ++ and -- buttons
    }

    //returns x for the formula
    float calculateX(float goalRatio, float actualRatio) {
        return (float) (Math.log(goalRatio) / Math.log(actualRatio));
    }

    //changed the size of shapes according to the @modifier
    void modifySize(Shape shape, float modifier) {
        if (shape.getClass() == Circle.class)
            ((Circle) shape).setRadius(((Circle) shape).getRadius() + modifier);
        else {
            ((Rectangle) shape).setHeight(((Rectangle) shape).getHeight() + modifier);
            ((Rectangle) shape).setWidth(((Rectangle) shape).getWidth() + modifier);
        }

        clicksForPrompt+=Math.abs(modifier/2);
    }

    float calculateRatio() {
        float unitArea, generatedArea;

        if (currentShape == GameShape.shape.CIRCLE) {
            unitArea = (float) (Math.PI * Math.pow(((Circle) unitShape).getRadius(), 2));
            generatedArea = (float) (Math.PI * Math.pow(((Circle) generatedShape).getRadius(), 2));
        } else {
            unitArea = unitSize * unitSize;
            generatedArea = (float) (((Rectangle) generatedShape).getWidth() * ((Rectangle) generatedShape).getHeight());
        }

        return generatedArea / unitArea;
    }

    void submitEstimate() {
        //Tracking data for shapes
        //float x = calculateX(shapeArea(unitShape) * ratio, shapeArea(generatedShape));
        float estimatedRatio = calculateRatio();

        float x = calculateX(ratio, estimatedRatio);

        GameShape shape = new GameShape(ratio, x, currentShape, estimatedRatio);
        playedShapes.add(shape);

        //Add Finish Button
        if (playedShapes.size() == 3) {
            finishButton.setDisable(false);
        }

        //Tracking data for user
        totalX += x;

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
        try {
            if (playedShapes.size() != 0) {
                float averageX = totalX / playedShapes.size();

                GameShape[] gameShapes = new GameShape[playedShapes.size()];
                gameShapes = playedShapes.toArray(gameShapes);

                GameObject go = new GameObject(name, averageX, clicksOverOptimum, gameShapes);
                POSTRequest(go);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        generateResultScreen();
    }

    void generateResultScreen() {
        BorderPane results = new BorderPane();
        results.setPrefSize(1280, 720);

        VBox centerContent = new VBox();
        centerContent.setAlignment(Pos.CENTER);

        Text welcomeMessage = new Text(String.join("\n"
                , "Hello " + name + ","
                , "here are your Results: \n"
                , "Your total X was " + Math.round((totalX / playedShapes.size()) * 100F) / 100F + "."
                , "X is calculated by: (perceived size ratio) = (actual ratio of area contents)^x."
                , "That means the closer your are to 1 the better you are. \n"
                , "Your clicks were " + clicksOverOptimum + " away from the optimum."
                , "The optimum is calculated by measuring the least amount of clicks needed and comparing your clicks to it."
        ));
        welcomeMessage.setStyle("-fx-font: 16 arial;");

        centerContent.getChildren().add(welcomeMessage);
        results.setCenter(centerContent);

        HBox exitButtonBox = new HBox();
        Button exitButton = new Button("Close");
        exitButton.setOnAction(e -> System.exit(0));
        exitButton.setPadding(new Insets(10,10,10,10));
        exitButton.setStyle("-fx-font: 16 arial;");
        exitButtonBox.getChildren().add(exitButton);
        exitButtonBox.setAlignment(Pos.CENTER);
        exitButtonBox.setPadding(new Insets(20,20,20,20));
        results.setBottom(exitButtonBox);

        Scene scene = new Scene(results);
        primaryStage.setScene(scene);
        primaryStage.show();

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
