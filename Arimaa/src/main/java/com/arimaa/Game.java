package com.arimaa;

import com.arimaa.pieces.Piece;
import com.arimaa.pieces.Rabbit;
import com.arimaa.pieces.SpecialPiece;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Game {

    private Player currentPlayer;
    public final Board board;
    public final GUI gui;

    public GameType gameType;
    private Tile selectedTile;

    private List<Piece> computerPieces;
    private Set<Piece> goldRabbitPieces;
    private Set<Piece> silverRabbitPieces;


    private Set<Tile> movementTiles;
    private Set<Tile> pushFromTiles;
    private Set<Tile> pushToTiles;
    private Set<Tile> pullFromTiles;
    private Tile pullToTile;
    private Tile pushFromTile;
    private Tile finishPushTile;
    private Tile pushingTile;

    public boolean setupPhase;
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
    }
    public void startVersusComputer(){
        gameType = GameType.versusComputer;
        setGame();
    }
    private void setGame(){
        gui.createGameScene(board, this);

        currentPlayer = Player.GOLD;
        moveCount = 0;
        selectedTile = null;
        gameIsOver = false;
        setupPhase = true;

        goldRabbitPieces = new HashSet<>();
        silverRabbitPieces = new HashSet<>();

        setTileSets();
        generatePiecesPosition();

        gui.setMoveCounter(moveCount);
        gui.setCurrentPlayer(currentPlayer);
        gui.goldPlayerStopwatch.start();

        changeStartPosition(currentPlayer);
    }
    private void setTileSets(){
        movementTiles = new HashSet<>();
        pushFromTiles = new HashSet<>();
        pushToTiles = new HashSet<>();
        pullFromTiles = new HashSet<>();
        pullToTile = null;
        finishPushTile = null;
        pushingTile = null;
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
                drawPullTiles();

                if (!isPushFinished()){
                    gui.fillTile(pushingTile, gui.pushingTileColor);
                    gui.fillTile(finishPushTile, gui.finishPushTileColor);
                    if (tile == finishPushTile){
                        movePiece(pushingTile, finishPushTile);
                        finishPushTile = null;
                        increaseMoveCount();
                        gui.clearBoard(board);
                    }
                } else if (selectedTile != null && movementTiles.contains(tile)) {
                    movement(selectedTile, tile);
                    handleTraps(selectedTile, tile);

                    increaseMoveCount();
                    gui.clearBoard(board);
                    drawPullTiles();

                    selectedTile = null;
                } else if (canBeMoved(tile)) {
                    selectedTile = tile;

                    if (moveCount < 3){
                        pushingTile = tile;
                        generatePushFromTiles(tile);
                        gui.fillTiles(pushFromTiles, gui.pushFromColor);
                    }
                    generateMovementTiles(tile);

                    gui.fillTile(selectedTile, gui.selectedTileColor);
                    gui.fillTiles(movementTiles, gui.movemetToColor);
                } else if (canBePulled(tile)){
                    pullPiece(tile, pullToTile);
                    increaseMoveCount();
                } else if (canBePushed(tile)){
                    generatePushToTiles(tile);
                    pushFromTile = tile;
                    gui.fillTiles(pushToTiles, gui.pushToTileColor);
                } else if (pushFromTile != null && pushToTiles.contains(tile)){
                    pushPiece(pushFromTile, tile);
                    finishPushTile = pushFromTile;
                    gui.fillTile(pushingTile, gui.pushingTileColor);
                    gui.fillTile(finishPushTile, gui.finishPushTileColor);

                    increaseMoveCount();
                }
                else {
                    selectedTile = null;
                }

                if (moveCount == maxMoves) {
                    endTurn();
                }
            }
        });
    }

    public void endTurn(){
        if (setupPhase) {
            gui.clearBoard(board);
            if  (gameType == GameType.versusPlayer) {
                currentPlayer = oppositePlayer(currentPlayer);
                changeStartPosition(currentPlayer);
            }
            if (currentPlayer == Player.GOLD){
                setTiles();
                setupPhase = false;
            }
        } else {
            Player winner = checkGameOver();
            if (gameIsOver){
                gui.goldPlayerStopwatch.pause();
                if (gameType == GameType.versusPlayer){
                    gui.silverPlayerStopwatch.pause();
                }
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
            setTileSets();
            gui.clearBoard(board);
        }
    }

    private void movePiece (Tile fromTile, Tile toTile){
        Piece piece = board.tiles[fromTile.tileCoordinateY][fromTile.tileCoordinateX].getPiece();

        piece.piecePositionX = toTile.tileCoordinateX;
        piece.piecePositionY = toTile.tileCoordinateY;

        board.tiles[toTile.tileCoordinateY][toTile.tileCoordinateX].setPiece(piece);
        board.tiles[fromTile.tileCoordinateY][fromTile.tileCoordinateX].setPiece(null);
    }
    private void movement(Tile fromTile, Tile toTile){
        pullToTile = fromTile;
        generatePullFromTiles(fromTile);
        gui.fillTiles(pullFromTiles, gui.pullFromColor);
        gui.fillTile(fromTile, gui.previousPiecePositonColor);
        movePiece(fromTile, toTile);
    }
    private void pullPiece(Tile fromTile, Tile toTile){
        movePiece(fromTile, toTile);
        handleTraps(fromTile, toTile);
        pullToTile = null;
        pullFromTiles.clear();
        gui.clearBoard(board);
    }
    private void pushPiece(Tile fromTile, Tile toTile){
        movePiece(fromTile, toTile);
        handleTraps(fromTile, toTile);
        finishPushTile = fromTile;
        pushToTiles.clear();
        gui.clearBoard(board);
    }


    private void generateMovementTiles(Tile tile){
        movementTiles = tile.getPiece().getLegalMovementTiles(board);
    }
    private void generatePullFromTiles(Tile tile){
        Piece piece, adjacentPiece;
        piece = tile.getPiece();
        pullFromTiles.clear();
        for (Tile adjacentOccupiedTile : tile.adjacentOccupiedTiles(board)){
            adjacentPiece = adjacentOccupiedTile.getPiece();
            if (piece.getPiecePlayer() != adjacentPiece.getPiecePlayer() && piece.pieceStrength > adjacentPiece.pieceStrength){
                pullFromTiles.add(adjacentOccupiedTile);
            }
        }
    }
    private void generatePushFromTiles(Tile tile){
        Piece piece, adjacentPiece;
        piece = tile.getPiece();
        pushFromTiles.clear();
        for (Tile adjacentOccupiedTile : tile.adjacentOccupiedTiles(board)){
            adjacentPiece = adjacentOccupiedTile.getPiece();
            if (piece.getPiecePlayer() != adjacentPiece.getPiecePlayer() && piece.pieceStrength > adjacentPiece.pieceStrength){
                pushFromTiles.add(adjacentOccupiedTile);
            }
        }
    }
    private void generatePushToTiles(Tile tile){
        pushToTiles.clear();
        pushToTiles.addAll(tile.adjacentFreeTiles(board));
    }

    private void handleTraps(Tile fromTile, Tile toTile){
        Tile adjacentTileTrapWithFriend = null;
        if (shouldRemovePieceOnTile(toTile)){
            removePieceOnTile(toTile);
        } else {
            adjacentTileTrapWithFriend = findAdjacentTileTrapWithFriend(fromTile, toTile.getPiece().getPiecePlayer());
        }

        if (adjacentTileTrapWithFriend != null && shouldRemovePieceOnTile(adjacentTileTrapWithFriend)){
            removePieceOnTile(adjacentTileTrapWithFriend);
        }
    }
    private Tile findAdjacentTileTrapWithFriend(Tile tile, Player player){
        Tile tileTrapWithFriend = null;
        //for (Tile adjacentTile : tile.adjacentTiles(board)){
        for (Tile adjacentTile : tile.adjacentOccupiedTiles(board)){
            if (adjacentTile.isTrap && adjacentTile.getPiece().getPiecePlayer() == player){
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
        int secondsPerMove = 2;
        int computerMoveCount = ThreadLocalRandom.current().nextInt(1, 5);
        System.out.println(computerMoveCount);
        gui.delayComputerMoves(this, computerMoveCount, secondsPerMove);
    }


    public boolean playComputerMove(){
        boolean pieceMoved = false;

        Collections.shuffle(computerPieces);
        for (Piece piece : computerPieces){
            movementTiles = piece.getLegalMovementTiles(board);
            if (!movementTiles.isEmpty()){
                Tile fromTile = board.tiles[piece.piecePositionY][piece.piecePositionX];

                Tile toTile = randomTile(movementTiles);

                movePiece(fromTile, toTile);
                handleTraps(fromTile, toTile);
                gui.fillTile(fromTile, gui.selectedTileColor);
                gui.fillTile(toTile, gui.movemetToColor);
                pieceMoved = true;
                break;
            }
        }

        return pieceMoved;
    }

    private Tile randomTile(Set<Tile> tiles){
        int size = tiles.size();
        int random = new Random().nextInt(size);
        int i = 0;
        Tile randomTile = null;
        for (Tile tile : tiles){
            if (i == random){
                randomTile = tile;
                break;
            }
            i++;
        }

        return randomTile;
    }

    private boolean isPushFinished(){
        return finishPushTile == null;
    }

    private void increaseMoveCount(){
        moveCount++;
        gui.setMoveCounter(moveCount);
    }
    private void drawPullTiles(){
        if (pullToTile != null){
            gui.fillTile(pullToTile, gui.pullToTileColor);
        }
        gui.fillTiles(pullFromTiles, gui.pullFromColor);
    }


    private boolean canBeMoved(Tile tile){
        return tile.isTileOccupied() && currentPlayer == tile.getPiece().getPiecePlayer() && !isPieceFrozen(tile);
    }
    private boolean canBePulled(Tile tile){
        return pullToTile != null && pullFromTiles.contains(tile);
    }
    private boolean canBePushed(Tile tile){
        return moveCount < 3 && pushFromTiles.contains(tile);
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

    private boolean isPieceFrozen(Tile tile){
        List<Tile> adjacentOccupiedTiles = tile.adjacentOccupiedTiles(board);
        boolean isFrozen = false;

        Piece piece = tile.getPiece();
        Piece adjacentPiece;

        for (Tile adjecantTile : adjacentOccupiedTiles){
            adjacentPiece = adjecantTile.getPiece();
            if (piece.getPiecePlayer() != adjacentPiece.getPiecePlayer() && adjacentPiece.pieceStrength > piece.pieceStrength){
                isFrozen = true;
                break;
            }
        }
        for (Tile adjecantTile : adjacentOccupiedTiles){
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

    private Player oppositePlayer(Player player){
        return player == Player.GOLD ? Player.SILVER : Player.GOLD;
    }

    public void changeStartPosition(Player player){
        int startRow = player == Player.GOLD ? 6 : 0;
        int endRow = player == Player.GOLD ? 7 : 1;
        selectedTile = null;
        Tile tile;
        for (int y = 0; y < 8; ++y){
            for (int x = 0; x < 8; ++x){
                tile = board.tiles[y][x];
                if (y >= startRow && y <= endRow){
                    setChangeStartPositionTile(tile);
                }
                else {
                    tile.tileSquare.setOnMouseClicked(null);
                }
            }
        }
    }

    private void setChangeStartPositionTile(Tile tile) {
        tile.tileSquare.setOnMouseClicked(e -> {
            if (selectedTile == null){
                gui.clearBoard(board);
                selectedTile = tile;
                gui.fillTile(tile, gui.selectedTileColor);
            } else{
                gui.fillTile(tile, gui.selectedTileColor);
                switchPiecesOnTiles(tile, selectedTile);
                selectedTile = null;
            }
        });
    }

    private void switchPiecesOnTiles(Tile tile1, Tile tile2) {
        Piece tmpPiece = tile1.piece;
        movePiece(tile2, tile1);
        tile2.setPiece(tmpPiece);
    }

    public void generatePiecesPosition(){
        //String fileName = "map_2.txt";
        //String fileName = "map_simple.txt";
        String fileName = "starting_piece_position.txt";
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
                    }
                }
                y++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
