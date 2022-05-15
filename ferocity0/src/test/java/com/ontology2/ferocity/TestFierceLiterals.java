package com.ontology2.ferocity;

import org.junit.jupiter.api.Test;

import static com.ontology2.ferocity.ExpressionDSL.add;
import static com.ontology2.ferocity.Literal.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestFierceLiterals {
    @Test
    public void literalizeAString() throws Throwable {
        var l= of("A string");
        assertEquals("\"A string\"", l.toString());
        assertEquals("A string", l.evaluate());
    }

    @Test
    public void literalizeNewline() {
        var l= of("\n");
        assertEquals("\"\\n\"", l.toString());
    }

    @Test
    public void literalizeCR() {
        var l= of("\r");
        assertEquals("\"\\r\"", l.toString());
    }

    @Test
    public void thatsALotOfBackslashes() {
        var l= of("\\");
        assertEquals("\"\\\\\"", l.toString());
    }

    @Test
    public void doubleDoubleQuote() {
        var l= of("\"");
        assertEquals("\"\\\"\"", l.toString());
    }

    @Test
    public void concatTwoStrings() {
        Expression<String> that = add(of("RED"), of("blue"));
        assertEquals("(\"RED\"+\"blue\")", that.toString());
    }

    @Test
    public void aBigNumber() throws Throwable {
        Literal<Long> of = of(9999999999L);
        assertEquals("9999999999L", of.toString());
        assertEquals( 9999999999L,  (long) of.evaluate());
    }

    @Test
    public void aModerateNumber() {
        assertEquals("666666",of(666666).toString());
    }

    @Test
    public void isTrue() {
        assertEquals("true",of(true).toString());
    }

    @Test
    public void isFalse() {
        assertEquals("false",of(false).toString());
    }

    @Test
    public void shiningC() {
        assertEquals("'c'",of('c').toString());
    }

    @Test
    public void newlineAsaCharacter() {
        assertEquals("'\\n'",of('\n').toString());
    }

    @Test
    public void backSlashSlash() {
        assertEquals("'\\\\'",of('\\').toString());
    }

    @Test
    public void nothingOfNothingIsNothing() throws Throwable {
        assertEquals("null",ofNull().toString());
        assertNull(ofNull().evaluate());
    }

    @Test
    public void thoseAwfulFloats() {
        assertEquals("1.45f",of(1.45f).toString());
    }

    @Test
    public void andDoubles() {
        assertEquals("7210000.0",of(72.1e5).toString());
    }

    @Test
    public void evenClasses() { assertEquals("java.lang.System.class", of(System.class).toString());}

    @Test
    public void noArrayButTheClassArray() {
        assertEquals(
                "new java.lang.Class[] {java.lang.StringBuilder.class, java.lang.Throwable.class}",
                of(new java.lang.Class[]{ StringBuilder.class, Throwable.class }).toString());}
}
