package com.arimaa;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Set;

/**
 * This class handles all events related to graphics of Arimaa game
 *
 * @author Jachym Zak
 */

public class GUI {

    private final GridPane menuGrid;
    private GridPane gameGrid;
    private Scene menuScene, gameScene;
    private Label moveCountLabel, currentPlayerLabel;

    private final int mainStageWidth = 800;
    private final int mainStageHeight = 800;
    private final double tileSquareSize = 70;
    private final double pieceSquareSize = 50;
    private final int gameOverStageWidth = 300;
    private final int gameOverStageHeight = 300;

    public final Color emptyTileColor = Color.WHITE;
    public final Color trapTileColor = Color.LIGHTGRAY;
    public final Color selectedTileColor = Color.LIGHTBLUE;
    public final Color movemetToColor = Color.LIGHTGREEN;
    public final Color pullFromColor = Color.LIGHTYELLOW;
    public final Color pushFromColor = Color.LIGHTPINK;
    public final Color pushToTileColor = Color.LIGHTPINK;
    public final Color previousPiecePositonColor = Color.LIGHTCYAN;

    public final Color pushingTileColor = selectedTileColor;
    public final Color finishPushTileColor = movemetToColor;
    public final Color pullToTileColor = previousPiecePositonColor;

    Stopwatch goldPlayerStopwatch;
    Stopwatch silverPlayerStopwatch;

    Stage mainStage;
    public GUI(Stage mainStage) {
        this.mainStage = mainStage;
        menuGrid = new GridPane();
    }

    /**
     * Set stage of main menu
     */
    public void setStageMenu(){
        mainStage.setScene(menuScene);
        mainStage.setTitle("Arimaa");
    }

    /**
     * Add all pieces on board to game GridPane
     * @param board all of its pieces will be set
     */
    public void setPieces(Board board){
        for (int y = 0; y < 8; ++y){
            for (int x = 0; x < 8; ++x){
                Tile tile = board.tiles[y][x];
                setPiece(tile);
            }
        }
    }
    private void setPiece(Tile tile){
        gameGrid.add(tile.pieceText, tile.tileCoordinateX, tile.tileCoordinateY);
    }

    /**
     * Create main menu scene
     * @param game game which main menu will start after user chooses game type
     */
    public void createMenuScene(Game game){
        Button versusPlayerButton, versusComputerButton;
        Label menuLabel;

        menuLabel = new Label("Menu");

        versusPlayerButton = new Button("Player VS Player");
        versusPlayerButton.setOnAction(e -> {
            game.startVersusPlayer();
            mainStage.setScene(gameScene);
        });

        versusComputerButton = new Button("Player VS Computer");
        versusComputerButton.setOnAction(e -> {
            game.startVersusComputer();
            mainStage.setScene(gameScene);
        });

        GridPane.setConstraints(menuLabel, 0, 0);
        GridPane.setConstraints(versusPlayerButton, 0, 5);
        GridPane.setConstraints(versusComputerButton, 0, 6);

        menuGrid.getChildren().addAll(menuLabel, versusPlayerButton, versusComputerButton);
        menuGrid.setAlignment(Pos.CENTER);

        menuScene = new Scene(menuGrid, mainStageWidth, mainStageHeight);
    }

    /**
     * Create game scene
     * @param board game scene will be created based this board
     * @param game game scene will be calling methods from this game
     */
    public void createGameScene(Board board, Game game){
        Button backButton, endTurnButton;
        Label goldPlayerStopwatchLabel;

        backButton = new Button("Back to menu");
        backButton.setOnAction(e -> backToMainMenu());

        endTurnButton = new Button("End turn");
        endTurnButton.setOnAction(e -> {
            if (game.setupPhase || game.moveCount > 0){
                game.endTurn();
            }
        });

        moveCountLabel = new Label();
        currentPlayerLabel = new Label();
        goldPlayerStopwatchLabel = new Label("Gold player time:");
        goldPlayerStopwatch = new Stopwatch();

        GridPane.setConstraints(backButton, 10 , 0);
        GridPane.setConstraints(endTurnButton, 10 , 1);
        GridPane.setConstraints(moveCountLabel, 10 , 2);
        GridPane.setConstraints(currentPlayerLabel, 10 , 3);
        GridPane.setConstraints(goldPlayerStopwatchLabel, 10, 4);
        GridPane.setConstraints(goldPlayerStopwatch.timeText, 10, 5);

        setGameGrid();

        setTileSquares(board);
        clearBoard(board);
        setPieces(board);

        gameGrid.getChildren().addAll(backButton, endTurnButton, moveCountLabel, currentPlayerLabel);
        gameGrid.getChildren().addAll(goldPlayerStopwatchLabel, goldPlayerStopwatch.timeText);

        if (game.gameType == GameType.versusPlayer){
            setSilverPlayerStopwatch();
        }

        gameScene = new Scene(gameGrid, mainStageWidth, mainStageHeight);
    }

    private void setSilverPlayerStopwatch() {
        Label silverPlayerStopwatchLabel;
        silverPlayerStopwatchLabel = new Label("Silver player time:");
        silverPlayerStopwatch = new Stopwatch();
        GridPane.setConstraints(silverPlayerStopwatchLabel, 10, 6);
        GridPane.setConstraints(silverPlayerStopwatch.timeText, 10, 7);
        gameGrid.getChildren().addAll(silverPlayerStopwatchLabel, silverPlayerStopwatch.timeText);
    }

