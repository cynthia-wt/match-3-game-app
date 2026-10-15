package actions;

import model.ActiveBrickState;
import model.Alignment;
import model.Brick;

import java.util.Arrays;

import static engine.GameEngine.BLANK_CELL;

public class BrickAction {

    public static ActiveBrickState moveToBottom(char[][] currentFrame,
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

        int finalVerticalPosition = currentBrickAlignment.equals(Alignment.HORIZONTAL)
                ? lowestPossibleRow
                : lowestPossibleRow - 2;

        return ActiveBrickState.builder()
                .horizontalPosition(currentHorizontalPosition)
                .verticalPosition(finalVerticalPosition)
                .landed(true)
                .build();
    }

    public static ActiveBrickState moveHorizontally(char[][] currentFrame,
                                                    int horizontalMovement,
                                                    Brick currentBrick,
                                                    int currentHorizontalPosition,
                                                    int currentVerticalPosition) {

        int finalHorizontalPosition = currentHorizontalPosition + horizontalMovement;
        Alignment currentBrickAlignment = currentBrick.getAlignment();
        char[] currentBrickSymbols = currentBrick.getSymbols();

        if (checkForHorizontalWalls(currentFrame[0].length, currentBrickAlignment, finalHorizontalPosition) ||
                checkForHorizontalCollisions(currentFrame, currentBrick, currentHorizontalPosition, currentVerticalPosition, finalHorizontalPosition)) {
            return ActiveBrickState.builder()
                    .horizontalPosition(currentHorizontalPosition)
                    .verticalPosition(currentVerticalPosition)
                    .landed(false)
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
        return ActiveBrickState.builder()
                .horizontalPosition(finalHorizontalPosition)
                .verticalPosition(currentVerticalPosition)
                .landed(false)
                .build();
    }

    public static ActiveBrickState moveDownByOneRow(char[][] currentFrame,
                                                    Brick currentBrick,
                                                    int currentHorizontalPosition,
                                                    int currentVerticalPosition) {

        if (hasLanded(currentFrame, currentBrick, currentHorizontalPosition, currentVerticalPosition)) {
            return ActiveBrickState.builder()
                    .horizontalPosition(currentHorizontalPosition)
                    .verticalPosition(currentVerticalPosition)
                    .landed(true)
                    .build();
        }

        clearExistingPosition(currentFrame, currentBrick, currentHorizontalPosition, currentVerticalPosition);

        Alignment currentBrickAlignment = currentBrick.getAlignment();
        char[] currentBrickSymbols = currentBrick.getSymbols();

        if (currentBrickAlignment.equals(Alignment.HORIZONTAL)) {
            for (int i = 0; i < currentBrickSymbols.length; i++) {
                currentFrame[currentVerticalPosition + 1][currentHorizontalPosition + i] = currentBrickSymbols[i];
            }
        } else {
            for (int i = 0; i < currentBrickSymbols.length; i++) {
                currentFrame[currentVerticalPosition + 1 + i][currentHorizontalPosition] = currentBrickSymbols[i];
            }
        }

        return ActiveBrickState.builder()
                .horizontalPosition(currentHorizontalPosition)
                .verticalPosition(currentVerticalPosition + 1)
                .landed(hasLanded(currentFrame, currentBrick, currentHorizontalPosition, currentVerticalPosition + 1))
                .build();
    }

    private static boolean hasLanded(char[][] currentFrame, Brick currentBrick, int currentHorizontalPosition, int currentVerticalPosition) {
        return checkForVerticalWalls(currentFrame.length, currentBrick.getAlignment(), currentVerticalPosition + 1)
                || checkForVerticalCollisions(currentFrame, currentBrick, currentHorizontalPosition, currentVerticalPosition, currentVerticalPosition + 1);
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


    private static boolean checkForHorizontalWalls(int boardWidth, Alignment currentBrickAlignment, int finalHorizontalPosition) {
        if (currentBrickAlignment.equals(Alignment.HORIZONTAL)) {
            return finalHorizontalPosition < 0 || finalHorizontalPosition + 2 > boardWidth - 1;
        } else {
            return finalHorizontalPosition < 0 || finalHorizontalPosition > boardWidth - 1;
        }
    }

    private static boolean checkForVerticalWalls(int boardHeight, Alignment currentBrickAlignment, int finalVerticalPosition) {
        if (currentBrickAlignment.equals(Alignment.HORIZONTAL)) {
            return finalVerticalPosition > boardHeight - 1;
        } else {
            return finalVerticalPosition + 2 > boardHeight - 1;
        }
    }

    private static boolean checkForHorizontalCollisions(char[][] currentFrame, Brick currentBrick, int currentHorizontalPosition, int currentVerticalPosition, int finalHorizontalPosition) {
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

    private static boolean checkForVerticalCollisions(char[][] currentFrame, Brick currentBrick, int currentHorizontalPosition, int currentVerticalPosition, int finalVerticalPosition) {
        char[][] tempFrameForCollisionCheck = Arrays.stream(currentFrame)
                .map(char[]::clone)
                .toArray(char[][]::new);
        clearExistingPosition(tempFrameForCollisionCheck, currentBrick, currentHorizontalPosition, currentVerticalPosition);

        if (currentBrick.getAlignment().equals(Alignment.HORIZONTAL)) {
            for (int i = currentHorizontalPosition; i < currentHorizontalPosition + 3; i++) {
                if (tempFrameForCollisionCheck[finalVerticalPosition][i] != BLANK_CELL) {
                    return true;
                }
            }
        } else {
            for (int i = finalVerticalPosition; i < finalVerticalPosition + 3; i++) {
                if (tempFrameForCollisionCheck[i][currentHorizontalPosition] != BLANK_CELL) {
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
