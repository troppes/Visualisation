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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class Vis02 extends Application {

    //ToDo:
    //Discuss Idea: Add example prompt of how the target can look on the right hand side of the screen
    //Maybe add more instructions?

    private Player player = new Player();

    //Screen item references
    Stage primaryStage;
    BorderPane root;
    Button skipPromptButton, startPromptButton;

    //game controller
    int totalGameModes = 8; //set total game modes to number of different gameloops we want to play Each loop has to be prepared seperately

    int[] timings = new int[] {1000, 500, 300, 200, 100};   //in ms
    int gameModeCounter = 0, timingCounter = 0;
    float minDistanceToPass = 30;

    //game mode
    int levelDimensionX=500, levelDimensionY=350;

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
        result.ifPresent(s -> player.name = s);
        if (result.isEmpty()) {
            System.exit(0);
        }

        //INSTRUCTIONS SCREEN
        Text welcomeMessage = new Text(String.join("\n"
                , "Welcome " + player.name + ","
                , "thank you for playing our Game! \n"
                , "Controls:"
                , "w or Button + = Increase shape size"
                , "s or Button - = Decrease shape size"
                , "e or Button ++ = Increase shape size * 5"
                , "d or Button -- = Decrease shape size * 5 \n"
                , "You can play as many shapes as you want, but after you have played three shapes, you can press the FINISH button to see your results."
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

        primaryStage.setScene(scene);
        primaryStage.setTitle("Vis02");
        primaryStage.show();
    }

    void loadGameScreen(){
        root.getChildren().clear();

        GameMode gameMode = new Color(levelDimensionX, levelDimensionY);

        Label heading;
        Label subHeading = new Label("Viewtime: " + timings[timingCounter] + "ms");
        subHeading.setFont(new Font("Arial", 20));

        VBox headingBox = new VBox();
        headingBox.setAlignment(Pos.CENTER);
        root.setTop(headingBox);

        startPromptButton = new Button("Start next prompt");
        skipPromptButton = new Button("Didn't see target");
        HBox bottomBox = new HBox();
        bottomBox.setSpacing(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.getChildren().addAll(startPromptButton, skipPromptButton);
        root.setBottom(bottomBox);

        //ToDo: add other game modes and more modes for the mixed ones
        switch(gameModeCounter){
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
                gameMode.setDisctractors(new GameMode.possibleModes[]{GameMode.possibleModes.ORIENTATION, GameMode.possibleModes.SIZE});
                break;
            case 5: //Orientation + Size, Color
                heading = new Label("Find the differently ORIENTED target");
                heading.setFont(new Font("Arial", 30));
                headingBox.getChildren().addAll(heading, subHeading);

                gameMode = new Orientation(levelDimensionX, levelDimensionY);
                gameMode.setDisctractors(new GameMode.possibleModes[]{GameMode.possibleModes.SIZE, GameMode.possibleModes.COLOR});
                break;
            case 6: //Size + Color, Orientation
                heading = new Label("Find the differently SIZED target");
                heading.setFont(new Font("Arial", 30));
                headingBox.getChildren().addAll(heading, subHeading);

                gameMode = new Size(levelDimensionX, levelDimensionY);
                gameMode.setDisctractors(new GameMode.possibleModes[]{GameMode.possibleModes.COLOR, GameMode.possibleModes.ORIENTATION});
                break;
            case 7: //Grouping + Color, Orientation, Size
                heading = new Label("Find the differently GROUPED target");
                heading.setFont(new Font("Arial", 30));
                headingBox.getChildren().addAll(heading, subHeading);

                gameMode = new Grouping(levelDimensionX, levelDimensionY);
                gameMode.setDisctractors(new GameMode.possibleModes[]{GameMode.possibleModes.COLOR, GameMode.possibleModes.ORIENTATION, GameMode.possibleModes.SIZE});
                break;
        }

        skipPromptButton.setDisable(true);
        startPromptButton.setDisable(false);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(timings[timingCounter]), event -> root.setCenter(null)));
        timeline.setCycleCount(1);

        gameMode.generateLevel();
        Pane activeLevel = gameMode.getLevel();

        playedGameModes.add(gameMode);  //adding the current gamemode as the last played gamemode

        startPromptButton.setOnAction(event -> {
            startPromptButton.setDisable(true);
            root.setCenter(activeLevel);
            timeline.play();
            timeline.setOnFinished(timelineEvent -> loadTargetFindingScreen());
        });

    }

    void loadTargetFindingScreen(){
        skipPromptButton.setDisable(false);

        skipPromptButton.setOnAction(event -> {
            levelSuccesfullChecker(-1, -1, -1, -1);
        });

        Pane guessLevel = new Pane();

        guessLevel.setMaxSize(levelDimensionX, levelDimensionY);
        Rectangle levelBorder = new Rectangle(levelDimensionX, levelDimensionY);
        levelBorder.setFill(javafx.scene.paint.Color.TRANSPARENT);
        levelBorder.setStroke(javafx.scene.paint.Color.BLACK);

        guessLevel.getChildren().addAll(levelBorder);

        root.setCenter(guessLevel);

        guessLevel.setOnMouseClicked(event -> {
            levelSuccesfullChecker(playedGameModes.get(playedGameModes.size()-1).getTargetX(), playedGameModes.get(playedGameModes.size()-1).getTargetY(), (int)event.getX(), (int)event.getY());
        });
    }

    void levelSuccesfullChecker(int targetX, int targetY, int guessX, int guessY){
        if(targetX==-1 || Math.sqrt(Math.pow(targetX-guessX, 2) + Math.pow(targetY-guessY, 2)) > minDistanceToPass){    //user pressed the skip button OR didn't guess correctly
            gameModeCounter++;

            if(timingCounter > 0)   //user has succesfully passed at least one test
                playedGameModes.get(playedGameModes.size()-1).setLowestTime(timings[timingCounter-1]);    //write the last succesfull lowest time into the gamemode
            else
                playedGameModes.get(playedGameModes.size()-1).setLowestTime(Integer.MAX_VALUE); //ToDo: Question for Database (Flo): What's a "high" value we can put in it that doesn't break anything, for when the user absolutely fucking sucks

            //reset timingCounter
            timingCounter = 0;
        }
        else{
            if(timingCounter < timings.length-1) { //min time hasn't been reached yet
                timingCounter++;

                playedGameModes.remove(playedGameModes.size()-1);
                //remove the last game played, as it was succesfully completed
            }
            else{   //reset game if min time has been succesfully reached
                gameModeCounter++;
                playedGameModes.get(playedGameModes.size()-1).setLowestTime(timings[timingCounter]);    //write the current lowest time into the gamemode

                //reset timingCounter
                timingCounter = 0;
            }
        }

        //if last gamemode has been played finish game, else reload next round
        if(gameModeCounter == totalGameModes) finishGame();
        else loadGameScreen();
    }

    void finishGame(){
        root.getChildren().clear();

        //ToDo
        ///placeholder\\\
        Label finish = new Label("Finito Pipito");
        finish.setFont(new Font("Arial", 80));
        String recordString = "";
        for (GameMode gameMode : playedGameModes){
            recordString += gameMode.getClass().getName() + ": " + gameMode.getLowestTime() + "\n";
        }
        Label finishRecord = new Label("LowestTimes:\n" + recordString);
        finishRecord.setFont(new Font("Arial", 20));
        VBox headingBox = new VBox();
        headingBox.getChildren().addAll(finish, finishRecord);
        headingBox.setAlignment(Pos.CENTER);
        root.setTop(headingBox);
        ///placeholder\\\
    }

    public static void main(String[] args) {
        launch(args);
    }
}
