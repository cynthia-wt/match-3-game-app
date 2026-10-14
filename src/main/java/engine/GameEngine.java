package engine;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import model.*;

import java.util.Arrays;
import java.util.List;

import static actions.BrickAction.moveHorizontally;
import static actions.BrickAction.moveToBottom;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GameEngine {

    final List<Brick> bricks;
    char[][] currentFrame;
    int currentFrameNumber;
    int currentBrickIndex;
    final int boardWidth;
    final int boardHeight;
    int currentVerticalPosition;
    int currentHorizontalPosition;

    public GameEngine(InitializationInput initializationInput) {
        int width = initializationInput.getBoardWidth();
        int height = initializationInput.getBoardHeight();

        char[][] board = new char[height][width];
        for (char[] row : board) {
            Arrays.fill(row, '.');
        }

        this.bricks = initializationInput.getBricks();
        this.currentBrickIndex = -1;
        this.currentFrame = board;
        this.currentFrameNumber = 1;
        this.boardWidth = initializationInput.getBoardWidth();
        this.boardHeight = initializationInput.getBoardHeight();
    }

    public void nextTurn(List<Command> commands) {
        executeCommands(commands);
        // move by down by 1 row => goal today

        // check for matches
        // check if all the bricks are used up or all the available spaces are filled up
        // add new brick - check if possible
    }

    public void nextBrick() {
        currentBrickIndex = currentBrickIndex + 1;
        currentVerticalPosition = 0;

        Brick nextBrick = bricks.get(currentBrickIndex);
        Alignment alignment = nextBrick.getAlignment();
        char[] symbols = nextBrick.getSymbols();

        if (alignment.equals(Alignment.HORIZONTAL)) {
            int start = (boardWidth - 3) / 2;
            currentHorizontalPosition = start;
            for (int i = 0; i < symbols.length; i++) {
                currentFrame[0][start + i] = symbols[i];
            }
        } else {
            int col = (boardWidth - 1) / 2;
            currentHorizontalPosition = col;
            for (int i = 0; i < symbols.length; i++) {
                currentFrame[i][col] = symbols[i];
            }
        }
    }

    private void executeCommands(List<Command> commands) {
        Brick currentBrick = bricks.get(currentBrickIndex);

        for (Command command : commands) {
            switch (command) {
                case LEFT -> updateGameState(moveHorizontally(-1, currentBrick, currentFrame, currentHorizontalPosition, currentVerticalPosition));
                case RIGHT -> updateGameState(moveHorizontally(1, currentBrick, currentFrame, currentHorizontalPosition, currentVerticalPosition));
                case DOWN -> updateGameState(moveToBottom(currentFrame, currentBrick, currentHorizontalPosition, currentVerticalPosition));
            };
        }
    }

    private void updateGameState(GameState gameState) {
        this.currentFrame = gameState.getCurrentFrame();
        this.currentHorizontalPosition = gameState.getCurrentHorizontalPosition();
        this.currentVerticalPosition = gameState.getCurrentVerticalPosition();
    }

}
