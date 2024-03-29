package com.ontology2.ferocity;

import java.lang.reflect.Constructor;

import static com.ontology2.ferocity.Utility.appendItems;

public class ConstructorCall<R> extends Expression<R> {
    private final Class<R> thatClass;
    private final Class<?>[] parameters;
    private final Expression<?>[] arguments;

    ConstructorCall(R[] thatClass, Class<?>[] parameters, Expression<?>[] arguments) {
        //noinspection unchecked
        this.thatClass=(Class<R>) thatClass.getClass().getComponentType();
        this.parameters=parameters;
        this.arguments=arguments;
    }

    @Override
    public R evaluate(Context ctx) throws Exception {
        Object[] argValues=new Object[arguments.length];
        for(int i=0;i<arguments.length;i++) {
            argValues[i]=arguments[i].evaluate(ctx);
        }

        Constructor<R> ctor = thatClass.getConstructor(this.parameters);
        return ctor.newInstance(argValues);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("new ");
        sb.append(thatClass.getCanonicalName());
        if(thatClass.getTypeParameters().length>0) {
            sb.append("<>");
        }
        sb.append('(');
        appendItems(sb, arguments, Expression::asSource, ", ");
        sb.append(')');
        return sb.toString();
    }

    public static <R> ConstructorCall<R>  createConstructorCall(R[] thatClass, Class<?>[] parameters,  Expression<?>... arguments) {
        return new ConstructorCall<>(thatClass, parameters, arguments);
    }
}
