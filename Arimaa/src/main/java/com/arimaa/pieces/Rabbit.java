package com.arimaa.pieces;

import com.arimaa.Board;
import com.arimaa.Move;
import com.arimaa.Player;

import java.util.HashSet;
import java.util.Set;

public class Rabbit extends Piece{

    private final static int[] CANDIDATE_MOVE_COORDINATES = {-1, 1, 8};

    public Rabbit(int piecePositionX, int piecePositionY, Player piecePlayer) {
        super(piecePositionX, piecePositionY, piecePlayer);
        c = 'R';
        isRabbit = true;
    }

    @Override
    public Set<Move> generateLegalMoves(Board board) {
        Set<Move> moves = new HashSet<>();
        if (piecePlayer == Player.GOLD && piecePositionY > 0 && !board.tiles[piecePositionY - 1][piecePositionX].isTileOccupied()){
            moves.add(Move.UP);
        }
        if (piecePlayer == Player.SILVER && piecePositionY < 7 && !board.tiles[piecePositionY + 1][piecePositionX].isTileOccupied()){
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

