package com.ontology2.ferocity;

import java.lang.reflect.Type;
import java.util.function.Function;

import static com.ontology2.ferocity.ParameterDeclaration.parameter;

@SuppressWarnings("FieldCanBeLocal")
public class LambdaFunction<In, Out> extends Lambda<Function<In, Out>> {
    private final Type inType;
    private final Type outType;
    private final Function<ParameterDeclaration<In>, Expression<Out>> fn;

    public LambdaFunction(Type inType, Type outType, Function<ParameterDeclaration<In>, Expression<Out>> fn) {
        super(new ParameterDeclaration[] { parameter(inType, "arg0") });
        this.inType = inType;
        this.outType = outType;
        this.fn = fn;
    }

    //
    // quite a few things are wrong with this one:  (1) we create a whole new context every time we
    // make one of these,  which prevents parameters from leaking out but and (2) we make no effort to
    // prevent the inner function from throwing an Exception and (3) rather than being fully parametric
    // like the WrapperGenerator/MetrodCall complex I am hand coding different variants of the lambda
    // function
    //

    @Override
    public Function<In, Out> evaluate(Context ctx) throws Exception {
        return x -> {
            Context newCtx = new Context();
            newCtx.set("arg0", x);
            return buildFunctionDefinition().evaluateRT(newCtx);
        };
    }

    protected Expression<Out> buildFunctionDefinition() {
        //noinspection unchecked
        return fn.apply((ParameterDeclaration<In>) pdecl[0]);
    }
}
