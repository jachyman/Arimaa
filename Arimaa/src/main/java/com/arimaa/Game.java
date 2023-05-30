package com.arimaa;

import com.arimaa.pieces_src.Piece;
import com.arimaa.pieces_src.Rabbit;
import com.arimaa.pieces_src.SpecialPiece;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.*;
public class Game {

    private Player currentPlayer;
    public final Board board;
    public final GUI gui;

    public GameType gameType;
    private Tile selectedTile;
    private Board lastBoard;

    private List<Piece> goldRabbitPieces;
    private List<Piece> silverRabbitPieces;
    private List<Piece> goldPieces;
    private List<Piece> silverPieces;

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

    private static final String startPiecePositionFileName = "starting_piece_position.txt";
    private static final Logger logger = Logger.getLogger(Game.class.getName());
    Handler stdout;

    public Game(Board board, GUI gui) {
        this.board = board;
        this.gui = gui;
        logger.setLevel(Level.ALL);
    }

    private void setLogger() {
        logger.setLevel(Level.ALL);

        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);

        logger.addHandler(handler);
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
        lastBoard = new Board();

        goldRabbitPieces = new ArrayList<>();
        silverRabbitPieces = new ArrayList<>();
        goldPieces = new ArrayList<>();
        silverPieces = new ArrayList<>();

        setTileSets();
        generatePiecesPosition(startPiecePositionFileName);
        setLogger();

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

