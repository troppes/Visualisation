package de.htw.app;

import java.util.*;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

public class Vis01 extends Application {

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
    GameShape.shape unplayedShape;
    HBox circlesBox;
    Button finishButton;
    Stage primaryStage;
    //tracking data
    ArrayList<GameShape> playedShapes = new ArrayList<>();
    float totalX = 0;
    int clicksOverOptimum = 0, clicksForPrompt = 0, optimalClicksForPrompt = 0;

    GameObject currentPlayer;

    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();
        root.setPrefSize(1280, 720);

        currentPlayer = new GameObject();
        currentPlayer.setUser("Player");
        currentPlayer.setId(-1);

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
        result.ifPresent(s -> currentPlayer.setUser(s));
        if(result.isEmpty()){
            System.exit(0);
        }

        Text welcomeMessage = new Text(String.join("\n"
                , "Welcome " + currentPlayer.getUser() + ","
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
                modifySize(generatedShape, 2);
            }
        });
        //- S down
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            switch (key.getCode()) {
                case W: {
                    modifySize(generatedShape, 2);
                    break;
                }
                case S: {
                    modifySize(generatedShape, -2);
                    break;
                }
                case E: {
                    modifySize(generatedShape, 10);
                    break;
                }
                case D: {
                    modifySize(generatedShape, -10);
                    break;
                }
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

        //recalculate random ratio, if it's equal to one, as this case doesn't give any proper data
        while (randomRatio == 1){
            randomRatio = .1f + random.nextFloat() * (5 - .1f);   //min + r.nextFloat() * (max - min);
            randomRatio = ((int) (randomRatio * 10)) / 10f;    //Shorten float to first decimal place
        }

        if(unplayedShape==null) {
            if (randomShape == 0)
                currentShape = GameShape.shape.CIRCLE;
            else
                currentShape = GameShape.shape.SQUARE;
        }
        else{
            currentShape = unplayedShape;
            randomShape = 1;
            if(unplayedShape == GameShape.shape.CIRCLE) randomShape = 0;
        }

        //set the unplayed shape to whatever shape didn't get generated after the first level generation
        //this is used, so that the first two shapes are always different ones
        unplayedShape = null;
        if(playedShapes.size() == 0){
            unplayedShape = GameShape.shape.CIRCLE;
            if(randomShape==0) unplayedShape = GameShape.shape.SQUARE;
        }

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
        //return Float.isInfinite(x) ? 0 : x;
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

        if (estimatedRatio!=1) {
            float x = calculateX(ratio, estimatedRatio);
            //Make sure its not infinity, if so set to 0

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
        }
        //draw new game
        generateNewGame();
    }

    void finishGame() {
        try {
            if (playedShapes.size() != 0) {
                float averageX = totalX / playedShapes.size();

                GameShape[] gameShapes = new GameShape[playedShapes.size()];
                gameShapes = playedShapes.toArray(gameShapes);

                currentPlayer.setAverage_x(averageX);
                currentPlayer.setShapes_played(gameShapes);
                currentPlayer.setClicks(clicksOverOptimum);
                POSTRequest(currentPlayer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        generateResultScreen();
    }

    void generateResultScreen() {

        List<GameShape> squareShapes = getSquares();
        List<GameShape> circleShapes = getCircles();
        List<GameObject> gameObjects = getGameObjects();

        BorderPane results = new BorderPane();
        results.setPrefSize(1280, 720);

        VBox centerContent = new VBox();
        centerContent.setAlignment(Pos.CENTER);

        Text welcomeMessage = new Text(String.join("\n"
                , "Hello " + currentPlayer.getUser() + ","
                , "Your total X was " + Math.round((totalX / playedShapes.size()) * 100F) / 100F + "."
                , "X is calculated by: (perceived size ratio) = (actual ratio of area contents)^x."
                , "That means the closer your are to 1 the better you are. \n"
        ));
        welcomeMessage.setStyle("-fx-font: 16 arial;");

        centerContent.getChildren().add(welcomeMessage);
        results.setCenter(centerContent);

        results.setLeft(generateAverageClicksBarChart(gameObjects));
        results.setTop(generateAverageXBarChart(squareShapes, circleShapes));

        results.setRight(generateHighScores(gameObjects));
        results.setBottom(generateScatter(circleShapes, squareShapes));

        Scene scene = new Scene(results);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    ScatterChart<Number, Number> generateScatter(List<GameShape> circles, List<GameShape> squares){

        final NumberAxis xAxis = new NumberAxis(0, 5, .5);
        final NumberAxis yAxis = new NumberAxis(0, 2, .2);
        final ScatterChart<Number,Number> sc = new ScatterChart<>(xAxis, yAxis);
        xAxis.setLabel("Ratio");
        yAxis.setLabel("X");
        sc.setTitle("X to Ratio");

        ArrayList<XYChart.Data<Number, Number>> circleData = new ArrayList<>();
        ArrayList<XYChart.Data<Number, Number>> squareData = new ArrayList<>();

        for (GameShape circle : circles) {
            circleData.add(new XYChart.Data<>(circle.getRatio(), circle.getX()));
        }

        for (GameShape square : squares) {
            squareData.add(new XYChart.Data<>(square.getRatio(), square.getX()));
        }

        XYChart.Series<Number, Number> circleSeries = new XYChart.Series<>();
        circleSeries.setName("Circles");
        circleSeries.setData(FXCollections.observableList(circleData));


        XYChart.Series<Number, Number> squareSeries = new XYChart.Series<>();
        squareSeries.setName("Squares");
        squareSeries.setData(FXCollections.observableList(squareData));

        sc.getData().add(circleSeries);
        sc.getData().add(squareSeries);

        return sc;
    }

    TableView<GameObject> generateHighScores(List<GameObject> gameObjects) {

        gameObjects.sort((o1, o2) -> {
            float d1 = Math.abs(o1.getAverage_x() - 1);
            float d2 = Math.abs(o2.getAverage_x() - 1);
            return Float.compare(d1, d2);
        });

        TableView<GameObject> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<GameObject, String> column1 = new TableColumn<>("Name");
        column1.setCellValueFactory(new PropertyValueFactory<>("user"));

        TableColumn<GameObject, String> column2 = new TableColumn<>("Score");
        column2.setCellValueFactory(new PropertyValueFactory<>("average_x"));

        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);

        tableView.setItems(FXCollections.observableList(gameObjects));

        //select current user
        tableView.getSelectionModel().select(gameObjects.indexOf(currentPlayer));
        tableView.scrollTo(gameObjects.indexOf(currentPlayer));

        return tableView;
    }

    BarChart<String, Number> generateAverageXBarChart(List<GameShape> squareShapes, List<GameShape> circleShapes){
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> barChart = new BarChart<>(xAxis,yAxis);
        barChart.setTitle("Average X Summary");

        barChart.setMaxHeight(300);
        barChart.setCategoryGap(50);

        float averageXTotal = 0, averageXCircles = 0, averageXSquares = 0;
        float averageOverestimatedX = 0, averageOverestimatedXCircles = 0, averageOverestimatedXSquares = 0;
        int overestimatedCirclesCounter = 0, overestimatedSquaresCounter = 0;
        float averageUnderestimatedX = 0, averageUnderestimatedXCircles = 0, averageUnderestimatedXSquares = 0;
        int underestimatedCirclesCounter = 0, underestimatedSquaresCounter = 0;

        for(GameShape shape : squareShapes){
            averageXTotal += shape.getX();
            averageXSquares += shape.getX();

            if(shape.getX() > 1){
                averageOverestimatedX += shape.getX();
                averageOverestimatedXSquares += shape.getX();

                overestimatedSquaresCounter++;
            }
            if(shape.getX() < 1){
                averageUnderestimatedX += shape.getX();
                averageUnderestimatedXSquares += shape.getX();

                underestimatedSquaresCounter++;
            }
        }
        averageXSquares /= squareShapes.size();
        averageOverestimatedXSquares /= overestimatedSquaresCounter;
        averageUnderestimatedXSquares /= underestimatedSquaresCounter;

        for(GameShape shape : circleShapes){
            averageXTotal += shape.getX();
            averageXCircles += shape.getX();

            if(shape.getX() > 1){
                averageOverestimatedX += shape.getX();
                averageOverestimatedXCircles += shape.getX();

                overestimatedCirclesCounter++;
            }
            if(shape.getX() < 1){
                averageUnderestimatedX += shape.getX();
                averageUnderestimatedXCircles += shape.getX();

                underestimatedCirclesCounter++;
            }
        }
        averageXCircles /= circleShapes.size();
        averageXTotal /= squareShapes.size()+circleShapes.size();

        averageOverestimatedXCircles /= overestimatedCirclesCounter;
        averageOverestimatedX /= overestimatedCirclesCounter+overestimatedSquaresCounter;

        averageUnderestimatedXCircles /= underestimatedCirclesCounter;
        averageUnderestimatedX /= underestimatedCirclesCounter+underestimatedSquaresCounter;


        float averageXPlayer = currentPlayer.getAverage_x();
        float averageXSquaresPlayer = 0, averageXCirclesPlayer = 0;
        int averageXSquaresPlayerCounter = 0, averageXCirclesPlayerCounter = 0;

        for (GameShape shape : playedShapes) {
            if(shape.getMyShape() == GameShape.shape.CIRCLE){
                averageXSquaresPlayer += shape.getX();
                averageXSquaresPlayerCounter++;
            }
            else if(shape.getMyShape() == GameShape.shape.SQUARE){
                averageXCirclesPlayer += shape.getX();
                averageXCirclesPlayerCounter++;
            }
        }
        averageXSquaresPlayer /= averageXSquaresPlayerCounter;
        averageXCirclesPlayer /= averageXCirclesPlayerCounter;

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Average X");
        series1.getData().add(new XYChart.Data<>("All Shapes", averageXTotal));
        series1.getData().add(new XYChart.Data<>("Circles", averageXCircles));
        series1.getData().add(new XYChart.Data<>("Squares", averageXSquares));

        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("Average overestimated X");
        series2.getData().add(new XYChart.Data<>("All Shapes", averageOverestimatedX));
        series2.getData().add(new XYChart.Data<>("Circles", averageOverestimatedXCircles));
        series2.getData().add(new XYChart.Data<>("Squares", averageOverestimatedXSquares));

        XYChart.Series<String, Number> series4 = new XYChart.Series<>();
        series4.setName("Average underestimated X");
        series4.getData().add(new XYChart.Data<>("All Shapes", averageUnderestimatedX));
        series4.getData().add(new XYChart.Data<>("Circles", averageUnderestimatedXCircles));
        series4.getData().add(new XYChart.Data<>("Squares", averageUnderestimatedXSquares));

        XYChart.Series<String, Number> series5 = new XYChart.Series<>();
        series5.setName("Your X");
        series5.getData().add(new XYChart.Data<>("All Shapes", averageXPlayer));
        series5.getData().add(new XYChart.Data<>("Circles", averageXCirclesPlayer));
        series5.getData().add(new XYChart.Data<>("Squares", averageXSquaresPlayer));

        barChart.getData().addAll(series2, series1, series4, series5);

        return barChart;

    }

    BarChart<Number, String> generateAverageClicksBarChart(List<GameObject> gameObjects){
        final NumberAxis xAxis = new NumberAxis();
        final CategoryAxis yAxis = new CategoryAxis();
        final BarChart<Number,String> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Clicks");
        xAxis.setTickLabelRotation(90);
        xAxis.setLabel("Clicks over the minimum");

        barChart.setMaxWidth(300);
        barChart.setMinHeight(200);
        //barChart.setCategoryGap(50);

        int averageClicksOverOptimum = 0;
        for (GameObject go : gameObjects) {
            averageClicksOverOptimum += go.getClicks();
        }
        averageClicksOverOptimum/=gameObjects.size();

        XYChart.Series<Number, String> series1 = new XYChart.Series<>();
        series1.setName("Clicks Needed");
        series1.getData().add(new XYChart.Data<>(averageClicksOverOptimum, "Average clicks needed"));
        series1.getData().add(new XYChart.Data<>( clicksOverOptimum, "Your clicks needed"));

        barChart.getData().add(series1);

        Tooltip t = new Tooltip("0 is the number of clicks you needed, to get the optimal X as a result. \n Any exceeding or missing number of clicks gets added to this variable");
        Tooltip.install(barChart, t);

        return barChart;

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

        URL obj = new URL("https://cms.reitz.dev/items/shape_game/?fields=user,average_x,clicks,id");
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
            currentPlayer = objectMapper.readValue(response.substring(0, response.length()-1).substring(8), GameObject.class);
        }
        if (responseCode != HttpURLConnection.HTTP_OK) {
            System.out.println("POST Response Code :  " + responseCode);
            System.out.println("POST Response Message : " + postConnection.getResponseMessage());
            throw new IOException("Posting the data did not work!");
        }
    }

    private List<GameObject> getGameObjects() {
        String response = getRequest("https://cms.reitz.dev/items/shape_game/?fields=user,average_x,clicks,id");
        try {
            return Arrays.asList(new ObjectMapper().readValue(response, GameObject[].class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private List<GameShape> getSquares() {
        String response = getRequest("https://cms.reitz.dev/items/shape_ratio/?filter[shape][_eq]=SQUARE&fields=x,ratio,shape,estimated_ratio");
        try {
            return Arrays.asList(new ObjectMapper().readValue(response, GameShape[].class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private List<GameShape> getCircles() {
        String response = getRequest("https://cms.reitz.dev/items/shape_ratio/?filter[shape][_eq]=CIRCLE&fields=x,ratio,shape,estimated_ratio");
        try {
            return Arrays.asList(new ObjectMapper().readValue(response, GameShape[].class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

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

    public static void main(String[] args) {
        launch(args);
    }
}
