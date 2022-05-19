package com.ontology2.ferocity;

import org.junit.jupiter.api.Test;

import static com.ontology2.ferocity.ConstructorCall.createConstructorCall;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestFierceConstructors {
    @Test
    public void constructAnException() throws Throwable {
        var ctor = createConstructorCall(
                new UnsupportedOperationException[] {},
                new Class[] {String.class},
                new Expression[] {Literal.of("I can't do that, Dave")}
        );
        var x = ctor.evaluate();
        assertEquals("I can't do that, Dave", x.getMessage());
        assertEquals("new java.lang.UnsupportedOperationException(\"I can't do that, Dave\")", ctor.asSource());
    }
}
