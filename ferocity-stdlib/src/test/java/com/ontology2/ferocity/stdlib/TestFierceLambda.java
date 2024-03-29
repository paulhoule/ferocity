package com.ontology2.ferocity.stdlib;

import com.ontology2.ferocity.Context;
import com.ontology2.ferocity.Expression;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

import com.ontology2.ferocity.ParameterDeclaration;
import fierce.java.math.BigInteger𝔣;

import static com.ontology2.ferocity.ExpressionDSL.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static fierce.java.lang.Math𝔣.callSqrt;
import static fierce.java.time.Month𝔣.callFrom;
import static fierce.java.time.LocalDate𝔣.callNow;
import static fierce.java.util.List𝔣.callAdd;

@SuppressWarnings({"Convert2MethodRef", "ConstantConditions"})
public class TestFierceLambda {
    @Test
    public void createAFunctionLambda() {
        var fnExpr = lambdaFunction(Double.class, Double.class, (ParameterDeclaration<Double> x) -> callSqrt(x.reference()));
        var fn = fnExpr.evaluateRT();
        assertEquals(4.0, fn.apply(16.0), 0.001);
        assertEquals("(arg0) -> java.lang.Math.sqrt(arg0)", fnExpr.asSource());
        Function<Double, Double> dd = (arg0) -> java.lang.Math.sqrt(arg0);
        assertEquals(7.0, dd.apply(49.0), 0.001);
    }

    @Test
    public void whatAboutSuppliers() {
        var fnExpr = lambdaSupplier(Month.class, () -> callFrom(callNow()));
        var supplier = fnExpr.evaluateRT();
        var thisMonth = supplier.get();
        assertTrue(thisMonth.getValue()>=0);
        assertTrue(thisMonth.getValue()<12);
        assertEquals("() -> java.time.Month.from(java.time.LocalDate.now())", fnExpr.asSource());
        Supplier<Month> mm = () -> java.time.Month.from(java.time.LocalDate.now());
        assertEquals(thisMonth, mm.get());
    }

    @Test
    public void andAConsumer() {
        var accumulator = new ArrayList<Month>();
        //noinspection unchecked
        var _accumulator = local("accumulator", (ArrayList<Month>[]) new ArrayList[] {});
        var cExpr = lambdaConsumer(Month.class, (in) -> discard(callAdd(_accumulator, in.reference())));
        var ctx = new Context();
        ctx.set("accumulator", accumulator);
        var addToArray = cExpr.evaluateRT(ctx);
        addToArray.accept(Month.FEBRUARY);
        assertEquals(List.of(Month.FEBRUARY), accumulator);
        assertEquals("(arg0) -> com.ontology2.ferocity.Exports.discardIt(accumulator.add(arg0))",cExpr.asSource());
        Consumer<Month> mm = (arg0) -> com.ontology2.ferocity.Exports.discardIt(accumulator.add(arg0));
        mm.accept(Month.OCTOBER);
        assertEquals(List.of(Month.FEBRUARY, Month.OCTOBER), accumulator);
    }

    @Test
    public void biConsumersWorkToo() {
        var accumulator = new ArrayList<String>();
        Expression<BiConsumer<List<String>,String>> expr = lambdaBiConsumer(List.class, String.class,
                (a,b) -> discard(callAdd(a.reference(), b.reference()))
        );
        var addToArray = expr.evaluateRT();
        addToArray.accept(accumulator, "We're");
        addToArray.accept(accumulator,"not");
        addToArray.accept(accumulator, "out");
        assertEquals("(arg0, arg1) -> " +
                "com.ontology2.ferocity.Exports.discardIt(arg0.add(arg1))",expr.asSource());
        BiConsumer<List<String>, String> c2 = (arg0, arg1)
                -> com.ontology2.ferocity.Exports.discardIt(arg0.add(arg1));
        c2.accept(accumulator,"yet.");
        String result = String.join(" ", accumulator);
        assertEquals("We're not out yet.", result);
    }

    @Test
    public void canDoMathWithBigIntegers() {
        Expression<BinaryOperator<BigInteger>> expr = lambdaBinaryOperator(BigInteger.class,
                (a, b) -> BigInteger𝔣.callAdd(a.reference(), b.reference()));
        var left = BigInteger.valueOf(105L);
        var right = BigInteger.valueOf(95L);
        assertEquals(left.add(right), expr.evaluateRT().apply(left,right));
    }

}
