package com.ontology2.ferocity;

import java.lang.annotation.Inherited;
import java.lang.reflect.Array;

/***
 * Two things you can do with an expression are evaluate it and serialize it to text;
 *
 * @param <T>
 */

abstract public class Expression<T> {

    public T evaluate() throws Throwable {
        return evaluate(new Context());
    }

    public String asSource() {
        return toString();
    }

    abstract public T evaluate(Context ctx) throws Throwable;

    // any Expression implements a toString() method that returns an executable expression
}

class LocalName<T> extends Expression<T> {
    private final String name;
    private final Class type;

    @Override
    public T evaluate(Context ctx) throws Throwable {
        if(ctx.has(name,type)) {
            return (T) ctx.get(name);
        } else {
            throw new FierceException("Couldn't find "+name+" with "+type+" in context");
        }
    }

    LocalName(String name, T[] type) {
        this.name = name;
        this.type = type.getClass().getComponentType();
    }

    @Override
    public String toString() {
        return name;
    }
}

class Quote<T> extends Expression<Expression<T>> {
    private final Expression<T> innerExpression;
    Quote(Expression<T> innerExpression) {
        this.innerExpression=innerExpression;
    }

    @Override
    public Expression<T> evaluate(Context ctx) throws Throwable {
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
    public T[] evaluate(Context ctx) throws Throwable {
        T[] result = (T[]) Array.newInstance(type, parts.length);
        for(int i=0;i<parts.length;i++) {
            Array.set(result, i, parts[i].evaluate());
        }
        return result;
    }
}

class Null<X> extends Expression<X> {
    public Null() {};
    @Override
    public X evaluate(Context ctx) throws Throwable {
        return null;
    }

    public String asSource() {
        return "null";
    }
}
