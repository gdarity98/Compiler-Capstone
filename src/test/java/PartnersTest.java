import edu.montana.csci.csci468.CatscriptTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PartnersTest extends CatscriptTestBase {

    @Test
    void testOne() {
        // Tests proper implementation of function, if, comparison expression, and print
        assertEquals("12\n12\n12\n", executeProgram("function gotcha(n : int) {" +
                "var x = n\n" +
                "if (x < 12) {" +
                "x = 12" +
                "}" +
                "print(x)" +
                "}\n" +
                "gotcha(12)\n" +
                "gotcha(11)\n" +
                "gotcha(9)"));
    }


    @Test
    void testTwo() {
        // Tests proper implementation of for loop with if statement inside
        assertEquals("5\n", executeProgram("function yay(x : int) {" +
                "var i = 5\n" +
                "if (i == (x-1)){" +
                "  print(i)" +
                "}" +
                " }\n" +
                "yay(4)\n" +
                "yay(1)\n" +
                "yay(6)"
            ));
    }

    @Test
    void testThree() {
        assertEquals("[24, 15, 8]\n", compile("[4*6, 3*5, 4*2]"));
    }
}