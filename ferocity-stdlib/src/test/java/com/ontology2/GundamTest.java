package com.ontology2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GundamTest {
    @Test
    public void itKnowsItsName() {
        var a = new Amuro();
        assertEquals("Gundam",a.mech());
    };
}
