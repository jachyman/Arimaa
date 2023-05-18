package com.example.demo;

import com.example.demo.board.Board;
import com.example.demo.board.Move;
import com.example.demo.board.Tile;
import com.example.demo.pieces.Piece;
import com.example.demo.pieces.Rabbit;
import com.example.demo.pieces.SpecialPiece;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {

    private final Color selectedPieceColor = Color.LIGHTBLUE;
    private final Color possibleMovesColor = Color.LIGHTGREEN;

    boolean whiteChoseAllPieces;

    Set<Move> legalMoves;
    Set<Tile> possibleTiles;

    Board board;
    GridPane gameGrid;
    Player currentPlayer;
    Tile chosenTile;

    public Game(Board board, GridPane gameGrid) {
        this.gameGrid = gameGrid;
        this.board = board;
        this.whiteChoseAllPieces = false;
        this.chosenTile = null;
        this.possibleTiles = new HashSet<>();
    }

    public void choosePiecesPosition(Player player){
        AtomicInteger chosenPiecesCount = new AtomicInteger();

        int fromRow, toRow;

        if (player == Player.WHITE) {
            fromRow = 6;
            toRow = 7;
        }
        else {
            fromRow = 0;
            toRow = 1;
        }

        for (int i = fromRow; i <= toRow; ++i){
            for (int j = 0; j < 8; ++j){
                Tile t = board.tiles[i][j];
                int finalI = i;
                int finalJ = j;

                t.tileSquare.setOnMouseClicked(e -> {
                    if (!t.isTileOccupied() && chosenPiecesCount.get() < 17 && (player == Player.WHITE || whiteChoseAllPieces)){
                        Piece piece = null;

                        if (chosenPiecesCount.get() < 8){
                            piece = new Rabbit(finalI, finalJ, player);
                        }
                        else if (chosenPiecesCount.get() < 10){
                            piece = new SpecialPiece.Cat(finalI, finalJ, player);
                        }
                        else if (chosenPiecesCount.get() < 12){
                            piece = new SpecialPiece.Dog(finalI, finalJ, player);
                        }
                        else if (chosenPiecesCount.get() < 14){
                            piece = new SpecialPiece.Horse(finalI, finalJ, player);
                        }
                        else if (chosenPiecesCount.get() < 15){
                            piece = new SpecialPiece.Camel(finalI, finalJ, player);
                        }
                        else if (chosenPiecesCount.get() < 16){
                            piece = new SpecialPiece.Elephant(finalI, finalJ, player);
                            if (player == Player.WHITE)
                                whiteChoseAllPieces = true;
                        }

                        t.setPiece(piece);
                        drawTile(t);

                        chosenPiecesCount.getAndIncrement();
                        board.print();
                    }
                });
            }
        }
    }

    public void generatePiecesPosition(){
        //String fileName = "map_2.txt";
        String fileName = "map_simple.txt";
        Path pathToProject = Paths.get("");
        Path filePath = Paths.get(pathToProject.toAbsolutePath() + "\\src\\main\\java\\com\\example\\demo\\" + fileName);
        System.out.println(filePath.toAbsolutePath());
        try {
            List<String> allLines = Files.readAllLines(filePath);

            int y = 0;
            for (String line : allLines) {
                for (int x = 0; x < 8; ++x){
                    char c = line.charAt(x);
                    if (c != 'O'){
                        Piece piece = null;
                        switch (c) {
                            case 'R' -> piece = new Rabbit(x, y, Player.BLACK);
                            case 'C' -> piece = new SpecialPiece.Cat(x, y, Player.BLACK);
                            case 'D' -> piece = new SpecialPiece.Dog(x, y, Player.BLACK);
                            case 'H' -> piece = new SpecialPiece.Horse(x, y, Player.BLACK);
                            case 'L' -> piece = new SpecialPiece.Camel(x, y, Player.BLACK);
                            case 'E' -> piece = new SpecialPiece.Elephant(x, y, Player.BLACK);
                            case 'r' -> piece = new Rabbit(x, y, Player.WHITE);
                            case 'c' -> piece = new SpecialPiece.Cat(x, y, Player.WHITE);
                            case 'd' -> piece = new SpecialPiece.Dog(x, y, Player.WHITE);
                            case 'h' -> piece = new SpecialPiece.Horse(x, y, Player.WHITE);
                            case 'l' -> piece = new SpecialPiece.Camel(x, y, Player.WHITE);
                            case 'e' -> piece = new SpecialPiece.Elephant(x, y, Player.WHITE);
                        }


                        Tile tile = board.tiles[y][x];
                        tile.setPiece(piece);
                        //drawTile(tile);
                    }
                }
                y++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        drawBoard();
    }
    public void play(){
        setGame();
    }

    private void setGame(){
        currentPlayer = Player.WHITE;
        for (int i = 0; i < 8; ++i){
            for (int j = 0; j < 8; ++j){
                Tile tile = board.tiles[i][j];
                setTile(tile);
            }
        }
    }

    private void setTile(Tile tile){
        tile.tileSquare.setOnMouseClicked(e -> {
            if (chosenTile != null && possibleTiles.contains(tile)){
                //tile.tileSquare.setFill(Color.YELLOW);
                movePiece(chosenTile, tile);
                currentPlayer = currentPlayer == Player.WHITE ? Player.BLACK : Player.WHITE;
            }
            else if (tile.isTileOccupied() && currentPlayer == tile.getPiece().getPiecePlayer()){
                chosenTile = tile;

                legalMoves = tile.getPiece().generateLegalMoves(board);
                possibleTiles = generatePossibleTiles(tile, legalMoves);

                board.clear();
                tile.tileSquare.setFill(selectedPieceColor);
                drawLegalMoves(possibleTiles);
            }
            else {
                chosenTile = null;
                board.clear();
            }

            board.print();
        });
    }

    private void drawBoard(){
        for (int y = 0; y < 8; ++y){
            for (int x = 0; x < 8; ++x){
                Tile tile = board.tiles[y][x];
                drawTile(tile);
            }
        }
    }
    private void drawTile(Tile tile){
        gameGrid.add(tile.pieceText, tile.tileCoordinateX, tile.tileCoordinateY);
    }

    private void movePiece (Tile fromTile, Tile toTile){
        Piece piece = board.tiles[fromTile.tileCoordinateY][fromTile.tileCoordinateX].getPiece();

        piece.piecePositionX = toTile.tileCoordinateX;
        piece.piecePositionY = toTile.tileCoordinateY;

        board.tiles[toTile.tileCoordinateY][toTile.tileCoordinateX].setPiece(piece);
        board.tiles[fromTile.tileCoordinateY][fromTile.tileCoordinateX].setPiece(null);
        board.clear();

        if (rabbitOnLastTile(piece)){
            gameOver(currentPlayer);
        }
    }

    private void restartGame(){

    }
    private void gameOver (Player winner){
        int width = 300;
        int height = 300;

        GridPane gameOverGrid = new GridPane();
        gameOverGrid.setPadding(new Insets(10, 10, 10, 10));

        Label gameOverLabel = new Label("GAME OVER");
        String winnerString = winner == Player.WHITE ? "WHITE" : "BLACK";
        Label winnerLabel = new Label("THE WINNER IS " + winnerString);

        GridPane.setConstraints(gameOverLabel, 0, 0);
        GridPane.setConstraints(winnerLabel, 0, 3);

        gameOverGrid.getChildren().addAll(gameOverLabel, winnerLabel);
        gameOverGrid.setAlignment(Pos.CENTER);

        Scene gameOverScene = new Scene(gameOverGrid, width, height);
        Stage gameOverStage = new Stage();
        gameOverStage.setScene(gameOverScene);
        gameOverStage.setTitle("Game over");

        gameOverStage.show();
    }

    private boolean rabbitOnLastTile(Piece piece){
        boolean ret = false;
        if (piece.isRabbit && currentPlayer == piece.getPiecePlayer()){
            if ((piece.getPiecePlayer() == Player.WHITE && piece.piecePositionY == 0) ||
                (piece.getPiecePlayer() == Player.BLACK && piece.piecePositionY == 7)){
                ret = true;
            }
        }
        return ret;
    }
    private Set<Tile> generatePossibleTiles(Tile startTile, Set<Move> legalMoves){
        Set<Tile> possibleTiles = new HashSet<>();

        int x = startTile.tileCoordinateX;
        int y = startTile.tileCoordinateY;
        if (legalMoves.contains(Move.UP)){
            possibleTiles.add(board.tiles[y-1][x]);
        }
        if (legalMoves.contains(Move.DOWN)){
            possibleTiles.add(board.tiles[y+1][x]);
        }
        if (legalMoves.contains(Move.LEFT)){
            possibleTiles.add(board.tiles[y][x-1]);
        }
        if (legalMoves.contains(Move.RIGHT)){
            possibleTiles.add(board.tiles[y][x+1]);
        }

        return possibleTiles;
    }
    private void drawLegalMoves(Set<Tile> possibleTiles){

        for (Tile possibleTile : possibleTiles) {
            possibleTile.tileSquare.setFill(possibleMovesColor);
        }
    }
}
