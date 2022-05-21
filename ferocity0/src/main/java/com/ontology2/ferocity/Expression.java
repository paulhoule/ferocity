package com.ontology2.ferocity;

import java.lang.reflect.Array;

/***
 * Two things you can do with an expression are evaluate it and serialize it to text;
 *
 * @param <T>
 */

abstract public class Expression<T> {

    public T evaluate() throws Exception {
        return evaluate(new Context());
    }

    public T evaluateRT() {
        return evaluateRT(new Context());
    }

    public T evaluateRT(Context ctx) {
        try {
            return evaluate(ctx);
        } catch(Exception x) {
            switch(x) {
                case RuntimeException xx -> throw xx;
                case InterruptedException xx -> Thread.currentThread().interrupt();
                default -> throw new RuntimeException(x);
            }
        }
        return null;
    }

    public String asSource() {
        return toString();
    }

    abstract public T evaluate(Context ctx) throws Exception;

    // any Expression implements a toString() method that returns an executable expression
}

class Quote<T> extends Expression<Expression<T>> {
    private final Expression<T> innerExpression;
    Quote(Expression<T> innerExpression) {
        this.innerExpression=innerExpression;
    }

    @Override
    public Expression<T> evaluate(Context ctx) throws Exception {
        return innerExpression;
    }

    @Override
    public String toString() {
        return innerExpression.toString();
    }
}

/**
 * An ArrayExpression is an array of expressions which,  when evaluated,  becomes
 * an array of the values of all of those expressions,
 *
 * @param <T>
 */

class ArrayExpression<T> extends Expression<T[]> {
    private final Expression<T>[] parts;
    private final Class<?> type;

    ArrayExpression(Expression<T>[] parts, T[] sample) {
        this.parts = parts;
        this.type = sample.getClass().getComponentType();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("new ");
        sb.append(type.getCanonicalName());
        sb.append("[] {");
        for(int i=0;i<parts.length;i++) {
            if(i>0)
                sb.append(", ");

            sb.append(parts[i]);
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public T[] evaluate(Context ctx) throws Exception {
        //noinspection unchecked
        T[] result = (T[]) Array.newInstance(type, parts.length);
        for(int i=0;i<parts.length;i++) {
            Array.set(result, i, parts[i].evaluate());
        }
        return result;
    }
}

class Null<X> extends Expression<X> {
    public Null() {}
    @Override
    public X evaluate(Context ctx) {
        return null;
    }

    public String asSource() {
        return "null";
    }
}
