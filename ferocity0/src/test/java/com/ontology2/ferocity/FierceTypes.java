package com.ontology2.ferocity;

import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import static com.ontology2.ferocity.ExpressionDSL.reify;
import static org.junit.Assert.assertEquals;

public class FierceTypes {
    @Test
    public void zeroParameterType() {
        Type t = reify(String.class);
        assertEquals("java.lang.String", t.getTypeName());
    }

    @Test
    public void oneParameterType() {
        Type t = reify(List.class, String.class);
        assertEquals("java.util.List<java.lang.String>", t.getTypeName());
    }

    @Test
    public void nestedParameterType() {
        Type t = reify(List.class, reify(Set.class, String.class));
        assertEquals("java.util.List<java.util.Set<java.lang.String>>", t.getTypeName());
    }
}
