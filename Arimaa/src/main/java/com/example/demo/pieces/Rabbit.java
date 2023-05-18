package com.example.demo.pieces;

import com.example.demo.Player;
import com.example.demo.board.Board;
import com.example.demo.board.Move;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Rabbit extends Piece{

    private final static int[] CANDIDATE_MOVE_COORDINATES = {-1, 1, 8};

    public Rabbit(int piecePositionX, int piecePositionY, Player piecePlayer) {
        super(piecePositionX, piecePositionY, piecePlayer);
        c = 'R';
    }

    @Override
    public Set<Move> generateLegalMoves(Board board) {
        Set<Move> moves = new HashSet<>();
        if (piecePlayer == Player.WHITE && piecePositionY > 0 && !board.tiles[piecePositionY - 1][piecePositionX].isTileOccupied()){
            moves.add(Move.UP);
        }
        if (piecePlayer == Player.BLACK && piecePositionY < 7 && !board.tiles[piecePositionY + 1][piecePositionX].isTileOccupied()){
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

}
