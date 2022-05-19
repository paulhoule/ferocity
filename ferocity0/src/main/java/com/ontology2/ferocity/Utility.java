package com.ontology2.ferocity;

import java.lang.reflect.*;
import java.util.function.Function;

import static com.ontology2.ferocity.ExpressionDSL.reify;
import static com.ontology2.ferocity.FierceWildcard.anyType;
import static com.ontology2.ferocity.Types.box;

public class Utility {
    public static <X> void appendItems(StringBuilder a, Iterable<X> s, Function<X,String> fn, CharSequence delimiter) {
        boolean isFirst=true;
        for(var item: s) {
            if(isFirst) {
                isFirst=false;
            } else {
                a.append(delimiter);
            }
            a.append(fn.apply(item));
        }
    }

    public static <X> void appendItems(StringBuilder a, X[] s, Function<X,String> fn, CharSequence delimiter) {
        boolean isFirst=true;
        for(var item: s) {
            if(isFirst) {
                isFirst=false;
            } else {
                a.append(delimiter);
            }
            a.append(fn.apply(item));
        }
    }

    static String sourceName(Type that) {
        return that.getTypeName().replace("$",".");
    }

    static String sourceName(TypeVariable<?> that) {
        if(that.getBounds().length==0)
            return that.getName();

        StringBuilder b = new StringBuilder(that.getName());
        b.append(" extends ");
        appendItems(b, that.getBounds(), Utility::sourceName, " & ");
        return b.toString();
    }

    static boolean isFinal(Class c) {
        return Modifier.isFinal(c.getModifiers());
    }
    static boolean isInterface(Class c) {
        return Modifier.isInterface(c.getModifiers());
    }

    static boolean isPublic(Executable m) {
        return Modifier.isPublic(m.getModifiers());
    }

    static boolean isStatic(Method m) {
        return Modifier.isStatic(m.getModifiers());
    };

    static Type getExpandedParameterType(Type t) {
        if (t instanceof Class c) {
            if(c.isPrimitive() || isFinal(c)) {
                return t;
            }
        }
        return anyType().boundedAboveBy(t);
    }

    static Type parameterExpressionType(Type t) {
        return expressionOf(getExpandedParameterType(t));
    }

    static Type parameterExpressionType(Parameter p) {
        return parameterExpressionType(p.getParameterizedType());
    }

    static Type expressionOf(Type t) {
        if(t instanceof Class<?> c) {
            if (c.isPrimitive()) {
                return reify(Expression.class,box(c));
            }
        }
        return reify(Expression.class, t);
    }
}
