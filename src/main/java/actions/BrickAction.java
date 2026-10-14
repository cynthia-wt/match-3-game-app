package actions;

import model.Alignment;
import model.Brick;
import model.GameState;

import java.util.Arrays;

import static engine.GameEngine.BLANK_CELL;

public class BrickAction {

    public static GameState moveToBottom(char[][] currentFrame,
                                         Brick currentBrick,
                                         int currentHorizontalPosition,
                                         int currentVerticalPosition) {

        Alignment currentBrickAlignment = currentBrick.getAlignment();
        char[] currentBrickSymbols = currentBrick.getSymbols();
        clearExistingPosition(currentFrame, currentBrick, currentHorizontalPosition, currentVerticalPosition);

        int lowestPossibleRow = getLowestPossibleRow(currentFrame, currentBrickAlignment, currentHorizontalPosition, currentVerticalPosition);
        if (currentBrickAlignment.equals(Alignment.HORIZONTAL)) {
            for (int i = 0; i < currentBrickSymbols.length; i++) {
                currentFrame[lowestPossibleRow][currentHorizontalPosition + i] = currentBrickSymbols[i];
            }
        } else {
            int topRow = lowestPossibleRow - 2;
            for (int i = 0; i < currentBrickSymbols.length; i++) {
                currentFrame[topRow + i][currentHorizontalPosition] = currentBrickSymbols[i];
            }
        }

        return GameState.builder()
                .currentFrame(currentFrame)
                .currentHorizontalPosition(-1)
                .currentVerticalPosition(-1)
                .build();
    }

    public static GameState moveHorizontally(char[][] currentFrame,
                                             int horizontalMovement,
                                             Brick currentBrick,
                                             int currentHorizontalPosition,
                                             int currentVerticalPosition) {

        int finalHorizontalPosition = currentHorizontalPosition + horizontalMovement;
        Alignment currentBrickAlignment = currentBrick.getAlignment();
        char[] currentBrickSymbols = currentBrick.getSymbols();

        if (checkForWalls(currentFrame[0].length, currentBrickAlignment, finalHorizontalPosition) ||
                checkForCollisions(currentFrame, currentBrick, currentHorizontalPosition, currentVerticalPosition, finalHorizontalPosition)) {
            return GameState.builder()
                    .currentFrame(currentFrame)
                    .currentHorizontalPosition(currentHorizontalPosition)
                    .currentVerticalPosition(currentVerticalPosition)
                    .build();
        }

        clearExistingPosition(currentFrame, currentBrick, currentHorizontalPosition, currentVerticalPosition);

        if (currentBrickAlignment.equals(Alignment.HORIZONTAL)) {
            for (int i = 0; i < currentBrickSymbols.length; i++) {
                currentFrame[currentVerticalPosition][finalHorizontalPosition + i] = currentBrickSymbols[i];
            }
        } else {
            for (int i = 0; i < currentBrickSymbols.length; i++) {
                currentFrame[currentVerticalPosition + i][finalHorizontalPosition] = currentBrickSymbols[i];
            }
        }
        return GameState.builder()
                .currentFrame(currentFrame)
                .currentHorizontalPosition(finalHorizontalPosition)
                .currentVerticalPosition(currentVerticalPosition)
                .build();
    }

    private static int getLowestPossibleRow(char[][] currentFrame,
                                            Alignment currentBrickAlignment,
                                            int currentHorizontalPosition,
                                            int currentVerticalPosition) {

        if (currentBrickAlignment.equals(Alignment.HORIZONTAL)) {
            for (int v = currentVerticalPosition + 1; v < currentFrame.length; v++) {
                for (int h = currentHorizontalPosition; h <= currentHorizontalPosition + 2; h++) {
                    if (currentFrame[v][h] != BLANK_CELL) {
                        return v - 1;
                    }
                }
            }
        } else {
            for (int v = currentVerticalPosition + 3; v < currentFrame.length; v++) {
                if (currentFrame[v][currentHorizontalPosition] != BLANK_CELL) {
                    return v - 1;
                }
            }
        }
        return currentFrame.length - 1;
    }


    private static boolean checkForWalls(int boardWidth, Alignment currentBrickAlignment, int finalHorizontalPosition) {
        if (currentBrickAlignment.equals(Alignment.HORIZONTAL)) {
            return finalHorizontalPosition < 0 || finalHorizontalPosition + 2 > boardWidth - 1;
        } else {
            return finalHorizontalPosition < 0 || finalHorizontalPosition > boardWidth - 1;
        }
    }

    private static boolean checkForCollisions(char[][] currentFrame, Brick currentBrick, int currentHorizontalPosition, int currentVerticalPosition, int finalHorizontalPosition) {
        char[][] tempFrameForCollisionCheck = Arrays.stream(currentFrame)
                .map(char[]::clone)
                .toArray(char[][]::new);
        clearExistingPosition(tempFrameForCollisionCheck, currentBrick, currentHorizontalPosition, currentVerticalPosition);

        if (currentBrick.getAlignment().equals(Alignment.HORIZONTAL)) {
            for (int i = finalHorizontalPosition; i < finalHorizontalPosition + 3; i++) {
                if (tempFrameForCollisionCheck[currentVerticalPosition][i] != BLANK_CELL) {
                    return true;
                }
            }
        } else {
            for (int i = currentVerticalPosition; i < currentVerticalPosition + 3; i++) {
                if (tempFrameForCollisionCheck[i][finalHorizontalPosition] != BLANK_CELL) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void clearExistingPosition(char[][] frame, Brick currentBrick, int currentHorizontalPosition, int currentVerticalPosition) {
        if (currentBrick.getAlignment().equals(Alignment.HORIZONTAL)) {
            for (int i = 0; i < currentBrick.getSymbols().length; i++) {
                frame[currentVerticalPosition][currentHorizontalPosition + i] = BLANK_CELL;
            }
        } else {
            for (int i = 0; i < currentBrick.getSymbols().length; i++) {
                frame[currentVerticalPosition + i][currentHorizontalPosition] = BLANK_CELL;
            }
        }
    }
}
