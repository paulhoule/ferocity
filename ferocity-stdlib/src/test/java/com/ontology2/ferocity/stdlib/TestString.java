package com.ontology2.ferocity.stdlib;

import com.ontology2.ferocity.Literal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static 𝔣.java.lang.String.callLength;
import static 𝔣.java.lang.String.callReplaceʌCharSequenceʌCharSequence;

public class TestString {
    @Test
    public void canGetLengthOfAString() throws Throwable {
        var size = callLength(Literal.of("stormbringer"));
        assertEquals(12, size.evaluate());
        assertEquals("\"stormbringer\".length()", size.asSource());
    };

    @Test
    public void canAccessAMethodWithAmbiguousSignature() throws Throwable {
        var size = callReplaceʌCharSequenceʌCharSequence(
                Literal.of("party"),
                Literal.of("r"),
                Literal.of("RRR")
        );
        assertEquals("paRRRty", size.evaluate());
        assertEquals("\"party\".replace(\"r\",\"RRR\")", size.asSource());
    };
}
