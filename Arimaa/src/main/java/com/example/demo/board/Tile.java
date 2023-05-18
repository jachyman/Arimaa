package com.example.demo.board;
import com.example.demo.Player;
import com.example.demo.pieces.Piece;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

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
            String color = piece.getPiecePlayer() == Player.WHITE ? "W" : "B";
            pieceString = piece.c + "-" + color;
        }
        pieceText.setText(pieceString);
    }
}
