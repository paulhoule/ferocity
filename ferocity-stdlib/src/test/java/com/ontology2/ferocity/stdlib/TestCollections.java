package com.ontology2.ferocity.stdlib;

import com.ontology2.ferocity.Context;
import com.ontology2.ferocity.Expression;
import com.ontology2.ferocity.Literal;
import org.junit.jupiter.api.Test;

//import java.util.ArrayList;
import 𝔣.java.util.ArrayList;
import java.util.List;

import static com.ontology2.ferocity.ExpressionDSL.local;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static 𝔣.java.util.List.*;

public class TestCollections {
    //
    // Note there should be some way to set a type parameter or just put <> on the
    // constructor if the list is generic
    //
    @Test
    public void createAList() {
        var lx = ArrayList.<String>newArrayList();
        var l = lx.evaluateRT();
        l.add("XXX");
        l.add("YYY");
        l.add("ZZZ");
        assertEquals(List.of("XXX","YYY","ZZZ"), l);
        assertEquals("new java.util.ArrayList<>()", lx.asSource());
    }

    @Test
    public void createAList2() {
        var lx = ArrayList.<String>newArrayList();
        var l = lx.evaluateRT();
        l.add("XXX");
        l.add("YYY");
        l.add("ZZZ");
        assertEquals(List.of("XXX","YYY","ZZZ"), l);
        assertEquals("new java.util.ArrayList<>()", lx.asSource());
    }

    @Test
    public void addToAList() {
        var l = new java.util.ArrayList<>();
        Context ctx=new Context();
        ctx.set("list", l);
        //noinspection unchecked
        var list = local("list", (List<String>[]) new java.util.ArrayList[] {});
        var lx = callAdd(list, Literal.of("shake"));
        var v  = lx.evaluateRT(ctx);
        assertTrue(v);
        assertEquals(List.of("shake"), l);
        assertEquals("list.add(\"shake\")", lx.asSource());
    }
}
