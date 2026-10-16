package actions;

import engine.GameEngine;
import model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {

    // ---------- helpers ----------

    private static Brick brick(Alignment alignment, String symbols) {
        return Brick.builder().alignment(alignment).symbols(symbols.toCharArray()).build();
    }

    // Adjust this one method if InitializationInput is created differently.
    private static GameEngine engine(int width, int height, Brick... bricks) {
        return new GameEngine(InitializationInput.builder()
                .boardWidth(width)
                .boardHeight(height)
                .bricks(List.of(bricks))
                .build());
    }

    private static String show(char[][] frame) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : frame) {
            sb.append(new String(row)).append('\n');
        }
        return sb.toString();
    }

    private static String show(String... rows) {
        return String.join("\n", rows) + "\n";
    }

    private static void assertFrame(GameEngine engine, String... expectedRows) {
        assertEquals(show(expectedRows), show(engine.getCurrentFrame()));
    }

    private static ActiveBrick active(GameEngine engine) {
        return engine.getCurrentGameState().getActiveBrick();
    }

    private static void nextTurns(GameEngine engine, int times) {
        for (int i = 0; i < times; i++) {
            engine.nextTurn(List.of());
        }
    }

    private final Brick horizontal = brick(Alignment.HORIZONTAL, "^^*");
    private final Brick vertical = brick(Alignment.VERTICAL, "*@^");

    // ---------- initialization ----------

    @Test
    void newEngine_hasBlankBoardAndNoActiveBrick() {
        GameEngine engine = engine(5, 4, horizontal);

        assertFrame(engine,
                ".....",
                ".....",
                ".....",
                ".....");
        assertEquals(1, engine.getCurrentGameState().getFrameNumber());
        assertEquals(-1, engine.getCurrentGameState().getBrickIndex());
        assertNull(active(engine).getBrick());
        assertEquals(-1, active(engine).getHorizontalPosition());
        assertEquals(-1, active(engine).getVerticalPosition());
        assertTrue(active(engine).isLanded()); // placeholder counts as landed, so nothing moves
    }

    // ---------- nextBrick ----------

    @Test
    void nextBrick_horizontalBrick_spawnsCenteredOnTopRow() {
        GameEngine engine = engine(5, 4, horizontal);

        engine.nextBrick();

        assertFrame(engine,
                ".^^*.",
                ".....",
                ".....",
                ".....");
        assertEquals(0, engine.getCurrentGameState().getBrickIndex());
        assertSame(horizontal, active(engine).getBrick());
        assertEquals(1, active(engine).getHorizontalPosition());
        assertEquals(0, active(engine).getVerticalPosition());
        assertFalse(active(engine).isLanded());
    }

    @Test
    void nextBrick_verticalBrick_spawnsCenteredInTopThreeRows() {
        GameEngine engine = engine(5, 4, vertical);

        engine.nextBrick();

        assertFrame(engine,
                "..*..",
                "..@..",
                "..^..",
                ".....");
        assertSame(vertical, active(engine).getBrick());
        assertEquals(2, active(engine).getHorizontalPosition());
        assertEquals(0, active(engine).getVerticalPosition());
    }

    // ---------- nextTurn: commands + gravity ----------

    @Test
    void nextTurn_noCommands_brickDropsOneRow() {
        GameEngine engine = engine(5, 4, horizontal);
        engine.nextBrick();

        engine.nextTurn(List.of());

        assertFrame(engine,
                ".....",
                ".^^*.",
                ".....",
                ".....");
        assertEquals(1, active(engine).getHorizontalPosition());
        assertEquals(1, active(engine).getVerticalPosition());
        assertFalse(active(engine).isLanded());
    }

    @Test
    void nextTurn_leftCommand_movesLeftThenDrops() {
        GameEngine engine = engine(5, 4, horizontal);
        engine.nextBrick();

        engine.nextTurn(List.of(Command.LEFT));

        assertFrame(engine,
                ".....",
                "^^*..",
                ".....",
                ".....");
        assertEquals(0, active(engine).getHorizontalPosition());
        assertEquals(1, active(engine).getVerticalPosition());
    }

    @Test
    void nextTurn_rightCommand_movesRightThenDrops() {
        GameEngine engine = engine(5, 4, horizontal);
        engine.nextBrick();

        engine.nextTurn(List.of(Command.RIGHT));

        assertFrame(engine,
                ".....",
                "..^^*",
                ".....",
                ".....");
        assertEquals(2, active(engine).getHorizontalPosition());
        assertEquals(1, active(engine).getVerticalPosition());
    }

    @Test
    void nextTurn_multipleCommands_areAppliedInOrder() {
        GameEngine engine = engine(7, 4, horizontal); // spawns at column 2
        engine.nextBrick();

        engine.nextTurn(List.of(Command.LEFT, Command.RIGHT, Command.RIGHT));

        assertFrame(engine,
                ".......",
                "...^^*.",
                ".......",
                ".......");
        assertEquals(3, active(engine).getHorizontalPosition());
    }

    @Test
    void nextTurn_twoRightCommands_moveTwoColumns() {
        // regression: each command must start from the result of the previous one
        GameEngine engine = engine(5, 8, horizontal);
        engine.nextBrick();

        engine.nextTurn(List.of(Command.LEFT)); // x=0, y=1
        engine.nextTurn(List.of());             // y=2
        engine.nextTurn(List.of(Command.RIGHT, Command.RIGHT));

        assertFrame(engine,
                ".....",
                ".....",
                ".....",
                "..^^*",
                ".....",
                ".....",
                ".....",
                ".....");
        assertEquals(2, active(engine).getHorizontalPosition());
        assertEquals(3, active(engine).getVerticalPosition());
    }

    @Test
    void nextTurn_commandBlockedByWall_brickStillDrops() {
        GameEngine engine = engine(5, 4, horizontal);
        engine.nextBrick();

        engine.nextTurn(List.of(Command.RIGHT, Command.RIGHT)); // second one hits the wall

        assertFrame(engine,
                ".....",
                "..^^*",
                ".....",
                ".....");
        assertEquals(2, active(engine).getHorizontalPosition());
        assertEquals(1, active(engine).getVerticalPosition());
    }

    // ---------- nextTurn: falling and landing ----------

    @Test
    void nextTurn_repeatedly_verticalBrickFallsThenLandsOnFloor() {
        GameEngine engine = engine(5, 5, vertical);
        engine.nextBrick();

        nextTurns(engine, 1);

        assertFrame(engine,
                ".....",
                "..*..",
                "..@..",
                "..^..",
                ".....");
        assertFalse(active(engine).isLanded());

        nextTurns(engine, 1); // reaches the floor, lands in the same turn

        assertFrame(engine,
                ".....",
                ".....",
                "..*..",
                "..@..",
                "..^..");
        assertEquals(2, active(engine).getHorizontalPosition());
        assertEquals(2, active(engine).getVerticalPosition());
        assertTrue(active(engine).isLanded());

        nextTurns(engine, 1); // landed: gravity is skipped

        assertFrame(engine,
                ".....",
                ".....",
                "..*..",
                "..@..",
                "..^..");
        assertEquals(2, active(engine).getVerticalPosition());
        assertTrue(active(engine).isLanded());
    }

    @Test
    void nextTurn_secondBrick_landsOnTopOfFirstBrick() {
        Brick second = brick(Alignment.HORIZONTAL, "abc");
        GameEngine engine = engine(5, 4, horizontal, second);

        engine.nextBrick();
        nextTurns(engine, 3); // rows 1, 2, 3 (floor)
        assertTrue(active(engine).isLanded());
        assertEquals(3, active(engine).getVerticalPosition());

        engine.nextBrick();
        assertFalse(active(engine).isLanded()); // new brick starts fresh
        assertSame(second, active(engine).getBrick());
        nextTurns(engine, 2); // rows 1, 2 (rests on the first brick)

        assertFrame(engine,
                ".....",
                ".....",
                ".abc.",
                ".^^*.");
        assertEquals(1, engine.getCurrentGameState().getBrickIndex());
        assertTrue(active(engine).isLanded());
        assertEquals(2, active(engine).getVerticalPosition());
    }

    @Test
    void nextTurn_downCommand_dropsBrickToFloor() {
        GameEngine engine = engine(5, 4, horizontal);
        engine.nextBrick();

        engine.nextTurn(List.of(Command.DOWN));

        assertFrame(engine,
                ".....",
                ".....",
                ".....",
                ".^^*.");
        assertEquals(1, active(engine).getHorizontalPosition());
        assertEquals(3, active(engine).getVerticalPosition());
        assertTrue(active(engine).isLanded());
    }

    @Test
    void nextTurn_commandsAfterDown_areIgnored() {
        GameEngine engine = engine(7, 4, horizontal); // spawns at column 2
        engine.nextBrick();

        engine.nextTurn(List.of(Command.DOWN, Command.RIGHT));

        assertFrame(engine,
                ".......",
                ".......",
                ".......",
                "..^^*..");
        assertEquals(2, active(engine).getHorizontalPosition());
        assertTrue(active(engine).isLanded());
    }

    // ---------- matches ----------

    @Test
    void nextTurn_horizontalMatchOfThree_isClearedWhenBrickLands() {
        Brick matching = brick(Alignment.HORIZONTAL, "aaa");
        GameEngine engine = engine(5, 4, matching);
        engine.nextBrick();

        nextTurns(engine, 2); // still falling: nothing cleared
        assertFrame(engine,
                ".....",
                ".....",
                ".aaa.",
                ".....");

        nextTurns(engine, 1); // lands on the floor and matches

        assertFrame(engine,
                ".....",
                ".....",
                ".....",
                ".....");
        assertTrue(active(engine).isLanded());
    }

    @Test
    void nextTurn_verticalMatchOfThree_isClearedWhenBrickLands() {
        Brick matching = brick(Alignment.VERTICAL, "aaa");
        GameEngine engine = engine(5, 4, matching);

        engine.nextBrick();
        engine.nextTurn(List.of(Command.DOWN));

        assertFrame(engine,
                ".....",
                ".....",
                ".....",
                ".....");
    }

    @Test
    void nextTurn_landedBrickWithoutMatch_staysOnBoard() {
        GameEngine engine = engine(5, 4, horizontal);
        engine.nextBrick();

        engine.nextTurn(List.of(Command.DOWN));

        assertFrame(engine,
                ".....",
                ".....",
                ".....",
                ".^^*.");
    }

    // ---------- hasGameEnded ----------

    @Test
    void hasGameEnded_beforeFirstBrick_isFalseWhenBricksAreAvailable() {
        GameEngine engine = engine(5, 4, horizontal);

        assertFalse(engine.hasGameEnded());
    }

    @Test
    void hasGameEnded_whenAllBricksAreUsed_isTrue() {
        GameEngine engine = engine(5, 4, horizontal);
        engine.nextBrick();

        assertTrue(engine.hasGameEnded());
    }

    @Test
    void hasGameEnded_nextBrickHasRoomToSpawn_isFalse() {
        GameEngine engine = engine(5, 4, horizontal, brick(Alignment.HORIZONTAL, "abc"));
        engine.nextBrick();
        nextTurns(engine, 3); // first brick lands on the floor

        assertFalse(engine.hasGameEnded());
    }

    @Test
    void hasGameEnded_horizontalSpawnAreaBlocked_isTrue() {
        GameEngine engine = engine(5, 4, horizontal, brick(Alignment.HORIZONTAL, "abc"));
        engine.nextBrick(); // first brick still sits on the top row

        assertTrue(engine.hasGameEnded());
    }

    @Test
    void hasGameEnded_verticalSpawnColumnBlocked_isTrue() {
        GameEngine engine = engine(5, 4, vertical, brick(Alignment.VERTICAL, "xyz"));
        engine.nextBrick(); // first brick occupies the spawn column from row 0

        assertTrue(engine.hasGameEnded());
    }

    // ---------- frame counter ----------

    @Test
    void nextTurn_incrementsFrameNumberEachTurn() {
        GameEngine engine = engine(5, 4, horizontal);
        engine.nextBrick();
        assertEquals(1, engine.getCurrentGameState().getFrameNumber());

        nextTurns(engine, 2);

        assertEquals(3, engine.getCurrentGameState().getFrameNumber());
    }

    @Test
    void hasGameEnded_verticalSpawnBlockedBelowTopRow_isTrue() {
        // 2nd cell of the spawn column is occupied, but row 0 is blank
        GameEngine engine = engine(5, 5, brick(Alignment.HORIZONTAL, "abc"), vertical);
        engine.nextBrick();
        nextTurns(engine, 1);   // "abc" now on row 1, columns 1-3 -> blocks (1,2)

        assertTrue(engine.hasGameEnded());
    }
}