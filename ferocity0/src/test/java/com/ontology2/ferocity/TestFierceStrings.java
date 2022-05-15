package com.ontology2.ferocity;

import org.junit.jupiter.api.Test;

import static com.ontology2.ferocity.Literal.of;
import static com.ontology2.ferocity.StringDSL.*;
import static com.ontology2.ferocity.ExpressionDSL.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestFierceStrings {
    @Test
    public void computeTheLengthOfAString() throws Throwable {
        Expression<Integer> four = callLength(of("Four"));
        assertEquals("\"Four\".length()", four.toString());
        assertEquals(4, (long) four.evaluate());
    }

    @Test
    public void getFourthCharacter() throws Throwable {
        Expression<Character> three = callCharAt(of("Three"), of(3));
        assertEquals("\"Three\".charAt(3)", three.toString());
        assertEquals((Character) 'e', three.evaluate());
    }

    @Test
    public void fromTheMiddle() throws Throwable {
        Expression<String> eleven = callSubstring(of("Eleven"), of(1), of(3));
        assertEquals("\"Eleven\".substring(1,3)", eleven.toString());
        assertEquals("le", eleven.evaluate());
    }

    @Test
    public void offTheEnd() throws Throwable {
        Expression<String> seventeen = callSubstring(of("seventeen"), of(1));
        assertEquals("\"seventeen\".substring(1)", seventeen.toString());
        assertEquals("eventeen", seventeen.evaluate());
    }

    @Test
    public void callValueOfLong() throws Throwable {
        Expression<String> fiveFive = callValueOf(of(55L));
        assertEquals("java.lang.String.valueOf(55L)", fiveFive.toString());
        assertEquals("55", fiveFive.evaluate());
    }

    @Test
    public void arrayCreation() throws Throwable {
        Expression<String[]> xxx = objectArray(STRING, of("x1"), of("x2"), of("x3"));
        String[] x3 = xxx.evaluate();
        assertEquals(3, x3.length);
        assertEquals("x1",x3[0]);
        assertEquals("x2",x3[1]);
        assertEquals("x3",x3[2]);
    }

    //
    // this test doesn't even compile...  i kinda wish it did but it doesn't
    //
    @Test
    public void createObjectArrayWithBooleans() throws Throwable {
        //
        // The compiler can figure out that you can make an object array
        // out of a set of Boolean objects
        //
        Expression<Object[]> koei = objectArray(OBJECT, of(true), of(false));
        Object[] lee = koei.evaluate();
        assertEquals(2, lee.length);
        assertEquals(true, lee[0]);
        assertEquals(false, lee[1]);
    }

    @Test
    public void iWontLoseToVarargs() throws Throwable {
        String result = String.format("%d %s", 555, "timer");
        assertEquals("555 timer", result);

        Expression<String> v0 = StringDSL.callFormat(of("that"));
        assertNotNull(v0);
        assertEquals("that", v0.evaluate());

        Expression<String> v2 = StringDSL.callFormat(of("%d %s")).also(of(555)).also(of("timer"));
        assertEquals("555 timer", v2.evaluate());

        Expression<Object[]> xxx = objectArray(STRING, of("it's"), of("more"), of("fun"), of("to"), of("compute"));
        Expression<String> vv = StringDSL.callFormat(of("%s %s %s %s %s"), xxx);
        assertEquals("it's more fun to compute", vv.evaluate());
    }

    @Test
    public void ifYourCharsetisEDBICThisWontWorkForYou() throws Throwable {
        String SPACE_THE_FINAL_FRONTIER=" ";
        Expression<byte[]> everything = StringDSL.callGetBytes(of(SPACE_THE_FINAL_FRONTIER));
        byte[] answer = everything.evaluate();
        assertEquals(1, answer.length);
        assertEquals(32, answer[0]);
        assertEquals("\" \".getBytes()", everything.asSource());
    }
}


//}
