package com.ontology2.ferocity.stdlib;

import com.ontology2.ferocity.Expression;
import com.ontology2.ferocity.Literal;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static 𝔣.java.util.stream.Collectors.*;
import static 𝔣.java.util.stream.Stream.*;
import static 𝔣.java.util.stream.StreamʃBuilder.*;

public class TestStreams {

//
// The following test doesn't build,  it can be made to build by manually hacking the signature of callCollect
// such that the collector argumenmt takes Expression<? extends java.util.stream.Collector<? super T, ?, R>>
// as opposed to A in that slot.  I can see a rule that might work on that one (since that A is used in one
// just one place it can be dropped out.
//

//    @Test
//    public void count123() {
//        var stream = callBuild(callAdd(callAdd(callAdd(callBuilder(), Literal.of(7)), Literal.of(11)),Literal.of(15)));
//        var expr = callCollect(stream, callCounting());
//        assertEquals(3,expr.evaluateRT());
//        assertEquals("java.util.stream.Stream.builder().add(7).add(11).add(15).build().collect(java.util.stream.Collectors.counting())", expr.asSource());
//    }

    @Test
    public void buildAList() {
        var stream = callBuild(callAdd(callAdd(callAdd(callBuilder(), Literal.of(7)),
                Literal.of(11)),Literal.of(15)));
        var expr = callToList(stream);
        var l = expr.evaluateRT();
        assertEquals(3,l.size());
        assertEquals(7, l.get(0));
        assertEquals(11, l.get(1));
        assertEquals(15, l.get(2));
    }

    @Test
    public void buildAListIncrementally() {
        var builder = callBuilder();
        for(int i=0;i<4;i++) {
            builder=callAdd(builder,Literal.of(i));
        }
        var stream = callBuild(builder);
        var expr = callToList(stream);
        var l = expr.evaluateRT();
        assertEquals(4,l.size());
        assertEquals(1, l.get(1));
    }
}

