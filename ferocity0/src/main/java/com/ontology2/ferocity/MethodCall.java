package com.ontology2.ferocity;

import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import static com.ontology2.ferocity.Utility.appendItems;

public class MethodCall<R> extends Expression<R> {
    private final Class<?> thatClass;
    private final Expression<?> that;
    private final String name;
    private final Class<?>[] parameters;
    private final Expression<?>[] arguments;
    private final Type[] typeParameters;

    MethodCall(Class<?> thatClass, Expression<?> that, String name, Class<?>[] parameters, Expression<?>[] arguments) {
        this.thatClass = thatClass;
        this.that = that;
        this.name = name;
        this.parameters = parameters;
        this.arguments = arguments;
        this.typeParameters = new Type[] {};
    }

    MethodCall(Class<?> thatClass, Expression<?> that, String name, Class<?>[] parameters, Expression<?>[] arguments, Type[] typeParameters) {
        this.thatClass = thatClass;
        this.that = that;
        this.name = name;
        this.parameters = parameters;
        this.arguments = arguments;
        this.typeParameters = typeParameters;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(that == null) {
            sb.append(thatClass.getCanonicalName());
        } else {
            sb.append(that.asSource());
        }
        sb.append(".");

        if(typeParameters.length>0) {
            sb.append('<');
            appendItems(sb,typeParameters,Utility::sourceName,",");
            sb.append('>');
        }
        sb.append(name);
        sb.append("(");
        for(int i=0;i<arguments.length;i++) {
            if (i>0)
                sb.append(",");
            sb.append(arguments[i].asSource());
        }
        appendVariableArgumentsTo(sb);
        sb.append(")");
        return sb.toString();
    }

    void appendVariableArgumentsTo(StringBuilder sb) {
    }

    boolean hasArguments() {
        return this.arguments.length>0;
    }

    @Override
    public R evaluate(Context ctx) throws Exception {
        Object[] argValues = new Object[arguments.length];
        for(int i=0;i<arguments.length;i++) {
            argValues[i] = arguments[i].evaluate(ctx);
        }
        Method method = thatClass.getMethod(name, this.parameters);
        //noinspection unchecked
        return (R) method.invoke(that == null ? null : that.evaluate(ctx), argValues);
    }

    /**
     *
     * @param ctx context object
     * @param varargValues are evaluated values because the way we evaluate this
     *                     depends on if it is V[] or V... syntax
     * @param <V> array of varargValues
     * @return what the expression evaluates to
     * @throws Exception if anything goes wrtong
     */
    <V> R evaluateVaradic(Context ctx, V[] varargValues) throws Exception {
        Object[] argValues = new Object[arguments.length+1];
        for(int i=0;i<arguments.length;i++) {
            argValues[i] = arguments[i].evaluate(ctx);
        }
        argValues[arguments.length] = varargValues;
        Method method = thatClass.getMethod(name, this.parameters);
        //noinspection unchecked
        return (R) method.invoke(that == null ? null : that.evaluate(ctx), argValues);
    }

    public <V> OpenVariadic<R, V> withVarargs(V[] object) {
        return new OpenVariadic<>(object, this, TreePVector.empty());
    }

    public <V> ClosedVariadic<R, V> withVarargs(V[] object, Expression<V[]> variableArguments) {
        return new ClosedVariadic<>(object, this, variableArguments);
    }

    //
    // Shouldn't this check (1) that the number of parameters is right and (2) fit any type bounds?
    //
    // Also this should be implemented for constructors
    //
    public MethodCall<R> withTypeParameters(Type... typeParameters) {
        return new MethodCall<>(thatClass, that, name, parameters, arguments, typeParameters);
    }

    public static <R,T> MethodCall<R>  createMethodCall(T[] thatClass, Expression<? extends T> that, String name, Class<?>[] parameters,  Expression<?>... arguments) {
        return new MethodCall<>(thatClass.getClass().getComponentType(), that, name, parameters, arguments);
    }

    public static <R,T> MethodCall<R>  createStaticMethodCall(T[] thatClass, String name, Class<?>[] parameters,  Expression<?>... arguments) {
        return new MethodCall<>(thatClass.getClass().getComponentType(), null, name, parameters, arguments);
    }

}

abstract class Variadic<R, V> extends Expression<R> {
    final V[] varArray;
    final MethodCall<R> fixedPart;


    Variadic(V[] varArray, MethodCall<R> fixedPart) {
        this.varArray = varArray;
        this.fixedPart = fixedPart;
    }

    @Override
    public R evaluate(Context ctx) throws Exception {
        return fixedPart.evaluateVaradic(ctx, getVariadicValues(ctx));
    }

    abstract V[] getVariadicValues(Context ctx) throws Exception;
}

class OpenVariadic<R, V> extends Variadic<R,V> {
    final PVector<Expression<V>> variableArguments;
    OpenVariadic(V[] varArray, MethodCall<R> fixedPart, PVector<Expression<V>> variableArguments) {
        super(varArray, fixedPart);
        this.variableArguments = variableArguments;
    }

    @Override
    V[] getVariadicValues(Context ctx) throws Exception {
        //noinspection unchecked
        V[] values = (V[]) Array.newInstance(varArray.getClass().getComponentType(), variableArguments.size());
        for(int i=0;i<variableArguments.size();i++) {
            values[i] = variableArguments.get(i).evaluate();
        }
        return values;
    }

    public <A extends V> OpenVariadic<R, V> also(Expression<A> argument) {
        return new OpenVariadic<>(varArray, fixedPart, variableArguments.plus((Expression<V>) argument));
    }
}

class ClosedVariadic<R,V> extends Variadic<R, V> {
    final Expression<V[]> variableArguments;

    ClosedVariadic(V[] varArray, MethodCall<R> fixedPart, Expression<V[]> variableArguments) {
        super(varArray, fixedPart);
        this.variableArguments = variableArguments;
    }

    @Override
    V[] getVariadicValues(Context ctx) throws Exception {
        return this.variableArguments.evaluate(ctx);
    }
}
