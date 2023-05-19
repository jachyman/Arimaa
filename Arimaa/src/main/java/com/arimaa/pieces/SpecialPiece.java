package com.arimaa.pieces;

import com.arimaa.Board;
import com.arimaa.Move;
import com.arimaa.Player;

import java.util.HashSet;
import java.util.Set;

public class SpecialPiece extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATES = {-8, -1, 1, 8};

    public SpecialPiece(int piecePositionX, int piecePositionY, Player piecePlayer) {
        super(piecePositionX, piecePositionY, piecePlayer);
        isRabbit = false;
    }

    @Override
    public Set<Move> generateLegalMoves(Board board) {
        Set<Move> moves = new HashSet<>();
        if (piecePositionY > 0 && !board.tiles[piecePositionY - 1][piecePositionX].isTileOccupied()){
            moves.add(Move.UP);
        }
        if (piecePositionY < 7 && !board.tiles[piecePositionY + 1][piecePositionX].isTileOccupied()){
            moves.add(Move.DOWN);
        }
        if (piecePositionX > 0 && !board.tiles[piecePositionY][piecePositionX - 1].isTileOccupied()){
            moves.add(Move.LEFT);
        }
        if (piecePositionX < 7 && !board.tiles[piecePositionY][piecePositionX + 1].isTileOccupied()){
            moves.add(Move.RIGHT);
        }

        return moves;
    }

    public static class Cat extends SpecialPiece {
        public Cat(int piecePositionX, int piecePositionY, Player piecePlayer) {
            super(piecePositionX, piecePositionY, piecePlayer);
            this.pieceStrength = 2;
            c = 'c';
        }
    }
    public static class Dog extends SpecialPiece {
        public Dog(int piecePositionX, int piecePositionY, Player piecePlayer) {
            super(piecePositionX, piecePositionY, piecePlayer);
            this.pieceStrength = 3;
            c = 'D';
        }
    }
    public static class Horse extends SpecialPiece {
        public Horse(int piecePositionX, int piecePositionY, Player piecePlayer) {
            super(piecePositionX, piecePositionY, piecePlayer);
            this.pieceStrength = 4;
            c = 'H';
        }
    }
    public static class Camel extends SpecialPiece {
        public Camel(int piecePositionX, int piecePositionY, Player piecePlayer) {
            super(piecePositionX, piecePositionY, piecePlayer);
            this.pieceStrength = 5;
            c = 'C';
        }
    }
    public static class Elephant extends SpecialPiece {
        public Elephant(int piecePositionX, int piecePositionY, Player piecePlayer) {
            super(piecePositionX, piecePositionY, piecePlayer);
            this.pieceStrength = 6;
            c = 'E';
        }
    }
}

