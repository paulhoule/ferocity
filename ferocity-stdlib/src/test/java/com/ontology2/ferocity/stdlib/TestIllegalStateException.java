package com.ontology2.ferocity.stdlib;

import com.ontology2.ferocity.Literal;
import org.junit.jupiter.api.Test;
import 𝔣.java.lang.Throwable;
import 𝔣.java.lang.IllegalStateException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static 𝔣.java.lang.IllegalStateException.newIllegalStateExceptionʌString;
import static 𝔣.java.lang.IllegalStateException.newIllegalStateExceptionʌThrowable;
import static 𝔣.java.lang.UnsupportedOperationException.newUnsupportedOperationExceptionʌString;

public class TestIllegalStateException {
    @Test
    public void createAnISE() throws java.lang.Throwable {
        var ise = newIllegalStateExceptionʌString((Literal.of("b0ws3r")));
        var that = ise.evaluate();
        assertEquals("b0ws3r", that.getMessage());
    }
}
