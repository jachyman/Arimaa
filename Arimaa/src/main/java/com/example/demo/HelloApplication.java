package com.example.demo;

import com.example.demo.board.Board;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    Button startButton, backButton;
    Scene menuScene, gameScene;
    int width = 800;
    int height = 800;
    GridPane menuGrid, gameGrid;
    Board board;
    Game game;

    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start(Stage stage) throws IOException {

        board = new Board();

        // SET GRIDS
        setMenuGrid();
        setGameGrid();

        // CREATE STAGES
        createStartScene(stage);
        createGameScene(stage);

        // STAGE
        setStage(stage);
        stage.show();

        game = new Game(board, gameGrid);

        // CHOOSE PIECE POSITION
        //game.choosePiecesPosition(Player.WHITE);
        //game.choosePiecesPosition(Player.BLACK);

        game.generatePiecesPosition();

        // GAME
        game.play();
    }

    private void setStage(Stage stage){
        stage.setScene(menuScene);
        stage.setTitle("Arimaa");
    }

    private void setMenuGrid(){
        menuGrid = new GridPane();
        menuGrid.setPadding(new Insets(10, 10, 10,10));
        menuGrid.setVgap(1);
        menuGrid.setHgap(1);
    }

    private void setGameGrid(){
        gameGrid = new GridPane();
        gameGrid.setPadding(new Insets(10, 10, 10,10));
        gameGrid.setVgap(8);
        gameGrid.setHgap(10);
    }

    private void createStartScene(Stage stage){
        Label menuLabel = new Label("Menu");

        startButton = new Button("Start");
        startButton.setOnAction(e -> stage.setScene(gameScene));

        GridPane.setConstraints(startButton, 0, 5);
        GridPane.setConstraints(menuLabel, 0, 0);

        menuGrid.getChildren().addAll(menuLabel, startButton);
        menuGrid.setAlignment(Pos.CENTER);
        menuScene = new Scene(menuGrid, width, height);
    }

    private void createGameScene(Stage stage){
        backButton = new Button("Back to menu");
        backButton.setOnAction(e -> stage.setScene(menuScene));

        GridPane.setConstraints(backButton, 10 , 0);

        createArimaaBoard();
        gameGrid.getChildren().add(backButton);
        gameScene = new Scene(gameGrid, width, height);
    }

    private void createArimaaBoard() {

        int count = 0;
        double s = 70; // side of tile

        for (int y = 0; y < 8; ++y){
            for (int x = 0; x < 8; ++x){
                Rectangle tile = new Rectangle(s, s, s, s);
                board.tiles[y][x].tileSquare = tile;
                gameGrid.add(tile, x, y);
            }
        }
        board.clear();

    }
}