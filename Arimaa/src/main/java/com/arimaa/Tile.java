package com.arimaa;

import com.arimaa.pieces.Piece;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Tile {

    final public int tileCoordinateX;
    final public int tileCoordinateY;
    Piece piece;
    final public boolean isTrap;
    public Rectangle tileSquare;
    public Text pieceText;

    public Tile(final int tileCoordinateX, final int tileCoordinateY, final boolean isTrap) {
        this.tileCoordinateX = tileCoordinateX;
        this.tileCoordinateY = tileCoordinateY;
        this.isTrap = isTrap;
        this.piece = null;
        this.pieceText = new Text();
    }

    public boolean isTileOccupied(){
        return piece != null;
    }

    public Piece getPiece(){
        return piece;
    }
    public void setPiece(Piece piece) {
        this.piece = piece;
        String pieceString = "";
        if (piece != null){
            String color = piece.getPiecePlayer() == Player.SILVER ? "S" : "G";
            pieceString = piece.c + "-" + color;
        }
        pieceText.setText(pieceString);
    }

    public List<Tile> adjacentTiles(Board board){
        List<Tile> tiles = new ArrayList<>();
        if (tileCoordinateY > 0){
            tiles.add(board.tiles[tileCoordinateY - 1][tileCoordinateX]);
        }
        if (tileCoordinateY < 7){
            tiles.add(board.tiles[tileCoordinateY + 1][tileCoordinateX]);
        }
        if (tileCoordinateX > 0){
            tiles.add(board.tiles[tileCoordinateY][tileCoordinateX - 1]);
        }
        if (tileCoordinateX < 7){
            tiles.add(board.tiles[tileCoordinateY][tileCoordinateX + 1]);
        }

        return tiles;
    }

    public List<Tile> adjacentOccupiedTiles(Board board){
        List<Tile> tiles = adjacentTiles(board);
        tiles.removeIf(adjacentTile -> !adjacentTile.isTileOccupied());
        return tiles;
    }

    public List<Tile> adjacentFreeTiles(Board board){
        List<Tile> tiles = adjacentTiles(board);
        tiles.removeIf(Tile::isTileOccupied);
        return tiles;
    }

    public void removePiece(){
        piece = null;
        pieceText.setText("");
    }
}

