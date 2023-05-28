package com.arimaa;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class ArimaaApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        GUI gui = new GUI(stage);

        Board board = new Board();
        Game game = new Game(board, gui);

        gui.createMenuScene(game);

        gui.setStageMenu();
        gui.showStage();
    }

    public static void main(String[] args) {
        launch();
    }
}