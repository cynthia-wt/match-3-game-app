package engine;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import model.*;

import java.util.Arrays;
import java.util.List;

import static actions.BrickAction.*;
import static actions.MatchAction.checkForMatches;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GameEngine {
    final char[][] currentFrame;
    final List<Brick> bricks;
    GameState currentGameState;

    public static final char BLANK_CELL = '.';

    public GameEngine(InitializationInput initializationInput) {
        int width = initializationInput.getBoardWidth();
        int height = initializationInput.getBoardHeight();
        char[][] board = new char[height][width];
        for (char[] row : board) {
            Arrays.fill(row, BLANK_CELL);
        }
        this.currentFrame = board;
        this.bricks = initializationInput.getBricks();
        this.currentGameState = GameState.builder()
                .frameNumber(1)
                .brickIndex(-1)
                .activeBrick(ActiveBrick.builder()
                        .brick(null)
                        .horizontalPosition(-1)
                        .verticalPosition(-1)
                        .landed(false)
                        .build())
                .build();
    }

    public boolean nextTurn(List<Command> commands) {
        executeCommands(commands);
        dropByOneRow();

        if (currentGameState.getActiveBrick().isLanded()) {
            checkForMatches(currentFrame, currentGameState.getActiveBrick());
        }

        // check if all the bricks are used up or cannot add new brick anymore - tomorrow/friday morning
        // testing and enhancement - sunday
        // check once more, before submitting by 5pm - monday

        incrementFrameNumber();
        return true;
    }

    public void nextBrick() {
        int currentBrickIndex = currentGameState.getBrickIndex() + 1;
        int currentVerticalPosition = 0;
        int currentHorizontalPosition;

        Brick nextBrick = bricks.get(currentBrickIndex);
        Alignment alignment = nextBrick.getAlignment();
        char[] symbols = nextBrick.getSymbols();
        int boardWidth = currentFrame[0].length;

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

        ActiveBrick activeBrick = ActiveBrick.builder()
                .brick(nextBrick)
                .horizontalPosition(currentHorizontalPosition)
                .verticalPosition(currentVerticalPosition)
                .landed(false)
                .build();

        this.currentGameState = currentGameState.toBuilder()
                .brickIndex(currentBrickIndex)
                .activeBrick(activeBrick)
                .build();
    }

    private void executeCommands(List<Command> commands) {
        for (Command command : commands) {
            ActiveBrick activeBrick = currentGameState.getActiveBrick();
            if (activeBrick.isLanded()) {
                break;
            }

            switch (command) {
                case LEFT -> updateActiveBrickState(moveHorizontally(currentFrame, -1, activeBrick));
                case RIGHT -> updateActiveBrickState(moveHorizontally(currentFrame, 1, activeBrick));
                case DOWN -> updateActiveBrickState(moveToBottom(currentFrame, activeBrick));
            }
            ;
        }
    }

    private void dropByOneRow() {
        if (!currentGameState.getActiveBrick().isLanded()) {

            updateActiveBrickState(moveDownByOneRow(currentFrame, currentGameState.getActiveBrick()));
        }
    }

    private void updateActiveBrickState(ActiveBrick activeBrick) {
        this.currentGameState = currentGameState.toBuilder()
                .activeBrick(activeBrick)
                .build();
    }

    private void incrementFrameNumber() {
        int currentFrameNumber = currentGameState.getFrameNumber();
        this.currentGameState = currentGameState.toBuilder()
                .frameNumber(currentFrameNumber + 1)
                .build();
    }

}
