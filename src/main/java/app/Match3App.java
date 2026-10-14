package app;

import client.GameConsole;
import engine.GameEngine;
import model.Command;
import model.InitializationInput;

import java.util.List;

public class Match3App {

    public static void main(String[] args) {
        // Get initialization input
        GameConsole gameConsole = new GameConsole();
        InitializationInput initializationInput = gameConsole.initialiseGame();

        // Initialize game engine
        GameEngine gameEngine = new GameEngine(initializationInput);
        boolean continueGame = true;

        // Release the first brick
        gameEngine.nextBrick();

        while (continueGame) {
            List<Command> commands = gameConsole.nextTurn(gameEngine.getCurrentFrame(), gameEngine.getCurrentFrameNumber());
            gameEngine.nextTurn(commands);
        }
    }
}