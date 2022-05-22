package com.ontology2.ferocity;

import java.lang.reflect.Type;
import java.util.function.Function;
import java.util.function.Supplier;

public class LambdaSupplier<Out> extends Lambda<Supplier<Out>> {
    private final Supplier<Expression<? extends Out>> fn;

    public LambdaSupplier(Type outType, Supplier<Expression<? extends Out>> fn) {
        super(new ParameterDeclaration[] {});
        this.fn = fn;
    }
    @Override
    public Supplier<Out> evaluate(Context ctx) throws Exception {
        return () -> buildFunctionDefinition().evaluateRT(ctx);
    }

    @Override
    protected Expression<? extends Out> buildFunctionDefinition() {
        return fn.get();
    }
}
