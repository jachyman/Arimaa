package com.arimaa.pieces;

import com.arimaa.*;

import java.util.HashSet;
import java.util.Set;

public class Rabbit extends Piece{

    public final static int[] CANDIDATE_MOVE_COORDINATES = {-1, 1, 8};

    public Rabbit(int piecePositionX, int piecePositionY, Player piecePlayer) {
        super(piecePositionX, piecePositionY, piecePlayer);
        c = 'R';
        isRabbit = true;
    }

    @Override
    protected void setPieceMovement() {
        pieceMovement.add(Direction.LEFT);
        pieceMovement.add(Direction.RIGHT);
        pieceMovement.add(piecePlayer == Player.GOLD ? Direction.UP : Direction.DOWN);
    }

}

