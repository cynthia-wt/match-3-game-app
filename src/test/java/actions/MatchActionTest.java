package actions;

import model.ActiveBrick;
import model.Alignment;
import model.Brick;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchActionTest {

    // ---------- helpers ----------

    private static Brick brick(Alignment alignment, String symbols) {
        return Brick.builder().alignment(alignment).symbols(symbols.toCharArray()).build();
    }

    private static ActiveBrick landed(Brick brick, int x, int y) {
        return ActiveBrick.builder()
                .brick(brick)
                .horizontalPosition(x)
                .verticalPosition(y)
                .landed(true)
                .build();
    }

    private static char[][] frame(String... rows) {
        char[][] frame = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            frame[i] = rows[i].toCharArray();
        }
        return frame;
    }

    private static String show(char[][] frame) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : frame) {
            sb.append(new String(row)).append('\n');
        }
        return sb.toString();
    }

    private static String show(String... rows) {
        return show(frame(rows));
    }

    private final Brick horizontalAaa = brick(Alignment.HORIZONTAL, "aaa");
    private final Brick verticalAaa = brick(Alignment.VERTICAL, "aaa");

    // ---------- horizontal brick ----------

    @Test
    void horizontalBrick_threeSameSymbols_clearsTheRow() {
        char[][] frame = frame(
                ".....",
                ".aaa.");

        MatchAction.checkForMatches(frame, landed(horizontalAaa, 1, 1));

        assertEquals(show(
                ".....",
                "....."), show(frame));
    }

    @Test
    void horizontalBrick_matchAgainstLeftWall_isCleared() {
        char[][] frame = frame(
                "aaa..",
                ".....");

        MatchAction.checkForMatches(frame, landed(horizontalAaa, 0, 0));

        assertEquals(show(
                ".....",
                "....."), show(frame));
    }

    @Test
    void horizontalBrick_matchAgainstRightWall_isCleared() {
        char[][] frame = frame(
                "..aaa",
                ".....");

        MatchAction.checkForMatches(frame, landed(horizontalAaa, 2, 0));

        assertEquals(show(
                ".....",
                "....."), show(frame));
    }

    @Test
    void horizontalBrick_matchExtendsIntoCellsOnItsLeft_clearsThemAll() {
        // brick "a*^" at x=2, with two 'a' already to its left
        char[][] frame = frame(
                "aaa*^",
                ".....");

        MatchAction.checkForMatches(frame, landed(brick(Alignment.HORIZONTAL, "a*^"), 2, 0));

        assertEquals(show(
                "...*^",
                "....."), show(frame));
    }

    @Test
    void horizontalBrick_matchExtendsOnBothSides_clearsAllFive() {
        char[][] frame = frame(
                "aaaaa",
                ".....");

        MatchAction.checkForMatches(frame, landed(horizontalAaa, 1, 0));

        assertEquals(show(
                ".....",
                "....."), show(frame));
    }

    @Test
    void horizontalBrick_onlyTwoInARow_isNotCleared() {
        // brick "a*^" at x=1, with one 'a' next to it: only two in a row
        char[][] frame = frame(
                "aa*^.",
                ".....");

        MatchAction.checkForMatches(frame, landed(brick(Alignment.HORIZONTAL, "a*^"), 1, 0));

        assertEquals(show(
                "aa*^.",
                "....."), show(frame));
    }

    @Test
    void horizontalBrick_noMatch_leavesFrameUnchanged() {
        char[][] frame = frame(
                ".^^*.",
                ".....");

        MatchAction.checkForMatches(frame, landed(brick(Alignment.HORIZONTAL, "^^*"), 1, 0));

        assertEquals(show(
                ".^^*.",
                "....."), show(frame));
    }

    @Test
    void horizontalBrick_middleCellCompletesAVerticalMatch() {
        // brick "*a^" at x=1, row 2; column 2 has 'a' in rows 0-2
        char[][] frame = frame(
                "..a..",
                "..a..",
                ".*a^.");

        MatchAction.checkForMatches(frame, landed(brick(Alignment.HORIZONTAL, "*a^"), 1, 2));

        assertEquals(show(
                ".....",
                ".....",
                ".*.^."), show(frame));
    }

    // ---------- vertical brick ----------

    @Test
    void verticalBrick_threeSameSymbolsAtTheBottom_clearsTheColumn() {
        char[][] frame = frame(
                ".....",
                "..a..",
                "..a..",
                "..a..");

        MatchAction.checkForMatches(frame, landed(verticalAaa, 2, 1));

        assertEquals(show(
                ".....",
                ".....",
                ".....",
                "....."), show(frame));
    }

    @Test
    void verticalBrick_matchExtendsAboveTheBrick_clearsAllFour() {
        char[][] frame = frame(
                "..a..",
                "..a..",
                "..a..",
                "..a..");

        MatchAction.checkForMatches(frame, landed(verticalAaa, 2, 1));

        assertEquals(show(
                ".....",
                ".....",
                ".....",
                "....."), show(frame));
    }

    @Test
    void verticalBrick_middleCellCompletesAHorizontalMatch() {
        // brick "*a^" at column 2; row 1 has 'a' at columns 0 and 1
        char[][] frame = frame(
                "..*..",
                "aaa..",
                "..^..");

        MatchAction.checkForMatches(frame, landed(brick(Alignment.VERTICAL, "*a^"), 2, 0));

        assertEquals(show(
                "..*..",
                ".....",
                "..^.."), show(frame));
    }

    @Test
    void verticalBrick_noMatch_leavesFrameUnchanged() {
        char[][] frame = frame(
                "..*..",
                "..@..",
                "..^..");

        MatchAction.checkForMatches(frame, landed(brick(Alignment.VERTICAL, "*@^"), 2, 0));

        assertEquals(show(
                "..*..",
                "..@..",
                "..^.."), show(frame));
    }

    // ---------- scope ----------

    @Test
    void matchesNotTouchingTheLandedBrick_areLeftAlone() {
        char[][] frame = frame(
                "bbb..",
                ".....",
                ".^^*.");

        MatchAction.checkForMatches(frame, landed(brick(Alignment.HORIZONTAL, "^^*"), 1, 2));

        assertEquals(show(
                "bbb..",
                ".....",
                ".^^*."), show(frame));
    }

    // ---------- both directions at once ----------

    @Test
    void verticalBrick_matchingInBothDirections_clearsBothLines() {
        // column 2 is a vertical line of three, and row 1 is a horizontal line of three
        char[][] frame = frame(
                "..a..",
                "aaa..",
                "..a..",
                ".....");

        MatchAction.checkForMatches(frame, landed(verticalAaa, 2, 0));

        assertEquals(show(
                ".....",
                ".....",
                ".....",
                "....."), show(frame));
    }
}