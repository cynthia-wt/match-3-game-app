package actions;

import model.ActiveBrick;
import model.Alignment;
import model.Brick;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BrickActionTest {

    // ---------- helpers ----------

    // Adjust this one method if your Brick is created differently (e.g. a constructor).
    private static Brick brick(Alignment alignment, String symbols) {
        return Brick.builder().alignment(alignment).symbols(symbols.toCharArray()).build();
    }

    private static ActiveBrick activeBrick(Brick brick, int x, int y) {
        return ActiveBrick.builder()
                .brick(brick)
                .horizontalPosition(x)
                .verticalPosition(y)
                .landed(false)
                .build();
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

        ActiveBrick state = BrickAction.moveToBottom(frame, activeBrick(horizontal, 1, 0));

        assertEquals(show(
                ".....",
                ".....",
                ".....",
                ".^^*."), show(frame));
        assertEquals(1, state.getHorizontalPosition());
        assertEquals(3, state.getVerticalPosition());
    }

    @Test
    void moveToBottom_verticalBrick_dropsToFloorKeepingSymbolOrder() {
        char[][] frame = frame(
                "..*..",
                "..@..",
                "..^..",
                ".....");

        ActiveBrick state = BrickAction.moveToBottom(frame, activeBrick(vertical, 2, 0));

        assertEquals(show(
                ".....",
                "..*..",
                "..@..",
                "..^.."), show(frame));
        assertEquals(2, state.getHorizontalPosition());
        assertEquals(1, state.getVerticalPosition()); // top row of the brick
        assertSame(vertical, state.getBrick());
    }

    @Test
    void moveToBottom_horizontalBrick_landsOnBlockUnderAnyOfItsCells() {
        char[][] frame = frame(
                ".^^*.",
                ".....",
                "...~.",
                ".....");

        ActiveBrick state = BrickAction.moveToBottom(frame, activeBrick(horizontal, 1, 0));

        assertEquals(show(
                ".....",
                ".^^*.",
                "...~.",
                "....."), show(frame));
        assertEquals(1, state.getHorizontalPosition());
        assertEquals(1, state.getVerticalPosition());
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

        ActiveBrick state = BrickAction.moveToBottom(frame, activeBrick(vertical, 2, 0));

        assertEquals(show(
                ".....",
                "..*..",
                "..@..",
                "..^..",
                "..~..",
                "....."), show(frame));
        assertEquals(2, state.getHorizontalPosition());
        assertEquals(1, state.getVerticalPosition());
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

        ActiveBrick state = BrickAction.moveToBottom(frame, activeBrick(vertical, 2, 0));

        assertEquals(show(
                ".....",
                "..*..",
                "..@..",
                "..^..",
                "..~..",
                ".....",
                "..~..",
                "....."), show(frame));
        assertEquals(1, state.getVerticalPosition());
    }

    @Test
    void moveToBottom_brickAlreadyOnFloor_staysWhereItIs() {
        char[][] frame = frame(
                ".....",
                ".^^*.");

        ActiveBrick state = BrickAction.moveToBottom(frame, activeBrick(horizontal, 1, 1));

        assertEquals(show(
                ".....",
                ".^^*."), show(frame));
        assertEquals(1, state.getHorizontalPosition());
        assertEquals(1, state.getVerticalPosition());
    }

    @Test
    void moveToBottom_marksTheBrickAsLanded() {
        char[][] frame = frame(
                ".^^*.",
                ".....");

        ActiveBrick state = BrickAction.moveToBottom(frame, activeBrick(horizontal, 1, 0));

        assertTrue(state.isLanded());
    }

    @Test
    void moveToBottom_horizontalBrick_returnsTheLandedPosition() {
        char[][] frame = frame(
                ".^^*.",
                ".....");

        ActiveBrick state = BrickAction.moveToBottom(frame, activeBrick(horizontal, 1, 0));

        assertEquals(1, state.getHorizontalPosition());
        assertEquals(1, state.getVerticalPosition());
    }

    @Test
    void moveToBottom_verticalBrick_returnsTheTopRowOfTheLandedBrick() {
        char[][] frame = frame(
                "..*..",
                "..@..",
                "..^..",
                ".....");

        ActiveBrick state = BrickAction.moveToBottom(frame, activeBrick(vertical, 2, 0));

        assertEquals(2, state.getHorizontalPosition());
        assertEquals(1, state.getVerticalPosition()); // occupies rows 1-3
    }

    // ---------- moveHorizontally: normal moves ----------

    @Test
    void moveHorizontally_horizontalBrick_movesRightByOne() {
        char[][] frame = frame(
                ".^^*.",
                ".....");

        ActiveBrick state = BrickAction.moveHorizontally(frame, 1, activeBrick(horizontal, 1, 0));

        assertEquals(show(
                "..^^*",
                "....."), show(frame));
        assertEquals(2, state.getHorizontalPosition());
        assertEquals(0, state.getVerticalPosition());
        assertFalse(state.isLanded());
        assertSame(horizontal, state.getBrick());
    }

    @Test
    void moveHorizontally_horizontalBrick_movesLeftByOne() {
        char[][] frame = frame(
                ".^^*.",
                ".....");

        ActiveBrick state = BrickAction.moveHorizontally(frame, -1, activeBrick(horizontal, 1, 0));

        assertEquals(show(
                "^^*..",
                "....."), show(frame));
        assertEquals(0, state.getHorizontalPosition());
    }

    @Test
    void moveHorizontally_verticalBrick_movesRight() {
        char[][] frame = frame(
                "..*..",
                "..@..",
                "..^..",
                ".....");

        ActiveBrick state = BrickAction.moveHorizontally(frame, 1, activeBrick(vertical, 2, 0));

        assertEquals(show(
                "...*.",
                "...@.",
                "...^.",
                "....."), show(frame));
        assertEquals(3, state.getHorizontalPosition());
    }

    @Test
    void moveHorizontally_verticalBrick_canReachTheLastColumn() {
        char[][] frame = frame(
                "...*.",
                "...@.",
                "...^.",
                ".....");

        ActiveBrick state = BrickAction.moveHorizontally(frame, 1, activeBrick(vertical, 3, 0));

        assertEquals(show(
                "....*",
                "....@",
                "....^",
                "....."), show(frame));
        assertEquals(4, state.getHorizontalPosition());
    }

    // ---------- moveHorizontally: blocked moves ----------

    @Test
    void moveHorizontally_horizontalBrick_blockedByRightWall() {
        char[][] frame = frame(
                "..^^*",
                ".....");

        ActiveBrick state = BrickAction.moveHorizontally(frame, 1, activeBrick(horizontal, 2, 0));

        assertEquals(show(
                "..^^*",
                "....."), show(frame));
        assertEquals(2, state.getHorizontalPosition());
        assertEquals(0, state.getVerticalPosition());
        assertFalse(state.isLanded());
    }

    @Test
    void moveHorizontally_horizontalBrick_blockedByLeftWall() {
        char[][] frame = frame(
                "^^*..",
                ".....");

        ActiveBrick state = BrickAction.moveHorizontally(frame, -1, activeBrick(horizontal, 0, 0));

        assertEquals(show(
                "^^*..",
                "....."), show(frame));
        assertEquals(0, state.getHorizontalPosition());
    }

    @Test
    void moveHorizontally_verticalBrick_blockedByRightWall() {
        char[][] frame = frame(
                "....*",
                "....@",
                "....^",
                ".....");

        ActiveBrick state = BrickAction.moveHorizontally(frame, 1, activeBrick(vertical, 4, 0));

        assertEquals(show(
                "....*",
                "....@",
                "....^",
                "....."), show(frame));
        assertEquals(4, state.getHorizontalPosition());
    }

    @Test
    void moveHorizontally_verticalBrick_blockedByLeftWall() {
        char[][] frame = frame(
                "*....",
                "@....",
                "^....",
                ".....");

        ActiveBrick state = BrickAction.moveHorizontally(frame, -1, activeBrick(vertical, 0, 0));

        assertEquals(0, state.getHorizontalPosition());
    }

    @Test
    void moveHorizontally_verticalBrick_blockedByBlockBesideItsLowestCell() {
        char[][] frame = frame(
                "..*..",
                "..@..",
                "..^~.",
                ".....");

        ActiveBrick state = BrickAction.moveHorizontally(frame, 1, activeBrick(vertical, 2, 0));

        assertEquals(show(
                "..*..",
                "..@..",
                "..^~.",
                "....."), show(frame));
        assertEquals(2, state.getHorizontalPosition());
    }

    @Test
    void moveHorizontally_verticalBrick_blockedByBlockBesideItsMiddleCell() {
        char[][] frame = frame(
                "..*..",
                ".~@..",
                "..^..",
                ".....");

        ActiveBrick state = BrickAction.moveHorizontally(frame, -1, activeBrick(vertical, 2, 0));

        assertEquals(show(
                "..*..",
                ".~@..",
                "..^..",
                "....."), show(frame));
        assertEquals(2, state.getHorizontalPosition());
    }

    @Test
    void moveHorizontally_horizontalBrick_blockedByBlockNextToIt() {
        char[][] frame = frame(
                "^^*~.",
                ".....");

        ActiveBrick state = BrickAction.moveHorizontally(frame, 1, activeBrick(horizontal, 0, 0));

        assertEquals(show(
                "^^*~.",
                "....."), show(frame));
        assertEquals(0, state.getHorizontalPosition());
    }

    // ---------- moveDownByOneRow: moves that leave the brick in the air ----------

    @Test
    void moveDownByOneRow_horizontalBrick_movesDownOneRow() {
        char[][] frame = frame(
                ".^^*.",
                ".....",
                ".....");

        ActiveBrick state = BrickAction.moveDownByOneRow(frame, activeBrick(horizontal, 1, 0));

        assertEquals(show(
                ".....",
                ".^^*.",
                "....."), show(frame));
        assertEquals(1, state.getHorizontalPosition());
        assertEquals(1, state.getVerticalPosition());
        assertFalse(state.isLanded());
        assertSame(horizontal, state.getBrick());
    }

    @Test
    void moveDownByOneRow_verticalBrick_movesDownOneRowKeepingSymbolOrder() {
        char[][] frame = frame(
                "..*..",
                "..@..",
                "..^..",
                ".....",
                ".....");

        ActiveBrick state = BrickAction.moveDownByOneRow(frame, activeBrick(vertical, 2, 0));

        assertEquals(show(
                ".....",
                "..*..",
                "..@..",
                "..^..",
                "....."), show(frame));
        assertEquals(2, state.getHorizontalPosition());
        assertEquals(1, state.getVerticalPosition());
        assertFalse(state.isLanded());
    }

    // ---------- moveDownByOneRow: moves that land the brick ----------

    @Test
    void moveDownByOneRow_horizontalBrick_canReachTheLastRowAndLandsThere() {
        char[][] frame = frame(
                ".^^*.",
                ".....");

        ActiveBrick state = BrickAction.moveDownByOneRow(frame, activeBrick(horizontal, 1, 0));

        assertEquals(show(
                ".....",
                ".^^*."), show(frame));
        assertEquals(1, state.getVerticalPosition());
        assertTrue(state.isLanded());
    }

    @Test
    void moveDownByOneRow_verticalBrick_landsImmediatelyWhenItReachesTheFloor() {
        char[][] frame = frame(
                "..*..",
                "..@..",
                "..^..",
                ".....");

        ActiveBrick state = BrickAction.moveDownByOneRow(frame, activeBrick(vertical, 2, 0));

        assertEquals(show(
                ".....",
                "..*..",
                "..@..",
                "..^.."), show(frame));
        assertEquals(1, state.getVerticalPosition());
        assertTrue(state.isLanded());
    }

    @Test
    void moveDownByOneRow_horizontalBrick_movesThenLandsOnBlockTwoRowsBelow() {
        char[][] frame = frame(
                ".^^*.",
                ".....",
                "...~.");

        ActiveBrick state = BrickAction.moveDownByOneRow(frame, activeBrick(horizontal, 1, 0));

        assertEquals(show(
                ".....",
                ".^^*.",
                "...~."), show(frame));
        assertEquals(1, state.getVerticalPosition());
        assertTrue(state.isLanded());
    }

    @Test
    void moveDownByOneRow_verticalBrick_notBlockedByBlockBesideIt() {
        char[][] frame = frame(
                "..*~.",
                "..@..",
                "..^..",
                ".....");

        ActiveBrick state = BrickAction.moveDownByOneRow(frame, activeBrick(vertical, 2, 0));

        assertEquals(show(
                "...~.",
                "..*..",
                "..@..",
                "..^.."), show(frame));
        assertEquals(1, state.getVerticalPosition());
        assertTrue(state.isLanded()); // reached the floor
    }

    @Test
    void moveDownByOneRow_horizontalBrick_notBlockedByBlocksOutsideItsColumns() {
        char[][] frame = frame(
                ".^^*.",
                "~...~");

        ActiveBrick state = BrickAction.moveDownByOneRow(frame, activeBrick(horizontal, 1, 0));

        assertEquals(show(
                ".....",
                "~^^*~"), show(frame));
        assertEquals(1, state.getVerticalPosition());
        assertTrue(state.isLanded()); // reached the floor
    }

    // ---------- moveDownByOneRow: already landed (no move, position unchanged) ----------

    @Test
    void moveDownByOneRow_verticalBrick_alreadyOnFloor_staysPut() {
        char[][] frame = frame(
                "..*..",
                "..@..",
                "..^..");

        ActiveBrick state = BrickAction.moveDownByOneRow(frame, activeBrick(vertical, 2, 0));

        assertEquals(show(
                "..*..",
                "..@..",
                "..^.."), show(frame));
        assertEquals(2, state.getHorizontalPosition());
        assertEquals(0, state.getVerticalPosition());
        assertTrue(state.isLanded());
        assertSame(vertical, state.getBrick());
    }

    @Test
    void moveDownByOneRow_horizontalBrick_alreadyOnFloor_staysPut() {
        char[][] frame = frame(
                ".....",
                ".^^*.");

        ActiveBrick state = BrickAction.moveDownByOneRow(frame, activeBrick(horizontal, 1, 1));

        assertEquals(show(
                ".....",
                ".^^*."), show(frame));
        assertEquals(1, state.getHorizontalPosition());
        assertEquals(1, state.getVerticalPosition());
        assertTrue(state.isLanded());
    }

    @Test
    void moveDownByOneRow_horizontalBrick_blockedByBlockUnderItsLeftCell() {
        char[][] frame = frame(
                ".^^*.",
                ".~...",
                ".....");

        ActiveBrick state = BrickAction.moveDownByOneRow(frame, activeBrick(horizontal, 1, 0));

        assertEquals(show(
                ".^^*.",
                ".~...",
                "....."), show(frame));
        assertEquals(1, state.getHorizontalPosition());
        assertEquals(0, state.getVerticalPosition());
        assertTrue(state.isLanded());
    }

    @Test
    void moveDownByOneRow_horizontalBrick_blockedByBlockUnderItsRightCell() {
        char[][] frame = frame(
                ".^^*.",
                "...~.",
                ".....");

        ActiveBrick state = BrickAction.moveDownByOneRow(frame, activeBrick(horizontal, 1, 0));

        assertEquals(show(
                ".^^*.",
                "...~.",
                "....."), show(frame));
        assertEquals(1, state.getHorizontalPosition());
        assertEquals(0, state.getVerticalPosition());
        assertTrue(state.isLanded());
    }

    @Test
    void moveDownByOneRow_verticalBrick_blockedByBlockUnderItsLowestCell() {
        char[][] frame = frame(
                "..*..",
                "..@..",
                "..^..",
                "..~..");

        ActiveBrick state = BrickAction.moveDownByOneRow(frame, activeBrick(vertical, 2, 0));

        assertEquals(show(
                "..*..",
                "..@..",
                "..^..",
                "..~.."), show(frame));
        assertEquals(2, state.getHorizontalPosition());
        assertEquals(0, state.getVerticalPosition());
        assertTrue(state.isLanded());
    }
}