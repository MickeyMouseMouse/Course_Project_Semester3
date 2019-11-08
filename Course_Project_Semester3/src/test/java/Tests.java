import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class Tests {
    private SignsFinder model = new SignsFinder();

    @Test
    public void wrongFormattedInputString() {
        model.setInputString("1=1");
        assertEquals(" 1 = 1", model.getInputString());

        model.setInputString("1    2  3=   5");
        assertEquals(" 1 2 3 = 5", model.getInputString());

        model.setInputString(" (((1 2)3   4) 5)=6");
        assertEquals(" ( ( ( 1 2 ) 3 4 ) 5 ) = 6", model.getInputString());
    }

    @Test
    public void badInputString() {
        model.setInputString("");
        assertFalse(model.isValid());

        model.setInputString("1 2 3");
        assertFalse(model.isValid());

        model.setInputString("( 1 2 ( 3 ) = 4");
        assertFalse(model.isValid());

        model.setInputString("( 1 2 ( 3 ) ) = ");
        assertFalse(model.isValid());

        model.setInputString("1 2 3 4 p = 24");
        assertFalse(model.isValid());

        model.setInputString("1 2 3 4 + 3 = 24");
        assertFalse(model.isValid());

        model.setInputString("1 2 3 4 3 = -24");
        assertFalse(model.isValid());
    }

    @Test
    public void thereIsSolution() {
        model.setInputString("2 ( 2 3 4 ( 4 5 ) 3 ) = 15");
        if (model.isValid())
            assertEquals(" 2+( 2+3+4+(-4+5 )+3 ) = 15", model.solve());
        else
            throw new RuntimeException("Input string isn't valid");
    }

    @Test
    public void thereIsNoSolution() {
        model.setInputString("2 3 3 ( 5 ( 6 3 ) ) = 28");
        if (model.isValid())
            assertEquals("There is no solution", model.solve());
        else
            throw new RuntimeException("Input string isn't valid");
    }

    @Test
    public void overflow() {
        model.setInputString("1 10000000000 1 = 1"); // 10 000 000 000 is a very large number for 4 bytes
        if (model.isValid())
            assertEquals("10000000000", model.solve());
        else
            throw new RuntimeException("Input string isn't valid");
    }

    @Test
    public void overflowDuringCalculation() {
        model.setInputString("100000 100000 = 1"); // 100 000 * 100 000 = 10 000 000 000
        if (model.isValid())
            assertEquals("Overflow", model.solve());
        else
            throw new RuntimeException("Input string isn't valid");
    }
}