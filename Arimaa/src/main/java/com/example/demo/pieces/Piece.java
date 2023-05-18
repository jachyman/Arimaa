package com.example.demo.pieces;

import com.example.demo.Player;
import com.example.demo.board.Board;
import com.example.demo.board.Move;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Piece {

    public int piecePositionX;
    public int piecePositionY;
    protected Player piecePlayer;
    protected int pieceStrength;
    public char c;

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
