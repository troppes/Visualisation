package de.htw.app;

import de.htw.app.games.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    GameMode gameMode = new Color(levelDimensionX, levelDimensionY);

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
        scene.getStylesheets().add("style.css");


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

        //ToDo: Remove this whole button
        Button jumpToGraphsButton = new Button("Str8 to Graphs");
        jumpToGraphsButton.setOnAction(event -> jumpToGraphs());
        root.setRight(jumpToGraphsButton);

        switch (gameModeCounter) {
            case 0: //Color
                heading = new Label("Find the differently COLORED target");
                heading.setFont(new Font("Arial", 30));
                headingBox.getChildren().addAll(heading, subHeading);

                if(timingCounter==0) gameMode = new Color(levelDimensionX, levelDimensionY);
                break;
            case 1: //Orientation
                heading = new Label("Find the differently ORIENTED target");
                heading.setFont(new Font("Arial", 30));
                headingBox.getChildren().addAll(heading, subHeading);

                if(timingCounter==0) gameMode = new Orientation(levelDimensionX, levelDimensionY);
                break;
            case 2: //Size
                heading = new Label("Find the differently SIZED target");
                heading.setFont(new Font("Arial", 30));
                headingBox.getChildren().addAll(heading, subHeading);

                if(timingCounter==0) gameMode = new Size(levelDimensionX, levelDimensionY);
                break;
            case 3: //Grouping
                heading = new Label("Find the differently GROUPED target");
                heading.setFont(new Font("Arial", 30));
                headingBox.getChildren().addAll(heading, subHeading);

                if(timingCounter==0) gameMode = new Grouping(levelDimensionX, levelDimensionY);
                break;
            case 4: //Color + Orientation, Size
                heading = new Label("Find the differently COLORED target");
                heading.setFont(new Font("Arial", 30));
                headingBox.getChildren().addAll(heading, subHeading);

                if(timingCounter==0) gameMode = new Color(levelDimensionX, levelDimensionY);
                if(timingCounter==0) gameMode.setDistractors(new GameMode.possibleModes[]{GameMode.possibleModes.ORIENTATION, GameMode.possibleModes.SIZE});
                break;
            case 5: //Orientation + Size, Color
                heading = new Label("Find the differently ORIENTED target");
                heading.setFont(new Font("Arial", 30));
                headingBox.getChildren().addAll(heading, subHeading);

                if(timingCounter==0) gameMode = new Orientation(levelDimensionX, levelDimensionY);
                if(timingCounter==0) gameMode.setDistractors(new GameMode.possibleModes[]{GameMode.possibleModes.SIZE, GameMode.possibleModes.COLOR});
                break;
            case 6: //Size + Color, Orientation
                heading = new Label("Find the differently SIZED target");
                heading.setFont(new Font("Arial", 30));
                headingBox.getChildren().addAll(heading, subHeading);

                if(timingCounter==0) gameMode = new Size(levelDimensionX, levelDimensionY);
                if(timingCounter==0) gameMode.setDistractors(new GameMode.possibleModes[]{GameMode.possibleModes.COLOR, GameMode.possibleModes.ORIENTATION});
                break;
            case 7: //Grouping + Color, Orientation, Size
                heading = new Label("Find the differently GROUPED target");
                heading.setFont(new Font("Arial", 30));
                headingBox.getChildren().addAll(heading, subHeading);

                if(timingCounter==0) gameMode = new Grouping(levelDimensionX, levelDimensionY);
                if(timingCounter==0) gameMode.setDistractors(new GameMode.possibleModes[]{GameMode.possibleModes.COLOR, GameMode.possibleModes.ORIENTATION, GameMode.possibleModes.SIZE});
                break;
        }

        skipPromptButton.setDisable(true);
        startPromptButton.setDisable(false);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(timings[timingCounter]), event -> root.setCenter(null)));
        timeline.setCycleCount(1);

        gameMode.generateLevel();
        Pane activeLevel = gameMode.getLevel();

        if(timingCounter==0) playedGameModes.add(gameMode);  //adding the current game mode as the last played game mode

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
                if(timingCounter==0) playedGameModes.remove(playedGameModes.size() - 1); //remove the last game played, as it was successfully completed
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

        playedGameModes.get(0).getMeanDistance();

        float meanTime = 0F;
        for (GameMode gameMode : playedGameModes) {
            if (gameMode.getLowestTime() == -1) { // Add a 3 second penalty, if they fail on the 1 second barrier.
                meanTime += 2000;
            } else {
                meanTime += gameMode.getLowestTime();
            }
            String numberOfDistractors = gameMode.getDistractors().length == 0 ? "no" : Arrays.toString(gameMode.getDistractors());
            recordString.append(gameMode.getGameMode()).append(" with ").append(numberOfDistractors)
                    .append(" distractions. Your lowest time: ").append(gameMode.getLowestTime()).append("\n");
        }
        player.setMeanTime(meanTime/playedGameModes.size());

