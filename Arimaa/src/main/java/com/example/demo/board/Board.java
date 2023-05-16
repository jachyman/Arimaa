package com.example.demo.board;

import com.example.demo.pieces.Rabbit;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;
import java.util.Set;

public class Board {
    public Tile[][] tiles = new Tile[8][8];
    Set<Integer> trapCoordinates = Set.of(18, 21, 42, 45);
    public Board (){
        for (int y = 0; y < 8; ++y){
            for (int x = 0; x < 8; ++x){
                boolean isTrap = trapCoordinates.contains((y * 8) + x);
                tiles[y][x] = new Tile(x, y, isTrap);
            }
        }
    }
    public void clear(){
        Rectangle tile;
        for (int i = 0; i < 8; ++i){
            for (int j = 0; j < 8; ++j){
                tile = tiles[i][j].tileSquare;
                tile.setFill(Color.WHITE);
                if (this.getTile(i, j).isTrap){
                    tile.setFill(Color.LIGHTGRAY);
                }
            }
        }
    }
    //boolean isTrap = trapCoordinates.contains(i);
    //tiles[i / 8][i % 8] = new Tile(i, isTrap);

    public Tile getTile(int x, int y){
        return tiles[x][y];
    }

    public void print(){
        for (int i = 0; i < 8; ++i){
            for (int j = 0; j < 8; ++j){
                Tile t = tiles[i][j];
                if (t.isTileOccupied())
                    System.out.print(t.piece.c);
                else if (t.isTrap)
                    System.out.print("X");
                else
                    System.out.print("O");
            }
            System.out.println();
        }
        System.out.println("-----------------------------------");
    }
}
