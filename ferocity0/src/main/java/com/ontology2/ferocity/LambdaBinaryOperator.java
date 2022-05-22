package com.ontology2.ferocity;

import java.lang.reflect.Type;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

import static com.ontology2.ferocity.ParameterDeclaration.parameter;

public class LambdaBinaryOperator<T> extends Lambda<BinaryOperator<T>> {
    private final Type type;
    private final BiFunction<ParameterDeclaration<T>, ParameterDeclaration<T>, Expression<T>> fn;

    public LambdaBinaryOperator(Type type, BiFunction<ParameterDeclaration<T>,
            ParameterDeclaration<T>, Expression<T>> fn) {
        super(new ParameterDeclaration[]{ parameter(type, "arg0"), parameter(type, "arg1") });
        this.type = type;
        this.fn = fn;
    }

    @Override
    public BinaryOperator<T> evaluate(Context ctx) throws Exception {
        return (x,y) -> {
            Context newCtx = ctx.copyOf();
            newCtx.set("arg0", x);
            newCtx.set("arg1", y);
            return buildFunctionDefinition().evaluateRT(newCtx);
        };
    }

    @Override
    protected Expression<T> buildFunctionDefinition() {
        return fn.apply(
                (ParameterDeclaration<T>) pdecl[0],
                (ParameterDeclaration<T>) pdecl[1]
        );
    }
}
