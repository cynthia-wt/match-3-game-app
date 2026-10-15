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

        if (alignment.equals(Alignment.HORIZONTAL)) {
            // For a horizontal brick, the row is fixed, columns span 3 cells
            for (int col = currentHorizontalPosition; col < currentHorizontalPosition + 3; col++) {
                checkForHorizontalMatches(currentFrame, col, currentVerticalPosition);
                checkForVerticalMatches(currentFrame, currentVerticalPosition, col);
            }
        } else {
            // For a vertical brick, the column is fixed, rows span 3 cells
            for (int row = currentVerticalPosition; row < currentVerticalPosition + 3; row++) {
                checkForHorizontalMatches(currentFrame, currentHorizontalPosition, row);
                checkForVerticalMatches(currentFrame, row, currentHorizontalPosition);
            }
        }
    }

    private static void checkForHorizontalMatches(char[][] currentFrame, int col, int row) {
        int matchSymbol = currentFrame[row][col];
        if (matchSymbol == BLANK_CELL) return;

        List<Integer> matches = new ArrayList<>();
        matches.add(col); // Add center cell once

        // Left horizontal match
        int left = col - 1;
        while (left >= 0 && currentFrame[row][left] == matchSymbol) {
            matches.add(left);
            left--;
        }

        // Right horizontal match
        int right = col + 1;
        while (right < currentFrame[0].length && currentFrame[row][right] == matchSymbol) {
            matches.add(right);
            right++;
        }

        if (matches.size() >= 3) {
            for (int matchCol : matches) {
                currentFrame[row][matchCol] = BLANK_CELL;
            }
        }
    }

    private static void checkForVerticalMatches(char[][] currentFrame, int row, int col) {
        int matchSymbol = currentFrame[row][col];
        if (matchSymbol == BLANK_CELL) return;

        List<Integer> matches = new ArrayList<>();
        matches.add(row); // Add center cell once

        // Top vertical match
        int top = row - 1;
        while (top >= 0 && currentFrame[top][col] == matchSymbol) {
            matches.add(top);
            top--;
        }

        // Bottom vertical match
        int bottom = row + 1;
        while (bottom < currentFrame.length && currentFrame[bottom][col] == matchSymbol) {
            matches.add(bottom);
            bottom++;
        }

        if (matches.size() >= 3) {
            for (int matchRow : matches) {
                currentFrame[matchRow][col] = BLANK_CELL;
            }
        }
    }
}