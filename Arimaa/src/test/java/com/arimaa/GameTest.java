package com.arimaa;

import com.arimaa.pieces_src.Rabbit;
import com.arimaa.pieces_src.SpecialPiece.Dog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class GameTest {
    @Test
    void switchPiecesOnTilesShouldSwitchPieces() {
        Board board = new Board();
        GUI gui = null;

        int rabbitX = 0;
        int rabbitY = 0;
        int dogX = 1;
        int dogY = 1;

        Rabbit rabbit = new Rabbit(rabbitX, rabbitY, Player.GOLD);
        Dog dog = new Dog(dogX, dogY, Player.SILVER);

        Tile originalRabbitTile = new Tile(rabbitX, rabbitY, false);
        Tile originalDogTile = new Tile(dogX, dogY, false);

        originalRabbitTile.setPiece(rabbit);
        originalDogTile.setPiece(dog);

        board.tiles[rabbitY][rabbitX] = originalRabbitTile;
        board.tiles[dogY][dogX] = originalDogTile;

        Game game = new Game(board, gui, false);
        game.switchPiecesOnTiles(originalDogTile, originalRabbitTile);

        assertEquals(rabbit, originalDogTile.getPiece());
        assertEquals(dog, originalRabbitTile.getPiece());
    }

    @Test
    void movePieceShouldMovePiece() {
        Board board = new Board();
        GUI gui = null;

        int pieceX = 1;
        int pieceY = 1;
        int destinationX = 3;
        int destinationY = 5;

        Dog piece = new Dog(pieceX, pieceY, Player.SILVER);

        Tile startTile = new Tile(pieceX, pieceY, false);
        Tile destinationTile = new Tile(destinationX, destinationY, false);

        startTile.setPiece(piece);

        board.tiles[pieceY][pieceX] = startTile;
        board.tiles[destinationY][destinationX] = destinationTile;

        Game game = new Game(board, gui, false);
        game.movePiece(startTile, destinationTile);

        assert(!startTile.isTileOccupied());
        assertEquals(piece, destinationTile.getPiece());
    }
}