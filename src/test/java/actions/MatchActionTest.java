package actions;

import model.ActiveBrick;
import model.Alignment;
import model.Brick;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchActionTest {

    private static final char BLANK = '.';

    @Test
    void testHorizontalMatchFound() {
        char[][] frame = {
                {BLANK, BLANK, BLANK, BLANK, BLANK},
                {BLANK, BLANK, BLANK, BLANK, BLANK},
                {BLANK, 'A', 'A', 'A', BLANK},
                {BLANK, BLANK, BLANK, BLANK, BLANK},
                {BLANK, BLANK, BLANK, BLANK, BLANK}
        };

        Brick brick = Brick.builder()
                .alignment(Alignment.HORIZONTAL)
                .build();

        ActiveBrick activeBrick = ActiveBrick.builder()
                .brick(brick)
                .horizontalPosition(1)
                .verticalPosition(2)
                .build();

        MatchAction.checkForMatches(frame, activeBrick);

        assertEquals(BLANK, frame[2][1]);
        assertEquals(BLANK, frame[2][2]);
        assertEquals(BLANK, frame[2][3]);
    }

    @Test
    void testVerticalMatchFound() {
        char[][] frame = {
                {BLANK, BLANK, BLANK, BLANK, BLANK},
                {BLANK, BLANK, 'B', BLANK, BLANK},
                {BLANK, BLANK, 'B', BLANK, BLANK},
                {BLANK, BLANK, 'B', BLANK, BLANK},
                {BLANK, BLANK, BLANK, BLANK, BLANK}
        };

        Brick brick = Brick.builder()
                .alignment(Alignment.VERTICAL)
                .build();

        ActiveBrick activeBrick = ActiveBrick.builder()
                .brick(brick)
                .horizontalPosition(2)
                .verticalPosition(1)
                .build();

        MatchAction.checkForMatches(frame, activeBrick);

        assertEquals(BLANK, frame[1][2]);
        assertEquals(BLANK, frame[2][2]);
        assertEquals(BLANK, frame[3][2]);
    }

    @Test
    void testNoMatchWhenLessThanThree() {
        char[][] frame = {
                {BLANK, BLANK, BLANK, BLANK, BLANK},
                {BLANK, 'C', 'C', BLANK, BLANK},
                {BLANK, BLANK, BLANK, BLANK, BLANK},
                {BLANK, BLANK, BLANK, BLANK, BLANK},
                {BLANK, BLANK, BLANK, BLANK, BLANK}
        };

        Brick brick = Brick.builder()
                .alignment(Alignment.HORIZONTAL)
                .build();

        ActiveBrick activeBrick = ActiveBrick.builder()
                .brick(brick)
                .horizontalPosition(1)
                .verticalPosition(1)
                .build();

        MatchAction.checkForMatches(frame, activeBrick);

        assertEquals('C', frame[1][1]);
        assertEquals('C', frame[1][2]);
    }

    @Test
    void testBlankCellsAreIgnored() {
        char[][] frame = {
                {BLANK, BLANK, BLANK, BLANK, BLANK},
                {BLANK, BLANK, BLANK, BLANK, BLANK},
                {BLANK, BLANK, BLANK, BLANK, BLANK},
                {BLANK, BLANK, BLANK, BLANK, BLANK},
                {BLANK, BLANK, BLANK, BLANK, BLANK}
        };

        Brick brick = Brick.builder()
                .alignment(Alignment.HORIZONTAL)
                .build();

        ActiveBrick activeBrick = ActiveBrick.builder()
                .brick(brick)
                .horizontalPosition(1)
                .verticalPosition(2)
                .build();

        assertDoesNotThrow(() -> MatchAction.checkForMatches(frame, activeBrick));

        assertEquals(BLANK, frame[2][1]);
        assertEquals(BLANK, frame[2][2]);
        assertEquals(BLANK, frame[2][3]);
    }

    // ==========================================
    // EDGE CASES & FALSE POSITIVE TESTS
    // ==========================================

    @Test
    void testFalsePositiveDiscontiguousSymbols() {
        char[][] frame = {
                {BLANK, BLANK, BLANK, BLANK, BLANK},
                {BLANK, 'A', 'X', 'A', BLANK},
                {BLANK, BLANK, BLANK, BLANK, BLANK},
                {BLANK, BLANK, BLANK, BLANK, BLANK},
                {BLANK, BLANK, BLANK, BLANK, BLANK}
        };

        Brick brick = Brick.builder()
                .alignment(Alignment.HORIZONTAL)
                .build();

        ActiveBrick activeBrick = ActiveBrick.builder()
                .brick(brick)
                .horizontalPosition(1)
                .verticalPosition(1)
                .build();

        MatchAction.checkForMatches(frame, activeBrick);

        assertEquals('A', frame[1][1]);
        assertEquals('X', frame[1][2]);
        assertEquals('A', frame[1][3]);
    }

    @Test
    void testBoundaryEdgeCaseLeftmostColumn() {
        char[][] frame = {
                {'D', 'D', 'D', BLANK, BLANK},
                {BLANK, BLANK, BLANK, BLANK, BLANK},
                {BLANK, BLANK, BLANK, BLANK, BLANK},
                {BLANK, BLANK, BLANK, BLANK, BLANK},
                {BLANK, BLANK, BLANK, BLANK, BLANK}
        };

        Brick brick = Brick.builder()
                .alignment(Alignment.HORIZONTAL)
                .build();

        ActiveBrick activeBrick = ActiveBrick.builder()
                .brick(brick)
                .horizontalPosition(0)
                .verticalPosition(0)
                .build();

        MatchAction.checkForMatches(frame, activeBrick);

        assertEquals(BLANK, frame[0][0]);
        assertEquals(BLANK, frame[0][1]);
        assertEquals(BLANK, frame[0][2]);
    }

    @Test
    void testBoundaryEdgeCaseTopRow() {
        char[][] frame = {
                {'E', BLANK, BLANK, BLANK, BLANK},
                {'E', BLANK, BLANK, BLANK, BLANK},
                {'E', BLANK, BLANK, BLANK, BLANK},
                {BLANK, BLANK, BLANK, BLANK, BLANK},
                {BLANK, BLANK, BLANK, BLANK, BLANK}
        };

        Brick brick = Brick.builder()
                .alignment(Alignment.VERTICAL)
                .build();

        ActiveBrick activeBrick = ActiveBrick.builder()
                .brick(brick)
                .horizontalPosition(0)
                .verticalPosition(0)
                .build();

        MatchAction.checkForMatches(frame, activeBrick);

        assertEquals(BLANK, frame[0][0]);
        assertEquals(BLANK, frame[1][0]);
        assertEquals(BLANK, frame[2][0]);
    }
}