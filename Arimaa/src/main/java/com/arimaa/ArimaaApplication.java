package com.arimaa;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ArimaaApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parameters parameters = getParameters();
        List<String> listParameters = parameters.getRaw();
        boolean useLogger = listParameters.size() > 0 && listParameters.get(0) == "logger";

        GUI gui = new GUI(stage);

        Board board = new Board();
        Game game = new Game(board, gui, useLogger);

        gui.createMenuScene(game);

        gui.setStageMenu();
        gui.showStage();
    }

    public static void main(String[] args) {
        launch(args);
    }
}