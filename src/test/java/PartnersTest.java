import edu.montana.csci.csci468.CatscriptTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PartnersTest extends CatscriptTestBase {
    @Test
    // Test 1 tested the execution of variable statements, for statements, and the if statement.
    void testOne() {
        assertEquals("1\n2\n3\n", executeProgram("var y = true\n" +
                "if(y) {" +
                "  for(x in [1, 2, 3]) { print(x) }" +
                "}"
        ));
    }

    @Test
    // Test 2 tested if a list literal expression, [1+2, 2-1, 3/1], would compile to bytecode.
    void testTwo() {
        assertEquals("[3, 1, 3]\n", compile("[1+2, 2-1, 3/1]"));
    }

    @Test
    // Test 3 tested the execution of a function and comparison expressions within an if statement.
    void testThree() {
        assertEquals("8\n9\n12\n", executeProgram("function foo(x : int) {" +
                "if(x > 5){" +
                "  print(x)" +
                "}" +
                " }\n" +
                "foo(8)\n" +
                "foo(9)\n" +
                "foo(5)\n" +
                "foo(12)"
        ));
    }
}