//        try {
//            player.setId(ConnectionManager.POSTRequest("https://cms.reitz.dev/items/vis02_player/", player));
//        } catch (IOException e) {
//            System.out.println("Failed to send data!");
//        }

        List<Player> players = ConnectionManager.GETRequest("https://cms.reitz.dev/items/vis02_player/?fields=id,name,mean_distance,mean_time", Player.class, false);
        List<GameMode> gameModes = ConnectionManager.GETRequest("https://cms.reitz.dev/items/vis02_game_mode/?fields=id,distractors,game_mode,lowest_time,mean_distance", GameMode.class, true);

        assert players != null;
        root.setRight(generatePlayerTable(players));


        assert gameModes != null;
        root.setTop(generateAverageTimes(gameModes));
        root.setBottom(generateDistanceChart(gameModes));

        Pane leftPane = new Pane();
        //leftPane.setMaxSize(300, 100);

        final Label caption = new Label("");
        caption.setTextFill(javafx.scene.paint.Color.BLACK);
        caption.setStyle("-fx-font: 12 arial;");

        leftPane.getChildren().addAll(generateMeanPieCharts(caption, gameModes), caption);

        root.setLeft(leftPane);

        Label finishRecord = new Label("Times:\n" + recordString);
        Label meanTimeLabel = new Label("Mean Time: " + player.getMeanTime()+"ms");
        Label meanDistanceLabel = new Label("Mean Distance: " + player.getMeanDistance()+"px");
        finishRecord.setFont(new Font("Arial", 20));
        meanTimeLabel.setFont(new Font("Arial", 20));
        meanDistanceLabel.setFont(new Font("Arial", 20));

        VBox headingBox = new VBox();
        headingBox.getChildren().addAll(finish/*, finishRecord, meanTimeLabel, meanDistanceLabel*/);
        headingBox.setAlignment(Pos.CENTER);
        root.setCenter(headingBox);
    }

    private void test(List<GameMode> gameModes) {

        while (gameModes.size() > 0){

            ArrayList<GameMode> singleGamemode = new ArrayList<>();
            singleGamemode.add(gameModes.remove(0));
            for (int i = 0; i < gameModes.size(); i++) {
                if(singleGamemode.get(0).equals(gameModes.get(i))){
                    singleGamemode.add(gameModes.get(i));
                }
            }
            double average = singleGamemode.stream().mapToDouble(GameMode::getMeanDistance).average().orElse(0.0);
            double min = singleGamemode.stream().mapToDouble(GameMode::getMeanDistance).min().orElse(0.0);
            double max = singleGamemode.stream().mapToDouble(GameMode::getMeanDistance).max().orElse(0.0);

            System.out.println(singleGamemode.get(0).getGameMode());
            System.out.println(Arrays.toString(singleGamemode.get(0).getDistractors()));
            System.out.println(singleGamemode.get(0).getMeanDistance());
            System.out.println(singleGamemode.size());
            System.out.println(average);
            System.out.println(min);
            System.out.println(max);

        }

    }

    TableView<Player> generatePlayerTable(List<Player> players) {

        TableView<Player> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Player, String> column1 = new TableColumn<>("Name");
        column1.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Player, String> column2 = new TableColumn<>("Mean time");
        column2.setSortType(TableColumn.SortType.DESCENDING);
        column2.setCellValueFactory(new PropertyValueFactory<>("meanTime"));
        column2.setComparator(column2.getComparator().reversed());

        TableColumn<Player, String> column3 = new TableColumn<>("Mean distance");
        column3.setCellValueFactory(new PropertyValueFactory<>("meanDistance"));
        column3.setComparator(column3.getComparator().reversed());

        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);
        tableView.getColumns().add(column3);

        tableView.setItems(FXCollections.observableList(players));

        tableView.getSortOrder().add(column2);
        tableView.sort();

        //select current user
        tableView.getSelectionModel().select(players.indexOf(player));
        tableView.scrollTo(players.indexOf(player));

        return tableView;
    }

    BarChart<String, Number> generateAverageTimes(List<GameMode> gameModes){
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> barChart = new BarChart<>(xAxis,yAxis);
        barChart.setTitle("Average time per prompt");

        barChart.setMaxHeight(280);
        barChart.setCategoryGap(50);

        float[] averageTimes = new float[8];
        int[] averageTimeCounter = new int[8];

        for (GameMode gameMode: gameModes) {
            if(GameMode.possibleModes.COLOR == gameMode.getGameMode() && gameMode.hasSameDistractors(new GameMode.possibleModes[]{})){
                if(gameMode.getLowestTime() == -1){
                    averageTimes[0] += 2000;
                }else{
                    averageTimes[0] += gameMode.getLowestTime();
                }
                averageTimeCounter[0] += 1;
            } else if(GameMode.possibleModes.ORIENTATION == gameMode.getGameMode() && gameMode.hasSameDistractors(new GameMode.possibleModes[]{})) {
                if(gameMode.getLowestTime() == -1){
                    averageTimes[1] += 2000;
                }else{
                    averageTimes[1] += gameMode.getLowestTime();
                }
                averageTimeCounter[1] += 1;
            }else if(GameMode.possibleModes.SIZE == gameMode.getGameMode() && gameMode.hasSameDistractors(new GameMode.possibleModes[]{})) {
                if(gameMode.getLowestTime() == -1){
                    averageTimes[2] += 2000;
                }else{
                    averageTimes[2] += gameMode.getLowestTime();
                }
                averageTimeCounter[2] += 1;
            }else if(GameMode.possibleModes.GROUPING == gameMode.getGameMode() && gameMode.hasSameDistractors(new GameMode.possibleModes[]{})){
                if(gameMode.getLowestTime() == -1){
                    averageTimes[3] += 2000;
                }else{
                    averageTimes[3] += gameMode.getLowestTime();
                }
                averageTimeCounter[3] += 1;
            }else if(GameMode.possibleModes.COLOR == gameMode.getGameMode() && gameMode.hasSameDistractors(new GameMode.possibleModes[]{GameMode.possibleModes.ORIENTATION, GameMode.possibleModes.SIZE})){
                if(gameMode.getLowestTime() == -1){
                    averageTimes[4] += 2000;
                }else{
                    averageTimes[4] += gameMode.getLowestTime();
                }
                averageTimeCounter[4] += 1;
            }else if(GameMode.possibleModes.ORIENTATION == gameMode.getGameMode() && gameMode.hasSameDistractors(new GameMode.possibleModes[]{GameMode.possibleModes.COLOR, GameMode.possibleModes.SIZE})){
                if(gameMode.getLowestTime() == -1){
                    averageTimes[5] += 2000;
                }else{
                    averageTimes[5] += gameMode.getLowestTime();
                }
                averageTimeCounter[5] += 1;
            }else if(GameMode.possibleModes.SIZE == gameMode.getGameMode() && gameMode.hasSameDistractors(new GameMode.possibleModes[]{GameMode.possibleModes.COLOR, GameMode.possibleModes.ORIENTATION})){
                if(gameMode.getLowestTime() == -1){
                    averageTimes[6] += 2000;
                }else{
                    averageTimes[6] += gameMode.getLowestTime();
                }
                averageTimeCounter[6] += 1;
            }else if(GameMode.possibleModes.GROUPING == gameMode.getGameMode() && gameMode.hasSameDistractors(new GameMode.possibleModes[]{GameMode.possibleModes.COLOR, GameMode.possibleModes.ORIENTATION, GameMode.possibleModes.SIZE})){
                if(gameMode.getLowestTime() == -1){
                    averageTimes[7] += 2000;
                }else{
                    averageTimes[7] += gameMode.getLowestTime();
                }
                averageTimeCounter[7] += 1;
            }
        }

        for(int i = 0; i < averageTimeCounter.length; i++){
            if(averageTimeCounter[i] == 0) averageTimes[i] = 0.0F;
            averageTimes[i] = averageTimes[i]/averageTimeCounter[i];
        }

        XYChart.Series<String, Number> averageSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> playerSeries = new XYChart.Series<>();

        playerSeries.setName("Your Score");
        averageSeries.setName("Average");

        playerSeries.getData().add(new XYChart.Data<>("Color w/ \nno distractors", playedGameModes.get(0).getLowestTime()));
        averageSeries.getData().add(new XYChart.Data<>("Color w/ \nno distractors", averageTimes[0]));

        playerSeries.getData().add(new XYChart.Data<>("Size w/ \nno distractors", playedGameModes.get(1).getLowestTime()));
        averageSeries.getData().add(new XYChart.Data<>("Size w/ \nno distractors", averageTimes[1]));

        playerSeries.getData().add(new XYChart.Data<>("Orientation w/ \nno distractors", playedGameModes.get(2).getLowestTime()));
        averageSeries.getData().add(new XYChart.Data<>("Orientation w/ \nno distractors", averageTimes[2]));

        playerSeries.getData().add(new XYChart.Data<>("Grouping w/ \nno distractors", playedGameModes.get(3).getLowestTime()));
        averageSeries.getData().add(new XYChart.Data<>("Grouping w/ \nno distractors", averageTimes[3]));

        playerSeries.getData().add(new XYChart.Data<>("Color w/ \norientation and size", playedGameModes.get(4).getLowestTime()));
        averageSeries.getData().add(new XYChart.Data<>("Color w/ \norientation and size", averageTimes[4]));

        playerSeries.getData().add(new XYChart.Data<>("Size w/ \ncolor and size", playedGameModes.get(5).getLowestTime()));
        averageSeries.getData().add(new XYChart.Data<>("Size w/ \ncolor and size", averageTimes[5]));

        playerSeries.getData().add(new XYChart.Data<>("Orientation w/ \ncolor and orientation", playedGameModes.get(6).getLowestTime()));
        averageSeries.getData().add(new XYChart.Data<>("Orientation w/ \ncolor and orientation", averageTimes[6]));

        playerSeries.getData().add(new XYChart.Data<>("Grouping w/ \ncolor, orientation and size", playedGameModes.get(7).getLowestTime()));
        averageSeries.getData().add(new XYChart.Data<>("Grouping w/ \ncolor, orientation and size", averageTimes[7]));

        barChart.getData().addAll(playerSeries, averageSeries);

        return barChart;
    }

    BarChart<String, Number> generateDistanceChart(List<GameMode> gameModes){
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> barChart = new BarChart<>(xAxis,yAxis);
        barChart.setTitle("Average distance per prompt");

        barChart.setMaxHeight(280);
        barChart.setCategoryGap(50);

        float[] averageDistances = new float[8];
        int[] distanceCounter = new int[8];

        for (GameMode gameMode: gameModes) {
            if(GameMode.possibleModes.COLOR == gameMode.getGameMode() && gameMode.hasSameDistractors(new GameMode.possibleModes[]{})){
                distanceCounter[0] += 1;
                averageDistances[0] += gameMode.getMeanDistance();
            } else if(GameMode.possibleModes.ORIENTATION == gameMode.getGameMode() && gameMode.hasSameDistractors(new GameMode.possibleModes[]{})) {
                distanceCounter[1] += 1;
                averageDistances[1] += gameMode.getMeanDistance();
            }else if(GameMode.possibleModes.SIZE == gameMode.getGameMode() && gameMode.hasSameDistractors(new GameMode.possibleModes[]{})) {
                distanceCounter[2] += 1;
                averageDistances[2] += gameMode.getMeanDistance();
            }else if(GameMode.possibleModes.GROUPING == gameMode.getGameMode() && gameMode.hasSameDistractors(new GameMode.possibleModes[]{})){
                distanceCounter[3] += 1;
                averageDistances[3] += gameMode.getMeanDistance();
            }else if(GameMode.possibleModes.COLOR == gameMode.getGameMode() && gameMode.hasSameDistractors(new GameMode.possibleModes[]{GameMode.possibleModes.ORIENTATION, GameMode.possibleModes.SIZE})){
                distanceCounter[4] += 1;
                averageDistances[4] += gameMode.getMeanDistance();
            }else if(GameMode.possibleModes.ORIENTATION == gameMode.getGameMode() && gameMode.hasSameDistractors(new GameMode.possibleModes[]{GameMode.possibleModes.COLOR, GameMode.possibleModes.SIZE})){
                distanceCounter[5] += 1;
                averageDistances[5] += gameMode.getMeanDistance();
            }else if(GameMode.possibleModes.SIZE == gameMode.getGameMode() && gameMode.hasSameDistractors(new GameMode.possibleModes[]{GameMode.possibleModes.COLOR, GameMode.possibleModes.ORIENTATION})){
                distanceCounter[6] += 1;
                averageDistances[6] += gameMode.getMeanDistance();
            }else if(GameMode.possibleModes.GROUPING == gameMode.getGameMode() && gameMode.hasSameDistractors(new GameMode.possibleModes[]{GameMode.possibleModes.COLOR, GameMode.possibleModes.ORIENTATION, GameMode.possibleModes.SIZE})){
                distanceCounter[7] += 1;
                averageDistances[7] += gameMode.getMeanDistance();
            }
        }

        for(int i = 0; i < distanceCounter.length; i++){
            if(distanceCounter[i] == 0) averageDistances[i] = 0.0F;
            averageDistances[i] = averageDistances[i]/distanceCounter[i];
        }

        XYChart.Series<String, Number> averageSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> playerSeries = new XYChart.Series<>();

        playerSeries.setName("Your Score");
        averageSeries.setName("Average");

        playerSeries.getData().add(new XYChart.Data<>("Color w/ \nno distractors", playedGameModes.get(0).getMeanDistance()));
        averageSeries.getData().add(new XYChart.Data<>("Color w/ \nno distractors", averageDistances[0]));

        playerSeries.getData().add(new XYChart.Data<>("Size w/ \nno distractors", playedGameModes.get(1).getMeanDistance()));
        averageSeries.getData().add(new XYChart.Data<>("Size w/ \nno distractors", averageDistances[1]));

        playerSeries.getData().add(new XYChart.Data<>("Orientation w/ \nno distractors", playedGameModes.get(2).getMeanDistance()));
        averageSeries.getData().add(new XYChart.Data<>("Orientation w/ \nno distractors", averageDistances[2]));

        playerSeries.getData().add(new XYChart.Data<>("Grouping w/ \nno distractors", playedGameModes.get(3).getMeanDistance()));
        averageSeries.getData().add(new XYChart.Data<>("Grouping w/ \nno distractors", averageDistances[3]));

        playerSeries.getData().add(new XYChart.Data<>("Color w/ \norientation and size", playedGameModes.get(4).getMeanDistance()));
        averageSeries.getData().add(new XYChart.Data<>("Color w/ \norientation and size", averageDistances[4]));

        playerSeries.getData().add(new XYChart.Data<>("Size w/ \ncolor and size", playedGameModes.get(5).getMeanDistance()));
        averageSeries.getData().add(new XYChart.Data<>("Size w/ \ncolor and size", averageDistances[5]));

        playerSeries.getData().add(new XYChart.Data<>("Orientation w/ \ncolor and orientation", playedGameModes.get(6).getMeanDistance()));
        averageSeries.getData().add(new XYChart.Data<>("Orientation w/ \ncolor and orientation", averageDistances[6]));

        playerSeries.getData().add(new XYChart.Data<>("Grouping w/ \ncolor, orientation and size", playedGameModes.get(7).getMeanDistance()));
        averageSeries.getData().add(new XYChart.Data<>("Grouping w/ \ncolor, orientation and size", averageDistances[7]));

        barChart.getData().addAll(playerSeries, averageSeries);

        return barChart;

    }

    HBox generateMeanPieCharts(Label caption, List<GameMode> gameModes){
        //time
        float averageTotalTime = 0;
        int averageTimeCounter = 0;

        for (GameMode gameMode: gameModes) {
            if(gameMode.getLowestTime() == -1){
                averageTotalTime += 2000;
            }else{
                averageTotalTime += gameMode.getLowestTime();
            }
            averageTimeCounter += 1;
        }

        averageTotalTime/=averageTimeCounter;

        //distance
        float averageTotalDistance = 0;
        int averageDistanceCounter = 0;

        for (GameMode gameMode: gameModes) {
            if(gameMode.getLowestTime() == -1){
                averageTotalDistance += 2000;
            }else{
                averageTotalDistance += gameMode.getLowestTime();
            }
            averageDistanceCounter += 1;
        }

        averageTotalDistance/=averageDistanceCounter;

        HBox hbox = new HBox();

        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Your average", (int)player.getMeanTime()),   //cast to int to eradicate everything after the comma
                        new PieChart.Data("Total average", (int)averageTotalTime));   //cast to int to eradicate everything after the comma
        PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Total average time");
        chart.setMaxSize(50, 50);

        //click show data
        for (PieChart.Data data : chart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                    e -> {
                        caption.setTranslateX(e.getSceneX());
                        caption.setTranslateY(e.getY() + 80);
                        caption.setText(data.getPieValue() + " pixel");
                    });
        }

        hbox.getChildren().add(chart);

        pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Your average", (int)player.getMeanDistance()),   //cast to int to eradicate everything after the comma
                        new PieChart.Data("Total average", (int)averageTotalDistance));   //cast to int to eradicate everything after the comma
        chart = new PieChart(pieChartData);
        chart.setTitle("Total average distance");
        chart.setMaxSize(50, 50);

        //click show data
        for (PieChart.Data data : chart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                    e -> {
                        caption.setTranslateX(e.getSceneX());
                        caption.setTranslateY(e.getY() + 80);
                        caption.setText(data.getPieValue() + " pixel");
                    });
        }

        hbox.getChildren().add(chart);

        return hbox;
    }

    //ToDo: remove this method
    void jumpToGraphs(){
        ArrayList<GameMode> gameModes = new ArrayList<>();

        for(int i=0; i<totalGameModes; i++){
            GameMode gameMode = null;

            switch (i) {
                case 0: //Color
                    gameMode = new Color(levelDimensionX, levelDimensionY);
                    break;
                case 1: //Orientation
                    gameMode = new Orientation(levelDimensionX, levelDimensionY);
                    break;
                case 2: //Size
                    gameMode = new Size(levelDimensionX, levelDimensionY);
                    break;
                case 3: //Grouping
                    gameMode = new Grouping(levelDimensionX, levelDimensionY);
                    break;
                case 4: //Color + Orientation, Size
                    gameMode = new Color(levelDimensionX, levelDimensionY);
                    gameMode.setDistractors(new GameMode.possibleModes[]{GameMode.possibleModes.ORIENTATION, GameMode.possibleModes.SIZE});
                    break;
                case 5: //Orientation + Size, Color
                    gameMode = new Orientation(levelDimensionX, levelDimensionY);
                    gameMode.setDistractors(new GameMode.possibleModes[]{GameMode.possibleModes.SIZE, GameMode.possibleModes.COLOR});
                    break;
                case 6: //Size + Color, Orientation
                    gameMode = new Size(levelDimensionX, levelDimensionY);
                    gameMode.setDistractors(new GameMode.possibleModes[]{GameMode.possibleModes.COLOR, GameMode.possibleModes.ORIENTATION});
                    break;
                case 7: //Grouping + Color, Orientation, Size
                    gameMode = new Grouping(levelDimensionX, levelDimensionY);
                    gameMode.setDistractors(new GameMode.possibleModes[]{GameMode.possibleModes.COLOR, GameMode.possibleModes.ORIENTATION, GameMode.possibleModes.SIZE});
                    break;
            }

            assert gameMode != null;
            gameMode.setLowestTime(250);
            gameMode.setMeanDistance(40);

            gameModes.add(gameMode);
        }

        playedGameModes = gameModes;

        player.setGames(gameModes);
        player.setMeanTime(250);
        player.setMeanDistance(40);

        finishGame();
    }

    public static void main(String[] args) {
        launch(args);
    }

}

