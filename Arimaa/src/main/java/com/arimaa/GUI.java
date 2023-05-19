package com.arimaa;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.Set;

public class GUI {

    private GridPane menuGrid, gameGrid;
    private Scene menuScene, gameScene;
    private Label moveCountLabel, currentPlayerLabel;

    private final int mainStageWidth = 800;
    private final int mainStageHeight = 800;
    private final double tileSquareSize = 70;
    private final int gameOverStageWidth = 300;
    private final int gameOverStageHeight = 300;

    private final Color selectedPieceColor = Color.LIGHTBLUE;
    private final Color legalMovesColor = Color.LIGHTGREEN;

    Stage mainStage;
    public GUI(Stage mainStage) {
        this.mainStage = mainStage;
        menuGrid = new GridPane();
        gameGrid = new GridPane();
    }

    public void setStageMenu(){
        mainStage.setScene(menuScene);
        mainStage.setTitle("Arimaa");
    }

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

    public void createMenuScene(Game game){
        Button startButton;
        Label menuLabel;

        menuLabel = new Label("Menu");

        startButton = new Button("Player VS Player");
        startButton.setOnAction(e -> {
            game.startAgainstPlayer();
            mainStage.setScene(gameScene);
        });

        GridPane.setConstraints(startButton, 0, 5);
        GridPane.setConstraints(menuLabel, 0, 0);

        menuGrid.getChildren().addAll(menuLabel, startButton);
        menuGrid.setAlignment(Pos.CENTER);

        menuScene = new Scene(menuGrid, mainStageWidth, mainStageHeight);
    }

    public void createGameScene(Board board, Game game){
        Button backButton, endTurnButton;

        backButton = new Button("Back to menu");
        backButton.setOnAction(e -> mainStage.setScene(menuScene));

        endTurnButton = new Button("End turn");
        endTurnButton.setOnAction(e -> {
            game.endTurn();
        });

        moveCountLabel = new Label();
        currentPlayerLabel = new Label();

        GridPane.setConstraints(backButton, 10 , 0);
        GridPane.setConstraints(endTurnButton, 10 , 1);
        GridPane.setConstraints(moveCountLabel, 10 , 2);
        GridPane.setConstraints(currentPlayerLabel, 10 , 3);

        setGameGrid();

        setTileSquares(board);
        clearBoard(board);
        setPieces(board);

        gameGrid.getChildren().addAll(backButton, endTurnButton, moveCountLabel, currentPlayerLabel);
        gameScene = new Scene(gameGrid, mainStageWidth, mainStageHeight);
    }

    public void setMoveCounter(int moveCount){
        moveCountLabel.setText(Integer.toString(moveCount));
    }
    public void setCurrentPlayer(Player currentPlayer){
        currentPlayerLabel.setText(currentPlayer == Player.GOLD ? "Gold" : "Silver");
    }

    private void setGameGrid(){
        gameGrid.setPadding(new Insets(10, 10, 10,10));
        gameGrid.setVgap(8);
        gameGrid.setHgap(10);
    }

    private void setTileSquares(Board board){
        for (int y = 0; y < 8; ++y){
            for (int x = 0; x < 8; ++x){
                Rectangle tileSquare = new Rectangle(tileSquareSize, tileSquareSize);
                board.tiles[y][x].tileSquare = tileSquare;
                gameGrid.add(tileSquare, x, y);
            }
        }
    }

    public void setColorLegalMove(Rectangle tileSquare){
        tileSquare.setFill(legalMovesColor);
    }
    public void drawLegalMoves(Set<Tile> legalMoves){
        for (Tile legalMove : legalMoves){
            setColorLegalMove(legalMove.tileSquare);
        }
    }
    public void setColorSelectedPiece(Rectangle tileSquare){
        tileSquare.setFill(selectedPieceColor);
    }
    public void clearBoard(Board board){
        Rectangle tileSquare;
        for (int i = 0; i < 8; ++i){
            for (int j = 0; j < 8; ++j){
                tileSquare = board.tiles[i][j].tileSquare;
                tileSquare.setFill(Color.WHITE);
                if (board.getTile(i, j).isTrap){
                    tileSquare.setFill(Color.LIGHTGRAY);
                }
            }
        }
    }

    public void gameOverScreen(Player winner, Game game){
        GridPane gameOverGrid = new GridPane();
        Stage gameOverStage = new Stage();

        Label gameOverLabel = new Label("GAME OVER");
        String winnerString = winner == Player.GOLD ? "GOLD" : "SILVER";
        Label winnerLabel = new Label("WINNER IS " + winnerString);

        Button restartButton = new Button("Player VS Player");
        restartButton.setOnAction(e ->{
            game.startAgainstPlayer();
            gameOverStage.hide();
        });

        Button quitButton = new Button("QUIT");
        quitButton.setOnAction(e ->{
            gameOverStage.close();
            mainStage.close();
        });

        GridPane.setConstraints(gameOverLabel, 0, 0);
        GridPane.setConstraints(winnerLabel, 0, 3);
        GridPane.setConstraints(restartButton, 0, 4);
        GridPane.setConstraints(quitButton, 0, 5);

        gameOverGrid.getChildren().addAll(gameOverLabel, winnerLabel, restartButton, quitButton);
        gameOverGrid.setAlignment(Pos.CENTER);

        Scene gameOverScene = new Scene(gameOverGrid, gameOverStageWidth, gameOverStageHeight);
        gameOverStage.setScene(gameOverScene);
        gameOverStage.setTitle("Game over");

        gameOverStage.show();
    }

    public void showStage(){
        mainStage.show();
    }
}
