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
                        .landed(true)
                        .build())
                .build();
    }

    public void nextTurn(List<Command> commands) {
        executeCommands(commands);
        dropByOneRow();

        if (currentGameState.getActiveBrick().isLanded()) {
            checkForMatches(currentFrame, currentGameState.getActiveBrick());
        }

        incrementFrameNumber();
    }

    public void nextBrick() {
        int currentBrickIndex = currentGameState.getBrickIndex() + 1;
        int currentVerticalPosition = 0;
        int currentHorizontalPosition = getBrickFirstHorizontalPosition(bricks.get(currentBrickIndex));

        Brick nextBrick = bricks.get(currentBrickIndex);
        Alignment alignment = nextBrick.getAlignment();
        char[] symbols = nextBrick.getSymbols();

        if (alignment.equals(Alignment.HORIZONTAL)) {
            for (int i = 0; i < symbols.length; i++) {
                currentFrame[0][currentHorizontalPosition + i] = symbols[i];
            }
        } else {
            for (int i = 0; i < symbols.length; i++) {
                currentFrame[i][currentHorizontalPosition] = symbols[i];
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

    public boolean hasGameEnded() {
        if (currentGameState.getBrickIndex() + 1 >= bricks.size()) {
            return true;
        }

        Brick nextBrick = bricks.get(currentGameState.getBrickIndex() + 1);
        int nextBrickHorizontalPosition = getBrickFirstHorizontalPosition(nextBrick);
        if (nextBrick.getAlignment().equals(Alignment.HORIZONTAL)) {
            for (int i = 0; i < nextBrick.getSymbols().length; i++) {
                if (currentFrame[0][nextBrickHorizontalPosition + i] != BLANK_CELL) {
                    return true;
                }
            }
        } else {
            if (currentFrame[0][nextBrickHorizontalPosition] != BLANK_CELL) {
                return true;
            }
        }

        return false;
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

    private int getBrickFirstHorizontalPosition(Brick brick) {
        Alignment alignment = brick.getAlignment();
        int boardWidth = currentFrame[0].length;
        if (alignment.equals(Alignment.HORIZONTAL)) {
            return (boardWidth - 3) / 2;
        } else {
            return (boardWidth - 1) / 2;
        }
    }
}
