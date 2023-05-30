package com.arimaa;

import com.arimaa.pieces_src.Piece;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tile {

    final public int tileCoordinateX;
    final public int tileCoordinateY;
    Piece piece;
    final public boolean isTrap;
    public Rectangle tileSquare;
    public Rectangle pieceSquare;
    public Text pieceText;

    public Tile(final int tileCoordinateX, final int tileCoordinateY, final boolean isTrap) {
        this.tileCoordinateX = tileCoordinateX;
        this.tileCoordinateY = tileCoordinateY;
        this.isTrap = isTrap;
        this.piece = null;
        this.pieceText = new Text();
    }

    public Tile(Tile that) {
        this(that.tileCoordinateX, that.tileCoordinateY, that.isTrap);
    }

    @Override
    public boolean equals(Object o){
        if (o == this){
            return true;
        }
        if (!(o instanceof Tile)){
            return false;
        }

        Tile t = (Tile) o;

        if (this.isTileOccupied() != t.isTileOccupied()){
            return false;
        }

        if (!t.isTileOccupied()){
            return true;
        }

        return this.piece.getPiecePlayer() == t.piece.getPiecePlayer()
                && this.piece.pieceStrength == t.piece.pieceStrength;
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

