package com.ontology2.ferocity;

// GOALS:
//
// - DSL to write Java expressions and serialize as Java source code
// - can compile time typing be useful here?
// - minimum number of operators and types to prove the concept
//

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.*;

import static com.ontology2.ferocity.MethodCall.createStaticMethodCall;

public class ExpressionDSL {
    @SuppressWarnings("rawtypes")
    public final static Class[] CLASS = new Class[0];
    public final static String[] STRING = new String[0];
    public final static Long[] LONG = new Long[0];
    public final static Boolean[] BOOLEAN = new Boolean[0];
    public final static Object[] OBJECT = new Object[0];
    @SuppressWarnings("rawtypes")
    public final static Expression[] EXPRESSION = new Expression[0];
    @SuppressWarnings("rawtypes")
    public final static MethodCall[] METHOD_CALL = new MethodCall[0];
    @SuppressWarnings("rawtypes")
    public final static ConstructorCall[] CONSTRUCTOR_CALL = new ConstructorCall[0];
    public final static Object[][] ARRAY_OF_OBJECT = new Object[0][];
    public final static byte[][] ARRAY_OF_BYTE = new byte[][] {};
    public final static Void[] VOID = new Void[0];

    public static <T> Expression<String> add(Expression<String> left, Expression<T> right) {
        return new AddOperator<>(left,right);
    }

    public static <T> LocalName<T> local(String name, T[] type) {
        return new LocalName<>(name, type);
    }

    // this one doesn't exist in Java but it does in LiSP and we need it to do some kinds
    // of metaprogramming.

    public static <T> Expression<Expression<T>> quote(Expression<T> innerExpression) {
        return new Quote<>(innerExpression);
    }

    @SafeVarargs
    public static <T,S extends T> Expression<T[]> objectArray(T[] ofType, Expression<S>... parts) {
        //noinspection unchecked,rawtypes
        return new ArrayExpression(parts, ofType);
    }

    @SuppressWarnings({"UnusedReturnValue", "unchecked", "rawtypes"})
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
     * Lambdas work remarkably well but they are still a bit of a sham.  In particular,  the types given
     * in arguments aren't really used now and they aren't checked at the first compile.  We might be able
     * to get away with not specifying the type at all,  but the ParameterDeclaration expects it.  We probably
     * should introduce a single object that holds a generic type as a generic type parameter and holds an
     * empty array to fix the raw type and the parameterized type.
     *
     * @param inType input type of the function we're creating
     * @param outType output type of function we're creating
     * @param fn a function that returns an expression of outType
     * @return an expression that creates a Function&lt;In,Out&gt;
     * @param <In> the input type (same as inType)
     * @param <Out> the output type (same as outType)
     */
    public static <In, Out> Expression<Function<In,Out>> lambdaFunction(Type inType, Type outType, Function<ParameterDeclaration<In>, Expression<Out>> fn) {
      return new LambdaFunction<>(inType, outType, fn);
    }

    public static <Out> Expression<Supplier<Out>> lambdaSupplier(Type outType, Supplier<Expression<? extends Out>> fn) {
        return new LambdaSupplier<>(outType, fn);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <In> Expression<Consumer<In>> lambdaConsumer(
            Type inType,
            Function<ParameterDeclaration<In>,
            Expression<Void>> fn) {
        return new LambdaConsumer(inType, fn);
    }

    public static <In0, In1> Expression<BiConsumer<In0, In1>> lambdaBiConsumer(
            Type in0Type,
            Type in1Type,
            BiFunction<ParameterDeclaration<In0>, ParameterDeclaration<In1>, Expression<Void>> fn) {
        //noinspection unchecked,rawtypes
        return new LambdaBiConsumer(in0Type, in1Type, fn);
    }

    public static <T> Expression<java.util.function.BinaryOperator<T>> lambdaBinaryOperator(
            Type type,
            BiFunction<ParameterDeclaration<T>, ParameterDeclaration<T>, Expression<T>> fn
    ) {
        return new LambdaBinaryOperator(type, fn);
    };

    public static <X> Expression<Void> discard(Expression<X> v) {
        return createStaticMethodCall(
                new com.ontology2.ferocity.Exports[]{},
                "discardIt",
                new Class[] {Object.class},
                v
        );
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
