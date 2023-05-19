package com.arimaa.pieces;

import com.arimaa.Board;
import com.arimaa.Move;
import com.arimaa.Player;

import java.util.Set;

public abstract class Piece {

    public int piecePositionX;
    public int piecePositionY;
    protected Player piecePlayer;
    protected int pieceStrength;
    public char c;
    public boolean isRabbit;

    public Piece(int piecePositionX, int piecePositionY , Player piecePlayer) {
        this.piecePositionX = piecePositionX;
        this.piecePositionY = piecePositionY;
        this.piecePlayer = piecePlayer;
    }

    abstract public Set<Move> generateLegalMoves(Board board);
    public Player getPiecePlayer(){
        return piecePlayer;
    }
}