        // set Tile to react correctly when clicked

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
                    // if player clicked on tile where Piece on selected tile can move to, move it there
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
                        pushFromTiles = generatePushFromTiles(tile);
                        gui.fillTiles(pushFromTiles, gui.pushFromColor);
                    }
                    generateMovementTiles(tile);

                    gui.fillTile(selectedTile, gui.selectedTileColor);
                    gui.fillTiles(movementTiles, gui.movemetToColor);
                } else if (canBePulled(tile)){
                    pullPiece(tile, pullToTile);
                    increaseMoveCount();
                } else if (canBePushed(tile)){
                    generatePushToTiles(tile); // find all pieces that can be pushed by piece on selected tile
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
        switchStopwatch();
        if (setupPhase) {
            // in setup phase, end turn changes current player or ends setup phase
            gui.clearBoard(board);
            if  (gameType == GameType.versusPlayer) {
                currentPlayer = oppositePlayer(currentPlayer);
                changeStartPosition(currentPlayer);
            }
            if (currentPlayer == Player.GOLD){
                copyToLastBoard();
                setTiles();
                setupPhase = false;
            }
        } else if (boardChanged()) {
            copyToLastBoard();
            Player winner = checkGameOver();
            if (gameIsOver){
                gui.goldPlayerStopwatch.stop();
                if (gameType == GameType.versusPlayer){
                    gui.silverPlayerStopwatch.stop();
                }
                gui.gameOverScreen(winner, this);
            }
            else {
                // if game is not over
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
        } else {
            // board has not been changed
            logger.info("Board was not changed");
            moveCount = 0;
            gui.setMoveCounter(moveCount);
        }
    }

    private void copyToLastBoard() {
        for (int y = 0; y < 8; ++y){
            for (int x = 0; x < 8; ++x){
                Tile t = new Tile(board.tiles[y][x]);
                t.setPiece(board.tiles[y][x].getPiece());
                lastBoard.tiles[y][x] = t;
            }
        }
    }

    public void movePiece (Tile fromTile, Tile toTile){
        com.arimaa.pieces_src.Piece piece = board.tiles[fromTile.tileCoordinateY][fromTile.tileCoordinateX].getPiece();

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
        // find all pieces on tiles, that can be pulled from tile
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
    private Set<Tile> generatePushFromTiles(Tile tile){
        // find all pieces on tiles, that can be pushed
        Piece piece, adjacentPiece;
        piece = tile.getPiece();
        Set<Tile> tiles = new HashSet<>();
        for (Tile adjacentOccupiedTile : tile.adjacentOccupiedTiles(board)){
            adjacentPiece = adjacentOccupiedTile.getPiece();
            if (piece.getPiecePlayer() != adjacentPiece.getPiecePlayer() && piece.pieceStrength > adjacentPiece.pieceStrength){
                tiles.add(adjacentOccupiedTile);
            }
        }
        return tiles;
    }
    private void generatePushToTiles(Tile tile){
        // find all tiles, where piece on tile can be pushed
        pushToTiles.clear();
        pushToTiles.addAll(tile.adjacentFreeTiles(board));
    }

    private void handleTraps(Tile fromTile, Tile toTile){
        // handle everything trap related, when piece is moved from formTile to toTile
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
        goldPieces.remove(piece);
        silverPieces.remove(piece);

        tile.removePiece();
    }

    private void switchStopwatch() {
        // pause current players stopwatch and start opposite players stopwatch
        if (currentPlayer == Player.GOLD) {
            gui.goldPlayerStopwatch.stop();
            if (gameType == GameType.versusPlayer){
                gui.silverPlayerStopwatch.start();
            }
        } else {
            if (gameType == GameType.versusPlayer){
                gui.silverPlayerStopwatch.stop();
            }
            gui.goldPlayerStopwatch.start();
        }
    }

    private void playComputerTurn(){
        int secondsPerMove = 2;
        int computerMoveCount = ThreadLocalRandom.current().nextInt(1, 5);
        System.out.println(computerMoveCount);
        gui.delayComputerMoves(this, computerMoveCount, secondsPerMove);
    }


    public void playComputerMove(){
        Collections.shuffle(silverPieces);
        for (Piece piece : silverPieces){
            movementTiles = piece.getLegalMovementTiles(board);
            if (!movementTiles.isEmpty()){
                Tile fromTile = board.tiles[piece.piecePositionY][piece.piecePositionX];

                Tile toTile = randomTile(movementTiles);

                movePiece(fromTile, toTile);
                handleTraps(fromTile, toTile);
                gui.fillTile(fromTile, gui.selectedTileColor);
                gui.fillTile(toTile, gui.movemetToColor);
                break;
            }
        }
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
        // checks if piece on tile should be removed
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

        List<Piece> currentPlayerRabbit = currentPlayer == Player.GOLD ? goldRabbitPieces : silverRabbitPieces;
        List<Piece> oppositePlayerRabbit = oppositePlayer == Player.GOLD ? goldRabbitPieces : silverRabbitPieces;

        if (rabbitReachedGoal(currentPlayer)){
            logger.info("Current player rabbit reached goal");
            winner = currentPlayer;
        } else if (rabbitReachedGoal(oppositePlayer)){
            logger.info("Opposite player rabbit reached goal");
            winner = oppositePlayer;
        } else if (oppositePlayerRabbit.size() == 0) {
            logger.info("Opposite player has no rabbits left");
            winner = currentPlayer;
        } else if (currentPlayerRabbit.size() == 0) {
            logger.info("Current player has no rabbits left");
            winner = oppositePlayer;
        } else if (!hasPossibleMoves(oppositePlayer)) {
            logger.info("Opposite player has no possible moves");
            winner = currentPlayer;
        }

        if (winner != null){
            gameIsOver = true;
        }
        return winner;
    }

    private boolean boardChanged() {
        for (int y = 0; y < 8; ++y){
            for (int x = 0; x < 8; ++x){
                if (!(board.tiles[y][x].equals(lastBoard.tiles[y][x]))){
                    System.out.println("TST");
                    logger.info("Board was changed on tile x: " + x + " y: " + y);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasPossibleMoves (Player player) {
        boolean hasPossibleMoves = false;
        List<Piece> pieces = player == Player.GOLD ? goldPieces : silverPieces;
        Tile tile;
        for (Piece piece : pieces){
            tile = board.tiles[piece.piecePositionY][piece.piecePositionX];
            if (!isPieceFrozen(tile)){
                Set<Tile> possibleMovementTiles = piece.getLegalMovementTiles(board);
                Set<Tile> possiblePushFromTiles = generatePushFromTiles(tile);
                if (possibleMovementTiles.size() > 0 || possiblePushFromTiles.size() > 0){
                    hasPossibleMoves = true;
                    break;
                }
            }
        }

        return hasPossibleMoves;
    }

    private boolean rabbitReachedGoal(Player player){
        boolean ret = false;

        int goalPositionY = player == Player.GOLD ? 0 : 7;
        List<Piece> rabbitPieces = player == Player.GOLD ? goldRabbitPieces : silverRabbitPieces;

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
        // set tiles for setup phase of the game for players turn
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

    public void switchPiecesOnTiles(Tile tile1, Tile tile2) {
        Piece tmpPiece = tile1.piece;
        movePiece(tile2, tile1);

        tmpPiece.piecePositionX = tile2.tileCoordinateX;
        tmpPiece.piecePositionY = tile2.tileCoordinateY;
        tile2.setPiece(tmpPiece);
    }

    public void generatePiecesPosition(String fileName){
        // set pieces according to map in file
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

                    if (piece != null){
                        if (piece.getPiecePlayer() == Player.GOLD){
                            goldPieces.add(piece);
                        } else {
                            silverPieces.add(piece);
                        }
                    }
                }
                y++;
            }
        } catch (IOException e) {
            logger.warning("Error while reading start piece position file");
            e.printStackTrace();
        }
    }
}
