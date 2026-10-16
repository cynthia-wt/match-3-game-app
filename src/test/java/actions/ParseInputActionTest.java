package actions;

import model.Alignment;
import model.Brick;
import model.Command;
import model.InitializationInput;
import org.junit.jupiter.api.Test;

import java.util.List;

import static engine.GameConfig.BRICK_LENGTH;
import static org.junit.jupiter.api.Assertions.*;

class ParseInputActionTest {

    // ---------- helper ----------

    private static IllegalArgumentException assertRejected(String input) {
        return assertThrows(IllegalArgumentException.class,
                () -> ParseInputAction.parseInitializationInput(input));
    }

    // ---------- parseInitializationInput: valid input ----------

    @Test
    void parseInitializationInput_validInput_parsesBoardSize() {
        InitializationInput result = ParseInputAction.parseInitializationInput("5 8 H^^* V*@^");

        assertEquals(5, result.getBoardWidth());
        assertEquals(8, result.getBoardHeight());
    }

    @Test
    void parseInitializationInput_validInput_parsesEveryBrickInOrder() {
        InitializationInput result = ParseInputAction.parseInitializationInput("5 8 H^^* V*@^");

        List<Brick> bricks = result.getBricks();
        assertEquals(2, bricks.size());

        assertEquals(Alignment.HORIZONTAL, bricks.get(0).getAlignment());
        assertArrayEquals(new char[]{'^', '^', '*'}, bricks.get(0).getSymbols());

        assertEquals(Alignment.VERTICAL, bricks.get(1).getAlignment());
        assertArrayEquals(new char[]{'*', '@', '^'}, bricks.get(1).getSymbols());
    }

    @Test
    void parseInitializationInput_singleBrick_isAccepted() {
        InitializationInput result = ParseInputAction.parseInitializationInput("5 8 H^^*");

        assertEquals(1, result.getBricks().size());
        assertArrayEquals(new char[]{'^', '^', '*'}, result.getBricks().get(0).getSymbols());
    }

    @Test
    void parseInitializationInput_fiveBricks_isAccepted() {
        InitializationInput result = ParseInputAction.parseInitializationInput(
                "5 8 H^^* V*@^ H~~~ V@@@ H*^~");

        assertEquals(5, result.getBricks().size());
    }

    @Test
    void parseInitializationInput_allAllowedSymbols_areAccepted() {
        InitializationInput result = ParseInputAction.parseInitializationInput("5 8 H~^* V@~^");

        assertArrayEquals(new char[]{'~', '^', '*'}, result.getBricks().get(0).getSymbols());
        assertArrayEquals(new char[]{'@', '~', '^'}, result.getBricks().get(1).getSymbols());
    }

    @Test
    void parseInitializationInput_trailingSpace_isIgnored() {
        InitializationInput result = ParseInputAction.parseInitializationInput("5 8 H^^* ");

        assertEquals(1, result.getBricks().size());
    }

    // ---------- parseInitializationInput: wrong number of parts ----------

    @Test
    void parseInitializationInput_emptyInput_isRejected() {
        assertEquals("Enter width, height and at least one brick.", assertRejected("").getMessage());
    }

    @Test
    void parseInitializationInput_blankInput_isRejected() {
        assertRejected("   ");
    }

    @Test
    void parseInitializationInput_missingHeight_isRejected() {
        assertRejected("5");
    }

    @Test
    void parseInitializationInput_noBricks_isRejected() {
        assertRejected("5 8");
    }

    @Test
    void parseInitializationInput_sixBricks_isRejected() {
        IllegalArgumentException e = assertRejected("5 8 H^^* V*@^ H~~~ V@@@ H*^~ V~~~");

        assertEquals("A maximum of 5 bricks is allowed.", e.getMessage());
    }

    // ---------- parseInitializationInput: malformed board size ----------

    @Test
    void parseInitializationInput_nonNumericWidth_isRejected() {
        IllegalArgumentException e = assertRejected("abc 8 H^^*");

        assertEquals("Width and height must be numbers.", e.getMessage());
    }

