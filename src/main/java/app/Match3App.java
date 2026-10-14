package app;

import client.GameConsole;
import model.Command;
import model.InitializationInput;
import engine.GameEngine;

import java.util.List;

public class Match3App {

    public static void main(String[] args) {
        GameConsole gameConsole = new GameConsole();
        InitializationInput initializationInput = gameConsole.initialiseGame();
        GameEngine gameEngine = new GameEngine(initializationInput);
        boolean continueGame = true;
        gameEngine.nextBrick();

        while (continueGame) {
            List<Command> commands = gameConsole.nextTurn(gameEngine.getCurrentFrame(), gameEngine.getCurrentFrameNumber());
            gameEngine.nextTurn(commands);
        }
    }
}