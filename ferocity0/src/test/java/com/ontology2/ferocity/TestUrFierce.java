package com.ontology2.ferocity;

import org.junit.jupiter.api.Test;

import static com.ontology2.ferocity.ExpressionDSL.*;
import static com.ontology2.ferocity.Literal.of;
import static com.ontology2.ferocity.ParameterDeclaration.parameter;
import static com.ontology2.ferocity.SelfDSL.callCreateMethodCall;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUrFierce {
    @Test
    public void buildASimpleClass() {
        UrClass c = new UrClass("com.example.ThatStub");
        assertEquals("""
                package com.example;

                public class ThatStub {
                }
                """, c.asSource());
    }

    @Test
    public void buildASimpleMethod() {
        UrMethod<String> m = method( "worms", STRING).withBody(of("Pink isn't well"));
        assertEquals("""
                public static java.lang.String worms() {
                   return "Pink isn't well";
                }""", m.asSource());
    }

    @Test
    public void aParameterDeclaration() {
        ParameterDeclaration<String> pd = parameter(STRING, "crabs");
        assertEquals("java.lang.String crabs",pd.asSource());
    }

    @Test
    public void methodWithParameter() {
        UrMethod<String> m =
                method("worms", STRING)
                .receives(parameter(STRING, "crabs"))
                .withBody(of("He's back at the hotel"));
        assertEquals("""
                public static java.lang.String worms(java.lang.String crabs) {
                   return "He's back at the hotel";
                }""", m.asSource());
    }

    @Test
    public void methodWithParameterForReal() {
        ParameterDeclaration<String> crabs = parameter(STRING, "crabs");
        UrMethod<String> m =
                method( "worms", STRING)
                        .receives(crabs)
                        .withBody(add(of("Waiting for the "), crabs.reference()));
        assertEquals("""
                public static java.lang.String worms(java.lang.String crabs) {
                   return ("Waiting for the "+crabs);
                }""", m.asSource());
    }



    @Test
    public void defineAClass() {
        ParameterDeclaration<Expression<String>> that = parameter(
                EXPRESSION,
                reify(Expression.class, String.class),
                "that"
        );
        var xx = callCreateMethodCall(
                objectArray(STRING),
                that.reference(),
                of("getBytes"),
                objectArray(CLASS),
                objectArray(EXPRESSION)
        );
        UrClass uc = defClass("com.ontology2.FierceString");
        uc = uc.def(method("callGetBytes", (Expression<byte[]>[]) EXPRESSION, reify(Expression.class, byte[].class))
                .receives(that).withBody(xx));
        assertEquals("""
                package com.ontology2;

                public class FierceString {
                public static com.ontology2.ferocity.Expression<byte[]> callGetBytes(com.ontology2.ferocity.Expression<java.lang.String> that) {
                   return com.ontology2.ferocity.MethodCall.createMethodCall(new java.lang.String[] {},that,"getBytes",new java.lang.Class[] {},new com.ontology2.ferocity.Expression[] {});
                }
                }
                """, uc.asSource());
    }
}
