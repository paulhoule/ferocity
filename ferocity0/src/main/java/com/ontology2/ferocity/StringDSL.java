package com.ontology2.ferocity;

import static com.ontology2.ferocity.ExpressionDSL.*;
import static com.ontology2.ferocity.MethodCall.*;

public class StringDSL {
    public static Expression<Integer> callLength(Expression<String> that) {
        return createMethodCall(STRING, that,"length", new Class[]{});
    }

    public static Expression<Character> callCharAt(Expression<String> that, Expression<Integer> charAt) {
        return createMethodCall(STRING, that,"charAt", new Class[]{int.class}, charAt);
    }

    public static Expression<String> callSubstring(Expression<String> that, Expression<Integer> begin, Expression<Integer> end) {
        return createMethodCall(STRING, that, "substring", new Class[]{int.class, int.class}, begin, end);
    }

    public static Expression<String> callSubstring(Expression<String> that, Expression<Integer> begin) {
        return createMethodCall(STRING, that, "substring", new Class[]{int.class}, begin);
    }

    public static Expression<byte[]> callGetBytes(Expression<String> that) {
        return createMethodCall(STRING, that, "getBytes", CLASS);
    }



    //
    // the syntax is written with varags,  but the compiler will not match the varags so you have to
    // create an array and pass it into args
    //
    // Note that I could write this like
    //
    // callFormat(Expression<String> format, Expression<Object[] args) in which case the varadic
    // part of the expression is an expression as a whole,  so you can vary the number of parameters
    // in it when you generate code.
    //

    public static OpenVariadic<String, Object> callFormat(Expression<String> format) {
        MethodCall<String> mc = createStaticMethodCall(STRING, "format", new Class[]{String.class, Object[].class}, format);
        return mc.withVarargs(OBJECT);
    }

    public static ClosedVariadic<String, Object> callFormat(Expression<String> format, Expression<Object[]> args) {
        MethodCall<String> mc = createStaticMethodCall(STRING, "format", new Class[]{String.class, Object[].class}, format);
        return mc.withVarargs(OBJECT, args);
    }

    public static Expression<String> callValueOf(Expression<Long> l) {
        return createStaticMethodCall(STRING, "valueOf", new Class[]{long.class}, l);
    }

}
