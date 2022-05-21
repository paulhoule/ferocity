package com.ontology2.ferocity;

import java.lang.reflect.Type;
import java.util.function.Function;
import java.util.function.Supplier;

public class LambdaSupplier<Out> extends Lambda<Supplier<Out>> {
    private final Supplier<Expression<Out>> fn;

    public LambdaSupplier(Type outType, Supplier<Expression<Out>> fn) {
        super(new ParameterDeclaration[] {});
        this.fn = fn;
    }
    @Override
    public Supplier<Out> evaluate(Context ctx) throws Exception {
        return () -> buildFunctionDefinition().evaluateRT(ctx);
    }

    @Override
    protected Expression<Out> buildFunctionDefinition() {
        return fn.get();
    }
}
