package com.arimaa.pieces;

import com.arimaa.*;

import java.util.HashSet;
import java.util.Set;

public abstract class Piece {

    public int piecePositionX;
    public int piecePositionY;
    protected Player piecePlayer;
    public int pieceStrength;
    protected Set<Direction> pieceMovement;
    public char c;
    public boolean isRabbit;

    public Piece(int piecePositionX, int piecePositionY , Player piecePlayer) {
        this.piecePositionX = piecePositionX;
        this.piecePositionY = piecePositionY;
        this.piecePlayer = piecePlayer;
        pieceMovement = new HashSet<>();
        setPieceMovement();
    }
    public Player getPiecePlayer(){
        return piecePlayer;
    }
    protected abstract void setPieceMovement();
    public Set<Tile> getLegalMovementTiles(Board board){
        Set<Tile> legalMovementTiles = new HashSet<>();
        Tile tile;

        int x = piecePositionX;
        int y = piecePositionY;

        for (Direction d : pieceMovement){
            if (d == Direction.LEFT && x > 0){
                tile = board.tiles[y][x-1];
                if (!tile.isTileOccupied()){
                    legalMovementTiles.add(tile);
                }
            }
            if (d == Direction.RIGHT && x < 7){
                tile = board.tiles[y][x+1];
                if (!tile.isTileOccupied()){
                    legalMovementTiles.add(tile);
                }
            }
            if (d == Direction.UP && y > 0){
                tile = board.tiles[y-1][x];
                if (!tile.isTileOccupied()){
                    legalMovementTiles.add(tile);
                }
            }
            if (d == Direction.DOWN && y < 7){
                tile = board.tiles[y+1][x];
                if (!tile.isTileOccupied()){
                    legalMovementTiles.add(tile);
                }
            }

        }

        return legalMovementTiles;
    }

}

