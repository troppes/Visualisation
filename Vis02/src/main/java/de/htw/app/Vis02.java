package de.htw.app;

import de.htw.app.games.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

public class Vis02 extends Application {

    private final Player player = new Player();

    //Screen item references
    Stage primaryStage;
    BorderPane root;
    Button skipPromptButton, startPromptButton;

    //game controller
    int totalGameModes = 8; //set total game modes to number of different game loops we want to play Each loop has to be prepared separately

    int[] timings = new int[]{1000, 500, 300, 200, 100};   //in ms
    int gameModeCounter = 0, timingCounter = 0;
    float minDistanceToPass = 40;

    //game mode
    int levelDimensionX = 500, levelDimensionY = 350;

    //tracking
    ArrayList<GameMode> playedGameModes = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {

        root = new BorderPane();
        root.setPrefSize(1280, 720);

        //NAME SELECTOR
        TextInputDialog dialog = new TextInputDialog("walter");
        dialog.setTitle("Enter Name");
        dialog.setHeaderText("Welcome");
        dialog.setContentText("Please enter your name:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(player::setName);
        if (result.isEmpty()) {
            System.exit(0);
        }

        //INSTRUCTIONS SCREEN
        Text welcomeMessage = new Text(String.join("\n"
                , "Welcome " + player.getName() + ", \n"
                , "thank you for playing our game! \n"
                , "The goal of the game is to find the target. \n"
                , "The target is an outlier, that has e.g. another color. The specific difference is shown in the label on the top of the window.\n"
                , "We show you the playing field for different amounts of time to test your pre-attentive perception, \n"
                , "and afterwards you need to click where you think you saw the target. If you did not see it, you can always click the button \n"
                , "\"I did not see the target\"."
        ));
        welcomeMessage.setStyle("-fx-font: 14 monospaced;");

        Alert instructions = new Alert(Alert.AlertType.INFORMATION);
        instructions.getDialogPane().setContent(welcomeMessage);
        instructions.setTitle("Instructions");
        instructions.setHeaderText("Instructions");
        instructions.showAndWait();


        //GAME SCREEN
        loadGameScreen();

        Scene scene = new Scene(root);

        this.primaryStage = primaryStage;


        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (key.getCode() == KeyCode.W) {
                finishGame();
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Find Waldo: Reaction Edition");
        primaryStage.show();
    }

    void loadGameScreen() {
        root.getChildren().clear();

        GameMode gameMode = new Color(levelDimensionX, levelDimensionY);

        Label heading;
        Label subHeading = new Label("View-time: " + timings[timingCounter] + "ms");
        subHeading.setFont(new Font("Arial", 20));

        VBox headingBox = new VBox();
        headingBox.setAlignment(Pos.CENTER);
        root.setTop(headingBox);

        startPromptButton = new Button("Start next prompt");
        skipPromptButton = new Button("Did not see target");
        HBox bottomBox = new HBox();
        bottomBox.setSpacing(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.getChildren().addAll(startPromptButton, skipPromptButton);
        root.setBottom(bottomBox);

        switch (gameModeCounter) {
            case 0: //Color
                heading = new Label("Find the differently COLORED target");
                heading.setFont(new Font("Arial", 30));
                headingBox.getChildren().addAll(heading, subHeading);

                gameMode = new Color(levelDimensionX, levelDimensionY);
                break;
            case 1: //Orientation
                heading = new Label("Find the differently ORIENTED target");
                heading.setFont(new Font("Arial", 30));
                headingBox.getChildren().addAll(heading, subHeading);

                gameMode = new Orientation(levelDimensionX, levelDimensionY);
                break;
            case 2: //Size
                heading = new Label("Find the differently SIZED target");
                heading.setFont(new Font("Arial", 30));
                headingBox.getChildren().addAll(heading, subHeading);

                gameMode = new Size(levelDimensionX, levelDimensionY);
                break;
            case 3: //Grouping
                heading = new Label("Find the differently GROUPED target");
                heading.setFont(new Font("Arial", 30));
                headingBox.getChildren().addAll(heading, subHeading);

                gameMode = new Grouping(levelDimensionX, levelDimensionY);
                break;
            case 4: //Color + Orientation, Size
                heading = new Label("Find the differently COLORED target");
                heading.setFont(new Font("Arial", 30));
                headingBox.getChildren().addAll(heading, subHeading);

                gameMode = new Color(levelDimensionX, levelDimensionY);
                gameMode.setDistractors(new GameMode.possibleModes[]{GameMode.possibleModes.ORIENTATION, GameMode.possibleModes.SIZE});
                break;
            case 5: //Orientation + Size, Color
                heading = new Label("Find the differently ORIENTED target");
                heading.setFont(new Font("Arial", 30));
                headingBox.getChildren().addAll(heading, subHeading);

                gameMode = new Orientation(levelDimensionX, levelDimensionY);
                gameMode.setDistractors(new GameMode.possibleModes[]{GameMode.possibleModes.SIZE, GameMode.possibleModes.COLOR});
                break;
            case 6: //Size + Color, Orientation
                heading = new Label("Find the differently SIZED target");
                heading.setFont(new Font("Arial", 30));
                headingBox.getChildren().addAll(heading, subHeading);

                gameMode = new Size(levelDimensionX, levelDimensionY);
                gameMode.setDistractors(new GameMode.possibleModes[]{GameMode.possibleModes.COLOR, GameMode.possibleModes.ORIENTATION});
                break;
            case 7: //Grouping + Color, Orientation, Size
                heading = new Label("Find the differently GROUPED target");
                heading.setFont(new Font("Arial", 30));
                headingBox.getChildren().addAll(heading, subHeading);

                gameMode = new Grouping(levelDimensionX, levelDimensionY);
                gameMode.setDistractors(new GameMode.possibleModes[]{GameMode.possibleModes.COLOR, GameMode.possibleModes.ORIENTATION, GameMode.possibleModes.SIZE});
                break;
        }

        skipPromptButton.setDisable(true);
        startPromptButton.setDisable(false);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(timings[timingCounter]), event -> root.setCenter(null)));
        timeline.setCycleCount(1);

        gameMode.generateLevel();
        Pane activeLevel = gameMode.getLevel();

        playedGameModes.add(gameMode);  //adding the current game mode as the last played game mode

        startPromptButton.setOnAction(event -> {
            startPromptButton.setDisable(true);
            root.setCenter(activeLevel);
            timeline.play();
            timeline.setOnFinished(timelineEvent -> loadTargetFindingScreen());
        });

    }

    void loadTargetFindingScreen() {
        skipPromptButton.setDisable(false);

        skipPromptButton.setOnAction(event -> levelSuccessChecker(-1, -1, -1, -1));

        Pane guessLevel = new Pane();

        guessLevel.setMaxSize(levelDimensionX, levelDimensionY);
        Rectangle levelBorder = new Rectangle(levelDimensionX, levelDimensionY);
        levelBorder.setFill(javafx.scene.paint.Color.TRANSPARENT);
        levelBorder.setStroke(javafx.scene.paint.Color.BLACK);

        guessLevel.getChildren().addAll(levelBorder);

        root.setCenter(guessLevel);

        guessLevel.setOnMouseClicked(event -> levelSuccessChecker(playedGameModes.get(playedGameModes.size() - 1).getTargetX(), playedGameModes.get(playedGameModes.size() - 1).getTargetY(), (int) event.getX(), (int) event.getY()));
    }


    void loadResultScreen(boolean win) {
        skipPromptButton.setDisable(true);

        StackPane stack = new StackPane();

        stack.setMaxSize(levelDimensionX, levelDimensionY);
        Rectangle levelBorder = new Rectangle(levelDimensionX, levelDimensionY);
        Text text = new Text();
        if (win) {
            text.setText("Object found! Very well! \nClick on this area, \nto start the next round.");
            levelBorder.setFill(javafx.scene.paint.Color.GREEN);
            text.setStyle("-fx-font-size: 24");
        } else {
            text.setText("Missed! \nClick on this area, \nto start the next round.");
            levelBorder.setFill(javafx.scene.paint.Color.RED);
            text.setStyle("-fx-fill: white; -fx-font-size: 24");
        }
        levelBorder.setStroke(javafx.scene.paint.Color.BLACK);
        stack.getChildren().addAll(levelBorder, text);
        stack.setOnMouseClicked(event -> loadGameScreen());
        root.setCenter(stack);
    }

    void levelSuccessChecker(int targetX, int targetY, int guessX, int guessY) {
        double distance = Math.sqrt(Math.pow(targetX - guessX, 2) + Math.pow(targetY - guessY, 2));
        boolean win = false;

        // If Player played -> Add Distance to Mean
        if (targetX != -1) {
            player.addDistance((float) distance);
            playedGameModes.get(playedGameModes.size() - 1).addDistance((float) distance);
        }

        if (targetX == -1 || distance > minDistanceToPass) {    //user pressed the skip button OR didn't guess correctly
            gameModeCounter++;
            if (timingCounter > 0)   //user has successfully passed at least one test
                playedGameModes.get(playedGameModes.size() - 1).setLowestTime(timings[timingCounter - 1]);    //write the last successful lowest time into the game mode
            else
                playedGameModes.get(playedGameModes.size() - 1).setLowestTime(-1);
            timingCounter = 0; //reset timingCounter
        } else {
            win = true;
            if (timingCounter < timings.length - 1) { //min time hasn't been reached yet
                timingCounter++;
                playedGameModes.remove(playedGameModes.size() - 1); //remove the last game played, as it was successfully completed
            } else {   //reset game if min time has been successfully reached
                gameModeCounter++;
                playedGameModes.get(playedGameModes.size() - 1).setLowestTime(timings[timingCounter]);    //write the current lowest time into the game mode
                timingCounter = 0;  //reset timingCounter
            }
        }

        //if last game mode has been played finish game, else reload next round
        if (gameModeCounter == totalGameModes) finishGame();
        else loadResultScreen(win);
    }

    void finishGame() {
        root.getChildren().clear();

        player.setGames(playedGameModes);

        Label finish = new Label("Results");
        finish.setFont(new Font("Arial", 80));
        StringBuilder recordString = new StringBuilder();

        float meanTime = 0F;
        for (GameMode gameMode : playedGameModes) {
            if (gameMode.getLowestTime() == -1) { // Add a 3 second penalty, if they fail on the 1 second barrier.
                meanTime += 2000;
            } else {
                meanTime += gameMode.getLowestTime();
            }
            String numberOfDistractors = gameMode.getDistractors().length == 0 ? "none" : Arrays.toString(gameMode.getDistractors());
            recordString.append(gameMode.getGameMode()).append(" with ").append(numberOfDistractors)
                    .append(" distractions. Your lowest time: ").append(gameMode.getLowestTime()).append("\n");
        }
        player.setMeanTime(meanTime);

        try {
            player.setId(ConnectionManager.POSTRequest("https://cms.reitz.dev/items/vis02_player/", player));
        } catch (IOException e) {
            System.out.println("Failed to send data!");
        }


        Label finishRecord = new Label("Times:\n" + recordString);
        Label meanTimeLabel = new Label("Mean Time: " + player.getMeanTime()+"ms");
        Label meanDistanceLabel = new Label("Mean Distance: " + player.getMeanDistance()+"px");
        finishRecord.setFont(new Font("Arial", 20));
        meanTimeLabel.setFont(new Font("Arial", 20));
        meanDistanceLabel.setFont(new Font("Arial", 20));

        VBox headingBox = new VBox();
        headingBox.getChildren().addAll(finish, finishRecord, meanTimeLabel, meanDistanceLabel);
        headingBox.setAlignment(Pos.CENTER);
        root.setTop(headingBox);
    }

    public static void main(String[] args) {
        launch(args);
    }

}

