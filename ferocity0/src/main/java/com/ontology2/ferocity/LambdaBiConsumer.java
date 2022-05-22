package com.ontology2.ferocity;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.ontology2.ferocity.ParameterDeclaration.parameter;

@SuppressWarnings("FieldCanBeLocal")
public class LambdaBiConsumer<In0,In1> extends Lambda<BiConsumer<In0,In1>> {
    private final Type in0Type;
    private final Type in1Type;
    private final BiFunction<ParameterDeclaration<In0>, ParameterDeclaration<In1>, Expression<Void>> innerConsumer;

    public LambdaBiConsumer(Type in0Type, Type in1Type, BiFunction<ParameterDeclaration<In0>, ParameterDeclaration<In1>, Expression<Void>> innerConsumer) {
        super(new ParameterDeclaration[]{ parameter(in0Type, "arg0"), parameter(in1Type, "arg1") });
        this.in0Type = in0Type;
        this.in1Type = in1Type;
        this.innerConsumer = innerConsumer;
    }

    @Override
    public BiConsumer<In0, In1> evaluate(Context ctx) throws Exception {
        return (x,y) -> {
            Context newCtx = ctx.copyOf();
            newCtx.set("arg0", x);
            newCtx.set("arg1", y);
            buildFunctionDefinition().evaluateRT(newCtx);
        };
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected Expression buildFunctionDefinition() {
        return innerConsumer.apply(
                (ParameterDeclaration<In0>) pdecl[0],
                (ParameterDeclaration<In1>) pdecl[1]
        );
    }
}
