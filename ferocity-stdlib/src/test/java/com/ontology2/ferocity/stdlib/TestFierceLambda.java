package com.ontology2.ferocity.stdlib;

import com.ontology2.ferocity.Context;
import com.ontology2.ferocity.Expression;
import org.junit.jupiter.api.Test;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.ontology2.ferocity.ParameterDeclaration;

import static com.ontology2.ferocity.ExpressionDSL.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static 𝔣.java.lang.Math.callSqrt;
import static 𝔣.java.time.Month.callFrom;
import static 𝔣.java.time.LocalDate.callNow;
import static 𝔣.java.util.List.callAdd;

@SuppressWarnings({"Convert2MethodRef", "ConstantConditions"})
public class TestFierceLambda {
    @Test
    public void createAFunctionLambda() {
        var fnExpr = lambdaFunction(Double.class, Double.class, (ParameterDeclaration<Double> x) -> callSqrt(x.reference()));
        var fn = fnExpr.evaluateRT();
        assertEquals(4.0, fn.apply(16.0), 0.001);
        assertEquals("(java.lang.Double arg0) -> java.lang.Math.sqrt(arg0)", fnExpr.asSource());
        Function<Double, Double> dd = (java.lang.Double arg0) -> java.lang.Math.sqrt(arg0);
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
        assertEquals("(java.time.Month arg0) -> com.ontology2.ferocity.Exports.discardIt(accumulator.add(arg0))",cExpr.asSource());
        Consumer<Month> mm = (java.time.Month arg0) -> com.ontology2.ferocity.Exports.discardIt(accumulator.add(arg0));
        mm.accept(Month.OCTOBER);
        assertEquals(List.of(Month.FEBRUARY, Month.OCTOBER), accumulator);
    }

}
