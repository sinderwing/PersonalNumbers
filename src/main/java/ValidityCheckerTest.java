import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidityCheckerTest {

    @Test
    void validate() {
        ValidityChecker vl = new ValidityChecker();

        // Valid personal numbers
        assertTrue(vl.validate("201701102384"));
        assertTrue(vl.validate("141206-2380"));
        assertTrue(vl.validate("20080903-2386"));
        assertTrue(vl.validate("7101169295"));
        assertTrue(vl.validate("198107249289"));
        assertTrue(vl.validate("19021214-9819"));
        assertTrue(vl.validate("190910199827"));
        assertTrue(vl.validate("191006089807"));
        assertTrue(vl.validate("192109099180"));
        assertTrue(vl.validate("4607137454"));
        assertTrue(vl.validate("194510168885"));
        assertTrue(vl.validate("900118+9811"));
        assertTrue(vl.validate("189102279800"));
        assertTrue(vl.validate("189912299816"));

        // Invalid personal numbers
        assertFalse(vl.validate("201701272394"));
        assertFalse(vl.validate("190302299813"));

        // Valid coordination number
        assertTrue(vl.validate("190910799824"));

        // Valid organisation numbers
        assertTrue(vl.validate("556614-3185"));
        assertTrue(vl.validate("16556601-6399"));
        assertTrue(vl.validate("262000-1111"));
        assertTrue(vl.validate("857202-7566"));

    }
}