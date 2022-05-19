package com.ontology2.ferocity;

import java.lang.reflect.Type;

import static com.ontology2.ferocity.ExpressionDSL.*;
import static com.ontology2.ferocity.MethodCall.*;

@SuppressWarnings("rawtypes")
public class SelfDSL {
    public static <T,R> Expression<Expression<R>>  callCreateStaticMethodCall(
            Expression<T[]> thatClass,
            Expression<String> name,
            Expression<Class[]> parameters,
            Expression<Expression<?>[]> args) {
        Class[] innerParameters ={
                Object[].class, String.class, Class[].class, Expression[].class
        };
        Expression[] innerArguments = {
                thatClass, name, parameters, args
        };

        return createStaticMethodCall(
                METHOD_CALL,
                "createStaticMethodCall",
                innerParameters,
                innerArguments);
    }

    public static <T, R> Expression<Expression<R>>  callCreateMethodCall(
            Expression<T[]> thatClass,
            Expression<Expression<T>> that,
            Expression<String> name,
            Expression<Class[]> parameters,
            Expression<Expression<?>[]> args) {
        Class[] innerParameters ={
                Object[].class,
                Expression.class,
                String.class,
                Class[].class,
                Expression[].class
        };
        Expression[] innerArguments = {
                thatClass,
                that,
                name,
                parameters,
                args
        };

        return createStaticMethodCall(
                METHOD_CALL,
                "createMethodCall",
                innerParameters,
                innerArguments);
    }

    public static <R> Expression<Expression<R>> callCreateConstructorCall(
            Expression<R[]> thatClass,
            Expression<Class[]> parameters,
            Expression<Expression<?>[]> args) {
        Class[] innerParameters = {
                Object[].class, Class[].class, Expression[].class
        };
        Expression[] innerArguments = {
                thatClass, parameters, args
        };
        return createStaticMethodCall(
                CONSTRUCTOR_CALL,
                "createConstructorCall",
                innerParameters,
                innerArguments
        );
    }
//    /**
//     *
//     * @param name
//     * @param parameters
//     * @param that
//     * @param variableArgs in the wrong order because type inference doesn't work otherwise.  Note this is
//     *                     an expression that returns an array of objects
//     * @param args         an array of expressions.
//     * @param <Return>
//     * @return
//     */
//    public static <Return> Expression<Expression<Return>>  callCreateStaticMethodCallWithVarargs(
//            Expression<String> name,
//            Expression<Class[]> parameters,
//            Expression<Class> that,
//            Expression<Object[]> variableArgs,
//            Expression... args) {
//
//        Class[] innerParameters ={
//                String.class,
//                Class[].class,
//                Class.class,
//                Expression.class,    // these are the varargs of the method call we're creating
//                Expression[].class   // this is this function's own vararg but fixed args of the one we're calling
//        };
//
//        Expression[] innerArguments = {
//                name,
//                parameters,
//                that,
//                variableArgs
//        };
//
//        return createStaticMethodCallWithVarargs(
//                "createStaticMethodCallWithVarargs",
//                innerParameters,
//                MethodCall.class,
//                args,
//                innerArguments);
//    }

//    public static Expression<Expression> callCreateStaticMethodCallWithVarargs(String name,Class[] parameters,
//       Class that,  Expression[] variableArgs, Expression... args) {
//        return new StaticMethodCallNWithVarargs<>(name, parameters, that, variableArgs, args);
//    }
}
