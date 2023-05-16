package com.example.demo;

import com.example.demo.board.Board;
import com.example.demo.board.Move;
import com.example.demo.board.Tile;
import com.example.demo.pieces.Piece;
import com.example.demo.pieces.Rabbit;
import com.example.demo.pieces.SpecialPiece;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {

    Board board;
    GridPane gameGrid;
    boolean whiteChoseAllPieces;
    Player currentPlayer;

    public Game(Board board, GridPane gameGrid) {
        this.gameGrid = gameGrid;
        this.board = board;
        this.whiteChoseAllPieces = false;
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

                        drawPiece(finalJ, finalI, piece);

                        chosenPiecesCount.getAndIncrement();
                        board.print();
                    }
                });
            }
        }
    }

    public void generatePiecesPosition(){
        String fileName = "map_2.txt";
        try {
            List<String> allLines = Files.readAllLines(Paths.get("C:\\SKOLA\\JAVA\\smestalka\\test_1\\demo\\src\\main\\java\\com\\example\\demo\\" + fileName));

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

                        board.tiles[y][x].setPiece(piece);
                        drawPiece(x, y, piece);
                    }
                }
                y++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            if (tile.isTileOccupied() && currentPlayer == tile.getPiece().getPiecePlayer()){
                //t.tileSquare.setFill(Color.RED);
                Set<Move> moves = tile.getPiece().legalMoves(board);
                board.clear();
                drawLegalMoves(tile, moves);
                currentPlayer = currentPlayer == Player.WHITE ? Player.BLACK : Player.WHITE;
            }
        });
    }

    private void drawLegalMoves(Tile tile, Set<Move> legalMoves){
        int x = tile.tileCoordinateX;
        int y = tile.tileCoordinateY;
        if (legalMoves.contains(Move.UP)){
            board.tiles[y-1][x].tileSquare.setFill(Color.GREEN);
        }
        if (legalMoves.contains(Move.DOWN)){
            board.tiles[y+1][x].tileSquare.setFill(Color.GREEN);
        }
        if (legalMoves.contains(Move.LEFT)){
            board.tiles[y][x-1].tileSquare.setFill(Color.GREEN);
        }
        if (legalMoves.contains(Move.RIGHT)){
            board.tiles[y][x+1].tileSquare.setFill(Color.GREEN);
        }

    }
    private void drawPiece(int x, int y, Piece piece){
        String color = piece.getPiecePlayer() == Player.WHITE ? "W" : "B";
        String pieceString = String.valueOf(piece.c) + "-" + color;
        Text pieceText = new Text(pieceString);
        gameGrid.add(pieceText, x, y);
    }
}