    private void backToMainMenu() {
        goldPlayerStopwatch.stop();
        if (silverPlayerStopwatch != null){
            silverPlayerStopwatch.stop();
        }
        mainStage.setScene(menuScene);
    }

    /**
     * Update move counter label
     * @param moveCount move counter will be set to this value
     */
    public void setMoveCounter(int moveCount){
        moveCountLabel.setText("Moves made: " + Integer.toString(moveCount));
    }

    /**
     * Update current player label
     * @param currentPlayer current player label will be set to this value
     */
    public void setCurrentPlayer(Player currentPlayer){
        String currentPlayerString = currentPlayer == Player.GOLD ? "Gold" : "Silver";
        currentPlayerLabel.setText("Current player: " + currentPlayerString);
    }

    private void setGameGrid(){
        gameGrid = new GridPane();
        gameGrid.setPadding(new Insets(10, 10, 10,10));
        gameGrid.setVgap(8);
        gameGrid.setHgap(10);
    }

    private void setTileSquares(Board board){
        for (int y = 0; y < 8; ++y){
            for (int x = 0; x < 8; ++x){
                Rectangle tileSquare = new Rectangle(tileSquareSize, tileSquareSize);

                board.tiles[y][x].tileSquare = tileSquare;
                //board.tiles[y][x].pieceSquare = pieceSquare;

                gameGrid.add(tileSquare, x, y);
                //gameGrid.add(pieceSquare, x, y);
            }
        }
    }

    /**
     * Fill all tiles in set to specific color
     * @param tiles set of tiles that will be colored
     * @param color this color will be used to fill all tiles
     */
    public void fillTiles(Set<Tile> tiles, Color color){
        for (Tile tile : tiles){
            tile.tileSquare.setFill(color);
        }
    }

    public void fillTile(Tile tile, Color color){
        tile.tileSquare.setFill(color);
    }

    /**
     * Clear all tiles on board from their color and fill trap tiles with trap tile color
     * @param board this board will be cleared
     */
    public void clearBoard(Board board){
        Rectangle tileSquare;
        for (int i = 0; i < 8; ++i){
            for (int j = 0; j < 8; ++j){
                tileSquare = board.tiles[i][j].tileSquare;
                tileSquare.setFill(emptyTileColor);
                if (board.getTile(i, j).isTrap){
                    tileSquare.setFill(trapTileColor);
                }
            }
        }
    }

    /**
     * Create and show game over screen
     * @param winner player that won the game
     * @param game game that the game over screen is for
     */
    public void gameOverScreen(Player winner, Game game){
        Button versusPlayerButton, versusComputerButton;

        GridPane gameOverGrid = new GridPane();
        Stage gameOverStage = new Stage();

        Label gameOverLabel = new Label("GAME OVER");
        String winnerString = winner == Player.GOLD ? "GOLD" : "SILVER";
        Label winnerLabel = new Label("WINNER IS " + winnerString);

        versusPlayerButton = new Button("Player VS Player");
        versusPlayerButton.setOnAction(e ->{
            game.startVersusPlayer();
            mainStage.setScene(gameScene);
            gameOverStage.hide();
        });

        versusComputerButton = new Button("Player VS Computer");
        versusComputerButton.setOnAction(e ->{
            game.startVersusComputer();
            mainStage.setScene(gameScene);
            gameOverStage.hide();
        });


        Button quitButton = new Button("QUIT");
        quitButton.setOnAction(e ->{
            gameOverStage.close();
            mainStage.close();
        });

        GridPane.setConstraints(gameOverLabel, 0, 0);
        GridPane.setConstraints(winnerLabel, 0, 3);
        GridPane.setConstraints(versusPlayerButton, 0, 4);
        GridPane.setConstraints(versusComputerButton, 0, 5);
        GridPane.setConstraints(quitButton, 0, 6);

        gameOverGrid.getChildren().addAll(gameOverLabel, winnerLabel, versusPlayerButton, versusComputerButton, quitButton);
        gameOverGrid.setAlignment(Pos.CENTER);

        Scene gameOverScene = new Scene(gameOverGrid, gameOverStageWidth, gameOverStageHeight);
        gameOverStage.setScene(gameOverScene);
        gameOverStage.setTitle("Game over");

        gameOverStage.show();
    }

    public void showStage(){
        mainStage.show();
    }

    /**
     * Play computer moves with certain delay
     * @param game game in which is computer playing
     * @param computerMoveCount how many should be played
     * @param secondsPerMove how long should delay be in seconds
     */
    public void delayComputerMoves(Game game, int computerMoveCount, int secondsPerMove){
        // set game moves with delay (for computer moves)
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(secondsPerMove), new EventHandler<>() {
            private int moves = 0;
            @Override
            public void handle(ActionEvent event) {
                clearBoard(game.board);
                if (moves == computerMoveCount) {
                    game.endTurn();
                } else {
                    game.playComputerMove(moves, computerMoveCount);
                }
                moves++;
            }
        }));
        timeline.setCycleCount(computerMoveCount + 1);
        timeline.play();
    }
}
