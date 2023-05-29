package com.arimaa.pieces_src;

import com.arimaa.*;

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

