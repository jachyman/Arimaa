package com.arimaa;

import com.arimaa.pieces.Piece;
import com.arimaa.pieces.Rabbit;
import com.arimaa.pieces.SpecialPiece;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Game {

    private Player currentPlayer;
    private final Board board;
    private final GUI gui;

    public GameType gameType;
    private Tile chosenTile;

    private List<Piece> computerPieces;
    private Set<Piece> goldRabbitPieces;
    private Set<Piece> silverRabbitPieces;

    private Set<Move> legalMoves;
    private List<Tile> possibleTiles;

    private boolean gameIsOver;
    public int moveCount;
    private final int maxMoves = 4;

    public Game(Board board, GUI gui) {
        this.board = board;
        this.gui = gui;
        computerPieces = new ArrayList<>();
    }

    public void startVersusPlayer(){
        gameType = GameType.versusPlayer;
        setGame();
        generatePiecesPosition();
        setTiles();
    }
    public void startVersusComputer(){
        gameType = GameType.versusComputer;
        setGame();
        generatePiecesPosition();
        setTiles();
    }
    private void setGame(){
        gui.createGameScene(board, this);

        currentPlayer = Player.GOLD;
        moveCount = 0;
        chosenTile = null;
        gameIsOver = false;

        goldRabbitPieces = new HashSet<>();
        silverRabbitPieces = new HashSet<>();

        gui.setMoveCounter(moveCount);
        gui.setCurrentPlayer(currentPlayer);
        gui.goldPlayerStopwatch.start();
    }

    public void endTurn(){
        Player winner = checkGameOver();
        if (gameIsOver){
            gui.gameOverScreen(winner, this);
        }
        else {
            switchStopwatch();
            currentPlayer = oppositePlayer(currentPlayer);
            moveCount = 0;
            gui.setMoveCounter(moveCount);
            gui.setCurrentPlayer(currentPlayer);

            if (gameType == GameType.versusComputer && currentPlayer == Player.SILVER) {
                playComputerTurn();
            }
        }
    }

    private void switchStopwatch(){
        if (currentPlayer == Player.GOLD) {
            gui.goldPlayerStopwatch.pause();
            if (gameType == GameType.versusPlayer){
                gui.silverPlayerStopwatch.resume();
            }
        } else {
            if (gameType == GameType.versusPlayer){
                gui.silverPlayerStopwatch.pause();
            }
            gui.goldPlayerStopwatch.resume();
        }
    }

    private void playComputerTurn(){
        //int computerMoveCount = ThreadLocalRandom.current().nextInt(1, maxMoves + 1);
        int computerMoveCount = 3;
        for (int i = 0; i < computerMoveCount; ++i){
            playComputerMove();
            try {
                //TimeUnit.SECONDS.sleep(1);
                Thread.sleep(1000);
            } catch (InterruptedException ie){
                System.out.println("Interrupted while Sleeping");
            }
            board.print();
            //gui.clearBoard(board);
        }

        //gui.clearBoard(board);
        endTurn();
    }

    private boolean playComputerMove(){
        boolean pieceMoved = false;

        Collections.shuffle(computerPieces);
        for (Piece piece : computerPieces){
            legalMoves = piece.generateLegalMoves(board);
            if (!legalMoves.isEmpty()){
                //System.out.println("X " + piece.piecePositionX + " Y " + piece.piecePositionY);
                Tile fromTile = board.tiles[piece.piecePositionY][piece.piecePositionX];
                possibleTiles = generatePossibleTiles(fromTile, legalMoves);

                Random rand = new Random();
                Tile toTile = possibleTiles.get(rand.nextInt(possibleTiles.size()));

                //gui.setColorComputerFromMove(fromTile.tileSquare);
                //gui.setColorComputerToMove(toTile.tileSquare);

                movePiece(fromTile, toTile);
                pieceMoved = true;
                break;
            }
        }

        return pieceMoved;
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

            if (!gameIsOver && (gameType == GameType.versusPlayer || currentPlayer == Player.GOLD)) {
                gui.clearBoard(board);

                if (chosenTile != null && possibleTiles.contains(tile)) {
                    movePiece(chosenTile, tile);

                    Tile adjacentTileTrapWithFriend = null;
                    if (shouldRemovePieceOnTile(tile)){
                        removePieceOnTile(tile);
                    } else {
                        adjacentTileTrapWithFriend = findAdjacentTileTrapWithFriend(chosenTile, currentPlayer);
                    }

                    if (adjacentTileTrapWithFriend != null && shouldRemovePieceOnTile(adjacentTileTrapWithFriend)){
                        removePieceOnTile(adjacentTileTrapWithFriend);
                    }

                    moveCount++;
                    gui.setMoveCounter(moveCount);
                    if (moveCount == maxMoves) {
                        endTurn();
                    }

                    chosenTile = null;
                } else if (tile.isTileOccupied() && currentPlayer == tile.getPiece().getPiecePlayer() && !isPieceFrozen(tile)) {
                    chosenTile = tile;

                    legalMoves = tile.getPiece().generateLegalMoves(board);
                    possibleTiles = generatePossibleTiles(tile, legalMoves);

                    //gui.clearBoard(board);
                    gui.setColorSelectedPiece(chosenTile.tileSquare);
                    gui.drawLegalMoves(possibleTiles);
                } else {
                    chosenTile = null;
                }
            }
        });
    }

    private boolean shouldRemovePieceOnTile(Tile tile){
        boolean shouldRemove = false;
        if (tile.isTrap){
            shouldRemove = true;
            for (Tile adjacentTile : tile.adjacentTiles(board)){
                if (isSamePiecePlayerOnTiles(tile, adjacentTile)){
                    shouldRemove = false;
                }
            }
        }

        return shouldRemove;
    }

    private Tile findAdjacentTileTrapWithFriend(Tile tile, Player player){
        Tile tileTrapWithFriend = null;
        for (Tile adjacentTile : tile.adjacentTiles(board)){
            if (adjacentTile.isTrap && adjacentTile.isTileOccupied() && adjacentTile.getPiece().getPiecePlayer() == player){
                tileTrapWithFriend = adjacentTile;
                break;
            }
        }

        return tileTrapWithFriend;
    }

    private void removePieceOnTile(Tile tile){
        Piece piece = tile.getPiece();

        goldRabbitPieces.remove(piece);
        silverRabbitPieces.remove(piece);
        computerPieces.remove(piece);

        tile.removePiece();
    }
    private boolean isPieceFrozen(Tile tile){
        List<Tile> adjacentTiles = tile.adjacentTiles(board);
        boolean isFrozen = false;

        Piece piece = tile.getPiece();
        Piece adjacentPiece;

        for (Tile adjecantTile : adjacentTiles){
            if (adjecantTile.isTileOccupied()){
                adjacentPiece = adjecantTile.getPiece();
                if (piece.getPiecePlayer() != adjacentPiece.getPiecePlayer() && adjacentPiece.pieceStrength > piece.pieceStrength){
                    isFrozen = true;
                    break;
                }
            }
        }
        for (Tile adjecantTile : adjacentTiles){
            if (isSamePiecePlayerOnTiles(tile, adjecantTile)){
                isFrozen = false;
                break;
            }
        }

        return isFrozen;
    }

    private boolean isSamePiecePlayerOnTiles(Tile tile1, Tile tile2){
        return tile1.isTileOccupied() && tile2.isTileOccupied() && tile1.getPiece().getPiecePlayer() == tile2.getPiece().getPiecePlayer();
    }

    private Player checkGameOver(){
        Player winner = null;
        Player oppositePlayer = oppositePlayer(currentPlayer);

        Set<Piece> currentPlayerRabbit = currentPlayer == Player.GOLD ? goldRabbitPieces : silverRabbitPieces;
        Set<Piece> oppositePlayerRabbit = oppositePlayer == Player.GOLD ? goldRabbitPieces : silverRabbitPieces;

        if (rabbitReachedGoal(currentPlayer)){
            winner = currentPlayer;
        } else if (rabbitReachedGoal(oppositePlayer)){
            winner = oppositePlayer;
        } else if (oppositePlayerRabbit.size() == 0) {
            winner = currentPlayer;
        } else if (currentPlayerRabbit.size() == 0) {
            winner = oppositePlayer;
        }

        if (winner != null){
            gameIsOver = true;
        }
        return winner;
    }

    private boolean rabbitReachedGoal(Player player){
        boolean ret = false;

        int goalPositionY = player == Player.GOLD ? 0 : 7;
        Set<Piece> rabbitPieces = player == Player.GOLD ? goldRabbitPieces : silverRabbitPieces;

        for (Piece piece : rabbitPieces){
            if (piece.piecePositionY == goalPositionY){
                ret = true;
                break;
            }
        }

        return ret;
    }

    private List<Tile> generatePossibleTiles(Tile startTile, Set<Move> legalMoves){
        List<Tile> possibleTiles = new ArrayList<>();

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

    private void movePiece (Tile fromTile, Tile toTile){
        Piece piece = board.tiles[fromTile.tileCoordinateY][fromTile.tileCoordinateX].getPiece();

        piece.piecePositionX = toTile.tileCoordinateX;
        piece.piecePositionY = toTile.tileCoordinateY;

        board.tiles[toTile.tileCoordinateY][toTile.tileCoordinateX].setPiece(piece);
        board.tiles[fromTile.tileCoordinateY][fromTile.tileCoordinateX].setPiece(null);
    }

    private Player oppositePlayer(Player player){
        return player == Player.GOLD ? Player.SILVER : Player.GOLD;
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
                        case 'R' -> {
                            piece = new Rabbit(x, y, Player.SILVER);
                            silverRabbitPieces.add(piece);
                        }
                        case 'C' -> piece = new SpecialPiece.Cat(x, y, Player.SILVER);
                        case 'D' -> piece = new SpecialPiece.Dog(x, y, Player.SILVER);
                        case 'H' -> piece = new SpecialPiece.Horse(x, y, Player.SILVER);
                        case 'L' -> piece = new SpecialPiece.Camel(x, y, Player.SILVER);
                        case 'E' -> piece = new SpecialPiece.Elephant(x, y, Player.SILVER);
                        case 'r' -> {
                            piece = new Rabbit(x, y, Player.GOLD);
                            goldRabbitPieces.add(piece);
                        }
                        case 'c' -> piece = new SpecialPiece.Cat(x, y, Player.GOLD);
                        case 'd' -> piece = new SpecialPiece.Dog(x, y, Player.GOLD);
                        case 'h' -> piece = new SpecialPiece.Horse(x, y, Player.GOLD);
                        case 'l' -> piece = new SpecialPiece.Camel(x, y, Player.GOLD);
                        case 'e' -> piece = new SpecialPiece.Elephant(x, y, Player.GOLD);
                        default ->  piece = null;
                    }

                    Tile tile = board.tiles[y][x];
                    tile.setPiece(piece);

                    if (gameType == GameType.versusComputer && piece != null && piece.getPiecePlayer() == Player.SILVER){
                        computerPieces.add(piece);
                        //System.out.println("COM PIECE ADDED");
                    }
                }
                y++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
