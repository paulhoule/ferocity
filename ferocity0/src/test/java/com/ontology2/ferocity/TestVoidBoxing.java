package com.ontology2.ferocity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestVoidBoxing {
    Void nothing() {
        return null;
    }

    /**
     * Note that there is boxing between void.class and Void.class and as such
     * you can have a Void[];  Void doesn't have a constructor,  so a Void is
     * always null,  a reasonable way to represent "no return value"
     */
    @Test
    public void aFunctionCanReturnCaptialVVoid() {
        assertNull(nothing());
    }


}
