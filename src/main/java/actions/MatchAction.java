package actions;

import model.ActiveBrick;
import model.Alignment;

import java.util.ArrayList;
import java.util.List;

import static engine.GameEngine.BLANK_CELL;

public class MatchAction {

    public static void checkForMatches(char[][] currentFrame, ActiveBrick activeBrick) {
        Alignment alignment = activeBrick.getBrick().getAlignment();
        int currentHorizontalPosition = activeBrick.getHorizontalPosition();
        int currentVerticalPosition = activeBrick.getVerticalPosition();
        List<int[]> horizontalMatches = new ArrayList<>();
        List<int[]> verticalMatches = new ArrayList<>();

        if (alignment.equals(Alignment.HORIZONTAL)) {
            // For a horizontal brick, the row is fixed, columns span 3 cells
            for (int col = currentHorizontalPosition; col < currentHorizontalPosition + 3; col++) {
                horizontalMatches.addAll(checkForHorizontalMatches(currentFrame, col, currentVerticalPosition));
                verticalMatches.addAll(checkForVerticalMatches(currentFrame, currentVerticalPosition, col));
            }
        } else {
            // For a vertical brick, the column is fixed, rows span 3 cells
            for (int row = currentVerticalPosition; row < currentVerticalPosition + 3; row++) {
                horizontalMatches.addAll(checkForHorizontalMatches(currentFrame, currentHorizontalPosition, row));
                verticalMatches.addAll(checkForVerticalMatches(currentFrame, row, currentHorizontalPosition));
            }
        }

        clearHorizontalMatches(currentFrame, horizontalMatches);
        clearVerticalMatches(currentFrame, verticalMatches);
    }

    private static List<int[]> checkForHorizontalMatches(char[][] currentFrame, int col, int row) {
        int matchSymbol = currentFrame[row][col];

        List<int[]> matches = new ArrayList<>();
        matches.add(new int[]{row, col}); // Add center cell once

        // Left horizontal match
        int left = col - 1;
        while (left >= 0 && currentFrame[row][left] == matchSymbol) {
            matches.add(new int[]{row, left});
            left--;
        }

        // Right horizontal match
        int right = col + 1;
        while (right < currentFrame[0].length && currentFrame[row][right] == matchSymbol) {
            matches.add(new int[]{row, right});
            right++;
        }

        if (matches.size() >= 3) {
            return matches;
        } else {
            return List.of();
        }
    }

    private static List<int[]> checkForVerticalMatches(char[][] currentFrame, int row, int col) {
        int matchSymbol = currentFrame[row][col];

        List<int[]> matches = new ArrayList<>();
        matches.add(new int[]{row, col}); // Add center cell once

        // Top vertical match
        int top = row - 1;
        while (top >= 0 && currentFrame[top][col] == matchSymbol) {
            matches.add(new int[]{top, col});
            top--;
        }

        // Bottom vertical match
        int bottom = row + 1;
        while (bottom < currentFrame.length && currentFrame[bottom][col] == matchSymbol) {
            matches.add(new int[]{bottom, col});
            bottom++;
        }

        if (matches.size() >= 3) {
            return matches;
        } else {
            return List.of();
        }
    }

    private static void clearHorizontalMatches(char[][] currentFrame, List<int[]> matches) {
        for (int[] matchCell : matches) {
            currentFrame[matchCell[0]][matchCell[1]] = BLANK_CELL;
        }
    }

    private static void clearVerticalMatches(char[][] currentFrame, List<int[]> matches) {
        for (int[] matchCell : matches) {
            currentFrame[matchCell[0]][matchCell[1]] = BLANK_CELL;
        }
    }
}