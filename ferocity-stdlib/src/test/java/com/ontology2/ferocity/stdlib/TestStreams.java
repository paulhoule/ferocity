package com.ontology2.ferocity.stdlib;

import com.ontology2.ferocity.Expression;
import com.ontology2.ferocity.Literal;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import 𝔣.java.util.HashSet;
import 𝔣.java.util.Collection;

import java.math.BigInteger;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.ontology2.ferocity.ExpressionDSL.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static 𝔣.java.math.BigInteger.callValueOf;
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

    @Test
    public void collectIntoSetManually() {
        var builder = callBuilder();
        for(int i=0;i<4;i++) {
            builder=callAdd(builder,Literal.of(i));
        }
        Expression<Stream<Integer>> stream = callBuild(builder);
        Expression<Supplier<Set<Integer>>> supplier = lambdaSupplier(Set.class, HashSet::newHashSet);
        Expression<BiConsumer<Set<Integer>, Integer>> accumulator = lambdaBiConsumer(Set.class, Integer.class,
                (a,b)-> discard(Collection.callAdd(a.reference(),b.reference())));

        Expression<BiConsumer<Set<Integer>, Set<Integer>>> combiner = lambdaBiConsumer(Set.class, Set.class,
                (a,b) -> discard(Collection.callAddAll(a.reference(), b.reference()))
        );
        var expr = callCollect(stream,supplier,accumulator,combiner);
        var l = expr.evaluateRT();
        assertEquals(Set.of(0,1,2,3),l);
        assertEquals("java.util.stream.Stream.builder().add(0).add(1).add(2).add(3).build().collect(" +
                "() -> new java.util.HashSet<>()," +
                "(arg0, arg1) -> com.ontology2.ferocity.Exports.discardIt(arg0.add(arg1))," +
                "(arg0, arg1) -> com.ontology2.ferocity.Exports.discardIt(arg0.addAll(arg1)))", expr.asSource());
        //noinspection Convert2MethodRef
        var r2 = java.util.stream.Stream.builder().add(0).add(1).add(2).add(3).build().collect(()
                -> new java.util.HashSet<>(),(arg0, arg1)
                -> com.ontology2.ferocity.Exports.discardIt(arg0.add(arg1)),(arg0, arg1)
                -> com.ontology2.ferocity.Exports.discardIt(arg0.addAll(arg1)));
        assertEquals(r2,l);
    }

    @Test
    public void countWithReducer() {
        var builder = callBuilder();
        for(long i=0;i<4;i++) {
            builder=callAdd(builder,callValueOf(Literal.of(i)));
        }
        Expression<Stream<BigInteger>> stream = callBuild(builder);
        Expression<BinaryOperator<BigInteger>> increment = lambdaBinaryOperator(BigInteger.class, (a,b) ->
                𝔣.java.math.BigInteger.callAdd(a.reference(), callValueOf(Literal.of(1L))));
        Expression<BigInteger> pipeline = callReduce(stream, callValueOf(Literal.of(0L)), increment);
        assertEquals(4, pipeline.evaluateRT().intValue());
    }

    //
    // Boy we are getting awkward here.  We've only got a limited set of operators for expressions and
    // some limitations in the code generator.
    //
    // An immediate hangup to make this work is that we need to put the <BigInteger> type parameter
    // onto callBuilder() at the very beginning of this process.  Can we make the expression take some
    // parameter like
    //
    // callBuilder.withTypes(BigInteger)
    //
    @Test @Disabled
    public void sumWithReducer() {
        Expression<Stream.Builder<BigInteger>> builder = callBuilder();
        for(long i=0;i<4;i++) {
            builder=callAdd(builder,callValueOf(Literal.of(i)));
        }
        Expression<Stream<BigInteger>> stream = callBuild(builder);
        Expression<BinaryOperator<BigInteger>> addEm = lambdaBinaryOperator(BigInteger.class, (a,b) ->
                𝔣.java.math.BigInteger.callAdd(a.reference(), b.reference()));
        Expression<BigInteger> pipeline = callReduce(stream, callValueOf(Literal.of(0L)), addEm);
        assertEquals(6, pipeline.evaluateRT().intValue());
        assertEquals("java.util.stream.Stream.<BigInteger>builder().add(java.math.BigInteger.valueOf(0L)).add(java.math.BigInteger.valueOf(1L)).add(java.math.BigInteger.valueOf(2L)).add(java.math.BigInteger.valueOf(3L)).build().reduce(java.math.BigInteger.valueOf(0L),(arg0, arg1) -> arg0.add(arg1))", pipeline.asSource());
        BigInteger codePipeline = java.util.stream.Stream.<BigInteger>builder().add(java.math.BigInteger.valueOf(0L)).
                add(java.math.BigInteger.valueOf(1L)).add(java.math.BigInteger.valueOf(2L)).
                add(java.math.BigInteger.valueOf(3L)).build().reduce(java.math.BigInteger.valueOf(0L),(arg0, arg1) -> arg0.add(arg1));
    }
}

