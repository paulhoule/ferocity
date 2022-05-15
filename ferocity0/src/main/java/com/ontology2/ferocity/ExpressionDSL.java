package com.ontology2.ferocity;

// GOALS:
//
// - DSL to write Java expressions and serialize as Java source code
// - can compile time typing be useful here?
// - minimum number of operators and types to prove the concept
//

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ExpressionDSL {
    public final static Class[] CLASS = new Class[0];
    public final static String[] STRING = new String[0];
    public final static Long[] LONG = new Long[0];
    public final static Boolean[] BOOLEAN = new Boolean[0];
    public final static Object[] OBJECT = new Object[0];
    public final static Expression[] EXPRESSION = new Expression[0];
    public final static MethodCall[] METHOD_CALL = new MethodCall[0];
    public final static Object[][] ARRAY_OF_OBJECT = new Object[0][];
    public final static byte[][] ARRAY_OF_BYTE = new byte[][] {};
    public final static Void[] VOID = new Void[0];

    public static <T> Expression<String> add(Expression<String> left, Expression<T> right) {
        return new AddOperator<>(left,right);
    }

    public static <T> LocalName local(String name, T[] type) {
        return new LocalName<>(name, type);
    }

    // this one doesn't exist in Java but it does in LiSP and we need it to do some kinds
    // of metaprogramming.

    public static <T> Expression<Expression<T>> quote(Expression<T> innerExpression) {
        return new Quote<>(innerExpression);
    }

    @SafeVarargs
    public static <T,S extends T> Expression<T[]> objectArray(T[] ofType, Expression<S>... parts) {
        return new ArrayExpression(parts, ofType);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static <T,S extends T> Expression<T[]> objectArrayExact(T[] ofType, Expression<T>[] parts) {
        return new ArrayExpression(parts, ofType);
    }

    public static UrClass defClass(String className) {
        return new UrClass(className);
    }

    public static <RR> UrMethodHeader<RR> method(String name,RR[] returnType) {
        return new UrMethodHeader<>(returnType, name);
    }

    public static <RR> UrMethodHeader<RR> method(String name, RR[] returnType, Type parameterizedReturnType) {
        return new UrMethodHeader<>(returnType, name, parameterizedReturnType);
    }

    public static <X> Expression<X> nil() {
        return new Null<>();
    }

    /**
     * Oddly the JDK doesn't have public constructors for ParameterizedType so
     * if we want to talk about them we must supply our own implementation.  If you
     * want to write something like
     *
     * Map &lt; String, Integer &gt;
     *
     * in source code you write
     *
     * reify(Map.class, String.class, Integer.class)
     *
     * note that this rewrites "void.class" into "Void.class" so we can talk about
     * a Expression &lt; Void &gt; when the reflection API tells us that the method
     * returns void.class.
     *
     * @param raw the class to be parameterized
     * @param parameters the type parameters
     * @return a fierce parameterized type
     */
    public static ParameterizedType reify(Class<?> raw, Type... parameters) {
        Type[] innerParameters = new Type[parameters.length];
        for(int i=0;i<parameters.length;i++) {
            Type typeParameter = parameters[i];
            if(typeParameter==void.class) {
                typeParameter = Void.class;
            }
            innerParameters[i] = typeParameter;
        }
        return new FierceParameterizedType(raw, innerParameters);
    }
}