    @Test
    void parseInitializationInput_nonNumericHeight_isRejected() {
        assertRejected("5 xyz H^^*");
    }

    @Test
    void parseInitializationInput_decimalSize_isRejected() {
        assertRejected("5.5 8 H^^*");
    }

    // ---------- parseInitializationInput: malformed bricks ----------

    @Test
    void parseInitializationInput_brickTooShort_isRejected() {
        IllegalArgumentException e = assertRejected("5 8 H^^");

        assertEquals("Each block should have 3 symbols.", e.getMessage());
    }

    @Test
    void parseInitializationInput_brickTooLong_isRejected() {
        assertRejected("5 8 H^^**");
    }

    @Test
    void parseInitializationInput_unknownAlignmentPrefix_isRejected() {
        IllegalArgumentException e = assertRejected("5 8 X^^*");

        assertEquals("Orientation should be either `H`(horizontal) or `V`(vertical).", e.getMessage());
    }

    @Test
    void parseInitializationInput_symbolNotAllowed_isRejected() {
        IllegalArgumentException e = assertRejected("5 8 Habc");

        assertEquals("Only these symbols are allowed: `~`, `^`, `*`, `@`", e.getMessage());
    }

    @Test
    void parseInitializationInput_widthLessThanBrickLength_isRejected() {
        IllegalArgumentException e = assertRejected("2 8 H***");

        assertEquals("Width and height should be > " + BRICK_LENGTH, e.getMessage());
    }

    @Test
    void parseInitializationInput_heightLessThanBrickLength_isRejected() {
        IllegalArgumentException e = assertRejected("4 2 H***");

        assertEquals("Width and height should be > " + BRICK_LENGTH, e.getMessage());
    }

    @Test
    void parseInitializationInput_oneBadBrickAmongGoodOnes_isRejected() {
        assertRejected("5 8 H^^* X*@^ H~~~");
    }

    // ---------- parseCommandInput ----------

    @Test
    void parseCommandInput_singleCommands_areMapped() {
        assertEquals(List.of(Command.LEFT), ParseInputAction.parseCommandInput("L"));
        assertEquals(List.of(Command.RIGHT), ParseInputAction.parseCommandInput("R"));
        assertEquals(List.of(Command.DOWN), ParseInputAction.parseCommandInput("D"));
    }

    @Test
    void parseCommandInput_lowercase_isAccepted() {
        assertEquals(List.of(Command.LEFT, Command.RIGHT), ParseInputAction.parseCommandInput("lr"));
    }

    @Test
    void parseCommandInput_twoCommands_keepsOrder() {
        assertEquals(List.of(Command.RIGHT, Command.RIGHT), ParseInputAction.parseCommandInput("RR"));
        assertEquals(List.of(Command.DOWN, Command.LEFT), ParseInputAction.parseCommandInput("DL"));
    }

    @Test
    void parseCommandInput_moreThanTwoCommands_keepsOnlyTheFirstTwo() {
        assertEquals(List.of(Command.LEFT, Command.RIGHT), ParseInputAction.parseCommandInput("LRD"));
    }

    @Test
    void parseCommandInput_invalidCharacters_areIgnored() {
        assertEquals(List.of(Command.LEFT), ParseInputAction.parseCommandInput("XL"));
    }

    @Test
    void parseCommandInput_invalidCharactersDoNotCountTowardTheLimit() {
        assertEquals(List.of(Command.LEFT, Command.RIGHT), ParseInputAction.parseCommandInput("XXLYR"));
    }

    @Test
    void parseCommandInput_emptyInput_returnsNoCommands() {
        assertEquals(List.of(), ParseInputAction.parseCommandInput(""));
    }

    @Test
    void parseCommandInput_onlyInvalidCharacters_returnsNoCommands() {
        assertEquals(List.of(), ParseInputAction.parseCommandInput("xyz 123"));
    }
}