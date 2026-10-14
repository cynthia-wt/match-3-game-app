package actions;

import model.Alignment;
import model.Brick;
import model.GameState;

import java.util.Arrays;

public class BrickAction {

    public static GameState moveToBottom(char[][] currentFrame,
                                         Brick currentBrick,
                                         int currentHorizontalPosition,
                                         int currentVerticalPosition) {

        Alignment currentBrickAlignment = currentBrick.getAlignment();
        char[] currentBrickSymbols = currentBrick.getSymbols();
        clearExistingPosition(currentFrame, currentBrickAlignment, currentHorizontalPosition, currentVerticalPosition);

        int lowestPossibleRow = getLowestPossibleRow(currentFrame, currentBrickAlignment, currentHorizontalPosition, currentVerticalPosition);
        if (currentBrickAlignment.equals(Alignment.HORIZONTAL)) {
            currentFrame[lowestPossibleRow][currentHorizontalPosition] = currentBrickSymbols[0];
            currentFrame[lowestPossibleRow][currentHorizontalPosition + 1] = currentBrickSymbols[1];
            currentFrame[lowestPossibleRow][currentHorizontalPosition + 2] = currentBrickSymbols[2];
        } else {
            currentFrame[lowestPossibleRow][currentHorizontalPosition] = currentBrickSymbols[2];
            currentFrame[lowestPossibleRow - 1][currentHorizontalPosition] = currentBrickSymbols[1];
            currentFrame[lowestPossibleRow - 2][currentHorizontalPosition] = currentBrickSymbols[0];
        }

        return GameState.builder()
                .currentFrame(currentFrame)
                .currentHorizontalPosition(-1)
                .currentVerticalPosition(-1)
                .build();
    }

    public static GameState moveHorizontally(int horizontalMovement,
                                             Brick currentBrick,
                                             char[][] currentFrame,
                                             int currentHorizontalPosition,
                                             int currentVerticalPosition) {

        int finalHorizontalPosition = currentHorizontalPosition + horizontalMovement;
        Alignment currentBrickAlignment = currentBrick.getAlignment();
        char[] currentBrickSymbols = currentBrick.getSymbols();

        if (checkForWalls(currentFrame[0].length, currentBrickAlignment, finalHorizontalPosition) ||
                checkForCollisions(currentFrame, currentBrickAlignment, currentHorizontalPosition, currentVerticalPosition, finalHorizontalPosition)) {
            return GameState.builder()
                    .currentFrame(currentFrame)
                    .currentHorizontalPosition(currentHorizontalPosition)
                    .currentVerticalPosition(currentVerticalPosition)
                    .build();
        }

        clearExistingPosition(currentFrame, currentBrickAlignment, currentHorizontalPosition, currentVerticalPosition);

        if (currentBrickAlignment.equals(Alignment.HORIZONTAL)) {
            currentFrame[currentVerticalPosition][finalHorizontalPosition] = currentBrickSymbols[0];
            currentFrame[currentVerticalPosition][finalHorizontalPosition + 1] = currentBrickSymbols[1];
            currentFrame[currentVerticalPosition][finalHorizontalPosition + 2] = currentBrickSymbols[2];
        } else {
            currentFrame[currentVerticalPosition][finalHorizontalPosition] = currentBrickSymbols[0];
            currentFrame[currentVerticalPosition + 1][finalHorizontalPosition] = currentBrickSymbols[1];
            currentFrame[currentVerticalPosition + 2][finalHorizontalPosition] = currentBrickSymbols[2];
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
                    if (currentFrame[v][h] != '.') {
                        return v - 1;
                    }
                }
            }
        } else {
            for (int v = currentVerticalPosition + 3; v < currentFrame.length; v++) {
                if (currentFrame[v][currentHorizontalPosition] != '.') {
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

    private static boolean checkForCollisions(char[][] currentFrame, Alignment currentBrickAlignment, int currentHorizontalPosition, int currentVerticalPosition, int finalHorizontalPosition) {
        char[][] tempFrameForCollisionCheck = Arrays.stream(currentFrame)
                .map(char[]::clone)
                .toArray(char[][]::new);
        clearExistingPosition(tempFrameForCollisionCheck, currentBrickAlignment, currentHorizontalPosition, currentVerticalPosition);

        if (currentBrickAlignment.equals(Alignment.HORIZONTAL)) {
            for (int i = finalHorizontalPosition; i < finalHorizontalPosition + 3; i++) {
                if (tempFrameForCollisionCheck[currentVerticalPosition][i] != '.') {
                    return true;
                }
            }
        } else {
            for (int i = currentVerticalPosition; i < currentVerticalPosition + 3; i++) {
                if (tempFrameForCollisionCheck[i][finalHorizontalPosition] != '.') {
                    return true;
                }
            }
        }
        return false;
    }

    public static void clearExistingPosition(char[][] frame, Alignment currentBrickAlignment, int currentHorizontalPosition, int currentVerticalPosition) {
        if (currentBrickAlignment.equals(Alignment.HORIZONTAL)) {
            frame[currentVerticalPosition][currentHorizontalPosition] = '.';
            frame[currentVerticalPosition][currentHorizontalPosition + 1] = '.';
            frame[currentVerticalPosition][currentHorizontalPosition + 2] = '.';
        } else {
            frame[currentVerticalPosition][currentHorizontalPosition] = '.';
            frame[currentVerticalPosition + 1][currentHorizontalPosition] = '.';
            frame[currentVerticalPosition + 2][currentHorizontalPosition] = '.';
        }
    }
}
