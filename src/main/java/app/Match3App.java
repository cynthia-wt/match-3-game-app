package app;

import client.GameConsole;
import engine.GameEngine;
import model.Command;
import model.InitializationInput;

import java.util.List;

public class Match3App {

    public static void main(String[] args) {
        boolean playAgain = true;
        GameConsole gameConsole = new GameConsole();

        while (playAgain) {
            startGameLoop();
            playAgain = gameConsole.startOver().equalsIgnoreCase("S");
        }

        gameConsole.quit();
    }

    private static void startGameLoop() {
        GameConsole gameConsole = new GameConsole();
        InitializationInput initializationInput = gameConsole.initialiseGame();
        GameEngine gameEngine = new GameEngine(initializationInput);

        while (true) {
            if (gameEngine.getCurrentGameState().getActiveBrick().isLanded()) {
                if (gameEngine.hasGameEnded()) {
                    gameConsole.gameOver(gameEngine.getCurrentFrame(), gameEngine.getCurrentGameState().getFrameNumber());
                    break;
                }

                gameEngine.nextBrick();
            }

            List<Command> commands = gameConsole.nextTurn(gameEngine.getCurrentFrame(), gameEngine.getCurrentGameState().getFrameNumber());
            gameEngine.nextTurn(commands);
        }
    }
}