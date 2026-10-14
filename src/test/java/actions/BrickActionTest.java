package actions;

import model.Alignment;
import model.Brick;
import model.GameState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BrickActionTest {

    // ---------- helpers ----------

    // Adjust this one method if your Brick is created differently (e.g. a constructor).
    private static Brick brick(Alignment alignment, String symbols) {
        return Brick.builder().alignment(alignment).symbols(symbols.toCharArray()).build();
    }

    // Builds a frame from readable rows, e.g. frame(".^^*.", ".....")
    private static char[][] frame(String... rows) {
        char[][] frame = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            frame[i] = rows[i].toCharArray();
        }
        return frame;
    }

    // Turns a frame back into text so a failing assertion shows the whole grid
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

    private final Brick horizontal = brick(Alignment.HORIZONTAL, "^^*");
    private final Brick vertical = brick(Alignment.VERTICAL, "*@^");

    // ---------- moveToBottom ----------

    @Test
    void moveToBottom_horizontalBrick_dropsToFloorOnEmptyGrid() {
        char[][] frame = frame(
                ".^^*.",
                ".....",
                ".....",
                ".....");

        GameState state = BrickAction.moveToBottom(frame, horizontal, 1, 0);

        assertEquals(show(
                ".....",
                ".....",
                ".....",
                ".^^*."), show(state.getCurrentFrame()));
    }

    @Test
    void moveToBottom_verticalBrick_dropsToFloorKeepingSymbolOrder() {
        char[][] frame = frame(
                "..*..",
                "..@..",
                "..^..",
                ".....");

        GameState state = BrickAction.moveToBottom(frame, vertical, 2, 0);

        assertEquals(show(
                ".....",
                "..*..",
                "..@..",
                "..^.."), show(state.getCurrentFrame()));
    }

    @Test
    void moveToBottom_horizontalBrick_landsOnBlockUnderAnyOfItsCells() {
        char[][] frame = frame(
                ".^^*.",
                ".....",
                "...~.",
                ".....");

        GameState state = BrickAction.moveToBottom(frame, horizontal, 1, 0);

        assertEquals(show(
                ".....",
                ".^^*.",
                "...~.",
                "....."), show(state.getCurrentFrame()));
    }

    @Test
    void moveToBottom_verticalBrick_landsOnTopOfBlock() {
        char[][] frame = frame(
                "..*..",
                "..@..",
                "..^..",
                ".....",
                "..~..",
                ".....");

        GameState state = BrickAction.moveToBottom(frame, vertical, 2, 0);

        assertEquals(show(
                ".....",
                "..*..",
                "..@..",
                "..^..",
                "..~..",
                "....."), show(state.getCurrentFrame()));
    }

    @Test
    void moveToBottom_stopsAboveTheFirstBlock_whenColumnHasTwoBlocks() {
        char[][] frame = frame(
                "..*..",
                "..@..",
                "..^..",
                ".....",
                "..~..",
                ".....",
                "..~..",
                ".....");

        GameState state = BrickAction.moveToBottom(frame, vertical, 2, 0);

        assertEquals(show(
                ".....",
                "..*..",
                "..@..",
                "..^..",
                "..~..",
                ".....",
                "..~..",
                "....."), show(state.getCurrentFrame()));
    }

    @Test
    void moveToBottom_brickAlreadyOnFloor_staysWhereItIs() {
        char[][] frame = frame(
                ".....",
                ".^^*.");

        GameState state = BrickAction.moveToBottom(frame, horizontal, 1, 1);

        assertEquals(show(
                ".....",
                ".^^*."), show(state.getCurrentFrame()));
    }

    @Test
    void moveToBottom_marksTheBrickAsLanded() {
        char[][] frame = frame(
                ".^^*.",
                ".....");

        GameState state = BrickAction.moveToBottom(frame, horizontal, 1, 0);

        assertEquals(-1, state.getCurrentHorizontalPosition());
        assertEquals(-1, state.getCurrentVerticalPosition());
    }

    // ---------- moveHorizontally: normal moves ----------

    @Test
    void moveHorizontally_horizontalBrick_movesRightByOne() {
        char[][] frame = frame(
                ".^^*.",
                ".....");

        GameState state = BrickAction.moveHorizontally(frame, 1, horizontal, 1, 0);

        assertEquals(show(
                "..^^*",
                "....."), show(state.getCurrentFrame()));
        assertEquals(2, state.getCurrentHorizontalPosition());
        assertEquals(0, state.getCurrentVerticalPosition());
    }

    @Test
    void moveHorizontally_horizontalBrick_movesLeftByOne() {
        char[][] frame = frame(
                ".^^*.",
                ".....");

        GameState state = BrickAction.moveHorizontally(frame, -1, horizontal, 1, 0);

        assertEquals(show(
                "^^*..",
                "....."), show(state.getCurrentFrame()));
        assertEquals(0, state.getCurrentHorizontalPosition());
    }

    @Test
    void moveHorizontally_verticalBrick_movesRight() {
        char[][] frame = frame(
                "..*..",
                "..@..",
                "..^..",
                ".....");

        GameState state = BrickAction.moveHorizontally(frame, 1, vertical, 2, 0);

        assertEquals(show(
                "...*.",
                "...@.",
                "...^.",
                "....."), show(state.getCurrentFrame()));
        assertEquals(3, state.getCurrentHorizontalPosition());
    }

    @Test
    void moveHorizontally_verticalBrick_canReachTheLastColumn() {
        char[][] frame = frame(
                "...*.",
                "...@.",
                "...^.",
                ".....");

        GameState state = BrickAction.moveHorizontally(frame, 1, vertical, 3, 0);

        assertEquals(show(
                "....*",
                "....@",
                "....^",
                "....."), show(state.getCurrentFrame()));
        assertEquals(4, state.getCurrentHorizontalPosition());
    }

    // ---------- moveHorizontally: blocked moves ----------

    @Test
    void moveHorizontally_horizontalBrick_blockedByRightWall() {
        char[][] frame = frame(
                "..^^*",
                ".....");

        GameState state = BrickAction.moveHorizontally(frame, 1, horizontal, 2, 0);

        assertEquals(show(
                "..^^*",
                "....."), show(state.getCurrentFrame()));
        assertEquals(2, state.getCurrentHorizontalPosition());
    }

    @Test
    void moveHorizontally_horizontalBrick_blockedByLeftWall() {
        char[][] frame = frame(
                "^^*..",
                ".....");

        GameState state = BrickAction.moveHorizontally(frame, -1, horizontal, 0, 0);

        assertEquals(show(
                "^^*..",
                "....."), show(state.getCurrentFrame()));
        assertEquals(0, state.getCurrentHorizontalPosition());
    }

    @Test
    void moveHorizontally_verticalBrick_blockedByRightWall() {
        char[][] frame = frame(
                "....*",
                "....@",
                "....^",
                ".....");

        GameState state = BrickAction.moveHorizontally(frame, 1, vertical, 4, 0);

        assertEquals(show(
                "....*",
                "....@",
                "....^",
                "....."), show(state.getCurrentFrame()));
        assertEquals(4, state.getCurrentHorizontalPosition());
    }

    @Test
    void moveHorizontally_verticalBrick_blockedByLeftWall() {
        char[][] frame = frame(
                "*....",
                "@....",
                "^....",
                ".....");

        GameState state = BrickAction.moveHorizontally(frame, -1, vertical, 0, 0);

        assertEquals(0, state.getCurrentHorizontalPosition());
    }

    @Test
    void moveHorizontally_verticalBrick_blockedByBlockBesideItsLowestCell() {
        char[][] frame = frame(
                "..*..",
                "..@..",
                "..^~.",
                ".....");

        GameState state = BrickAction.moveHorizontally(frame, 1, vertical, 2, 0);

        assertEquals(show(
                "..*..",
                "..@..",
                "..^~.",
                "....."), show(state.getCurrentFrame()));
        assertEquals(2, state.getCurrentHorizontalPosition());
    }

    @Test
    void moveHorizontally_verticalBrick_blockedByBlockBesideItsMiddleCell() {
        char[][] frame = frame(
                "..*..",
                ".~@..",
                "..^..",
                ".....");

        GameState state = BrickAction.moveHorizontally(frame, -1, vertical, 2, 0);

        assertEquals(show(
                "..*..",
                ".~@..",
                "..^..",
                "....."), show(state.getCurrentFrame()));
        assertEquals(2, state.getCurrentHorizontalPosition());
    }

    @Test
    void moveHorizontally_horizontalBrick_blockedByBlockNextToIt() {
        char[][] frame = frame(
                "^^*~.",
                ".....");

        GameState state = BrickAction.moveHorizontally(frame, 1, horizontal, 0, 0);

        assertEquals(show(
                "^^*~.",
                "....."), show(state.getCurrentFrame()));
        assertEquals(0, state.getCurrentHorizontalPosition());
    }
}