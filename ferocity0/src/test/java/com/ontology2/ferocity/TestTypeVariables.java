package com.ontology2.ferocity;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Parameter;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
public class TestTypeVariables {
    @Test
    public void iCanGetTheTypeVariablesForGetAnnotation() throws NoSuchMethodException {
        var m = Parameter.class.getDeclaredMethod("getAnnotation",Class.class);
        assertFalse(m.isDefault());
        var typeParameters=m.getTypeParameters();
        assertEquals(1,typeParameters.length);
        assertEquals("T", typeParameters[0].toString());
    }

    @Test
    public void 太陽() {
        assertTrue(true);
    }

    @Test
    public void somethingエabout() {
        assertTrue(true);
    }

    @Test

    public void whatAboutℍくʌ() {
        assertTrue(true);
    }
}
