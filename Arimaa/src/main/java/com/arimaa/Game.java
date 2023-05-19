package com.arimaa;

import com.arimaa.pieces.Piece;
import com.arimaa.pieces.Rabbit;
import com.arimaa.pieces.SpecialPiece;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Game {

    private Player currentPlayer;
    private final Board board;
    private final GUI gui;
    private Tile chosenTile;
    private Set<Move> legalMoves;
    private Set<Tile> possibleTiles;
    private int moveCount;
    private final int maxMoves = 4;

    public Game(Board board, GUI gui) {
        this.board = board;
        this.gui = gui;
    }

    public void startAgainstPlayer(){
        generatePiecesPosition();
        setGame();
    }
    public void startAgainstComputer(){
        generatePiecesPosition();
    }
    private void setGame(){
        currentPlayer = Player.GOLD;
        moveCount = 0;
        setTiles();
        chosenTile = null;
        gui.setMoveCounter(moveCount);
        gui.setCurrentPlayer(currentPlayer);
    }

    public void endTurn(){
        if (moveCount > 0){
            currentPlayer = currentPlayer == Player.GOLD ? Player.SILVER : Player.GOLD;
            moveCount = 0;
            gui.setMoveCounter(moveCount);
            gui.setCurrentPlayer(currentPlayer);
        }
    }
    private void setTiles(){
        for (int i = 0; i < 8; ++i){
            for (int j = 0; j < 8; ++j){
                Tile tile = board.tiles[i][j];
                setTile(tile);
            }
        }
    }
    private void setTile(Tile tile){
        tile.tileSquare.setOnMouseClicked(e -> {
            gui.clearBoard(board);

            if (chosenTile != null && possibleTiles.contains(tile)){
                movePiece(chosenTile, tile);

                if (rabbitOnLastTile(tile.getPiece())){
                    gui.gameOverScreen(currentPlayer, this);
                }
                if (moveCount == maxMoves){
                    endTurn();
                }

                chosenTile = null;
            }
            else if (tile.isTileOccupied() && currentPlayer == tile.getPiece().getPiecePlayer()){
                chosenTile = tile;

                legalMoves = tile.getPiece().generateLegalMoves(board);
                possibleTiles = generatePossibleTiles(tile, legalMoves);

                gui.clearBoard(board);
                gui.setColorSelectedPiece(chosenTile.tileSquare);
                gui.drawLegalMoves(possibleTiles);
            }
            else {
                chosenTile = null;
            }
        });
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

    private boolean rabbitOnLastTile(Piece piece){
        boolean ret = false;
        if (piece.isRabbit && currentPlayer == piece.getPiecePlayer()){
            if ((piece.getPiecePlayer() == Player.GOLD && piece.piecePositionY == 0) ||
                    (piece.getPiecePlayer() == Player.SILVER && piece.piecePositionY == 7)){
                ret = true;
            }
        }
        return ret;
    }

    private void movePiece (Tile fromTile, Tile toTile){
        Piece piece = board.tiles[fromTile.tileCoordinateY][fromTile.tileCoordinateX].getPiece();

        piece.piecePositionX = toTile.tileCoordinateX;
        piece.piecePositionY = toTile.tileCoordinateY;

        board.tiles[toTile.tileCoordinateY][toTile.tileCoordinateX].setPiece(piece);
        board.tiles[fromTile.tileCoordinateY][fromTile.tileCoordinateX].setPiece(null);

        moveCount++;
        gui.setMoveCounter(moveCount);
    }

    public void generatePiecesPosition(){
        //String fileName = "map_2.txt";
        String fileName = "map_simple.txt";
        Path pathToProject = Paths.get("");
        Path filePath = Paths.get(pathToProject.toAbsolutePath() + "\\src\\main\\java\\com\\arimaa\\" + fileName);

        try {
            List<String> allLines = Files.readAllLines(filePath);

            int y = 0;
            for (String line : allLines) {
                for (int x = 0; x < 8; ++x){
                    char c = line.charAt(x);

                    Piece piece;
                    switch (c) {
                        case 'R' -> piece = new Rabbit(x, y, Player.SILVER);
                        case 'C' -> piece = new SpecialPiece.Cat(x, y, Player.SILVER);
                        case 'D' -> piece = new SpecialPiece.Dog(x, y, Player.SILVER);
                        case 'H' -> piece = new SpecialPiece.Horse(x, y, Player.SILVER);
                        case 'L' -> piece = new SpecialPiece.Camel(x, y, Player.SILVER);
                        case 'E' -> piece = new SpecialPiece.Elephant(x, y, Player.SILVER);
                        case 'r' -> piece = new Rabbit(x, y, Player.GOLD);
                        case 'c' -> piece = new SpecialPiece.Cat(x, y, Player.GOLD);
                        case 'd' -> piece = new SpecialPiece.Dog(x, y, Player.GOLD);
                        case 'h' -> piece = new SpecialPiece.Horse(x, y, Player.GOLD);
                        case 'l' -> piece = new SpecialPiece.Camel(x, y, Player.GOLD);
                        case 'e' -> piece = new SpecialPiece.Elephant(x, y, Player.GOLD);
                        default ->  piece = null;
                    }

                    Tile tile = board.tiles[y][x];
                    tile.setPiece(piece);
                }
                y++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
