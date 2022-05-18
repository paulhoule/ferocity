package com.ontology2.ferocity.stdlib;

import com.ontology2.ferocity.Literal;
import org.junit.jupiter.api.Test;

import static com.ontology2.ferocity.ExpressionDSL.STRING;
import static com.ontology2.ferocity.ExpressionDSL.objectArray;
import static com.ontology2.ferocity.Literal.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static 𝔣.java.lang.String.*;

public class TestString {
    @Test
    public void canGetLengthOfAString() throws Throwable {
        var size = callLength(of("stormbringer"));
        assertEquals(12, size.evaluate());
        assertEquals("\"stormbringer\".length()", size.asSource());
    }

    @Test
    public void canAccessAMethodWithAmbiguousSignature() throws Throwable {
        var size = callReplaceʌCharSequenceʌCharSequence(
                of("party"),
                of("r"),
                of("RRR")
        );
        assertEquals("paRRRty", size.evaluate());
        assertEquals("\"party\".replace(\"r\",\"RRR\")", size.asSource());
    }

    @Test
    public void canAccessAStaticMethod() throws Throwable {
        var parts = callJoinʌCharSequenceʌCharSequenceʘ(
                of("*"),
                objectArray(STRING, of("x1"), of("x2"), of("x3"))
        );
        assertEquals("x1*x2*x3", parts.evaluate());
        assertEquals("java.lang.String.join(\"*\",new java.lang.String[] " +
                "{\"x1\", \"x2\", \"x3\"})", parts.asSource());
    }
}
