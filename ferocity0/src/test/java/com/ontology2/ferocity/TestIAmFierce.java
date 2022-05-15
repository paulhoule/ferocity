//package com.ontology2.ferocity;
//
//import org.junit.Test;
//
//import static com.ontology2.ferocity.ExpressionDSL.local;
//import static com.ontology2.ferocity.ExpressionDSL.quote;
//import static com.ontology2.ferocity.Literal.of;
//import static com.ontology2.ferocity.MethodCall.createStaticMethodCallWithVarargs;
//import static com.ontology2.ferocity.SelfDSL.callCreateStaticMethodCall;
//import static com.ontology2.ferocity.SelfDSL.callCreateStaticMethodCallWithVarargs;
//import static org.junit.Assert.assertEquals;
//
//public class IAmFierce {
//    final Class[] CLASS = new Class[0];
//    final String[] STRING = new String[0];
//    final Long[] LONG = new Long[0];
//    final Boolean[] BOOLEAN = new Boolean[0];
//    final Object[][] ARRAY_OF_OBJECT = new Object[0][];
//
//    @Test
//    public void testLocalName() throws Throwable {
//        Expression<Double> that = local("x", CLASS);
//        assertEquals("x", that.toString());
//        Context ctx = new Context();
//        ctx.set("x", java.util.HashMap.class);
//        assertEquals(java.util.HashMap.class, that.evaluate(ctx));
//    }
//    @Test
//    public void iAmNotAQuine() throws Throwable {
//        Expression<String> name = of("valueOf");
//        Expression<Class[]> parameters = of(new Class[] {boolean.class});
//        Expression<Class> string = of(String.class);
//        Expression[] args = new Expression[] {
//            quote(local("y", BOOLEAN))
//        };
//        Expression<Expression<String>> x = callCreateStaticMethodCall(name, parameters, string, args);
//        assertEquals("com.ontology2.ferocity.MethodCall.createStaticMethodCall(\"valueOf\",new java.lang.Class[] {boolean.class},java.lang.String.class,y)", x.toString());
//
//        Context ctx = new Context();
//        ctx.set("y", false);
//        // first evaluation doesn't look up the local name,  but the second one does
//        // the quote operator we use doesn't exist in real Java Expression trees,  but the
//        // effect is similar to compiling the code with javac and running it later.
//        Expression<String> innerX = x.evaluate();
//        assertEquals("false", innerX.evaluate(ctx));
//    }
//
//    @Test
//    public void varadicStatic0() throws Throwable {
//        Expression<String> that = MethodCall.createStaticMethodCallWithVarargs(
//                "format",
//                new java.lang.Class[] {java.lang.String.class, java.lang.Object[].class},
//                java.lang.String.class,
//                local("there", ARRAY_OF_OBJECT),
//                new Expression[] {local("format", STRING)}
//        );
//    }
//
//    @Test
//    public void varadicStatic() throws Throwable {
//        Expression<String> name = of("format");
//        Expression<Class[]> parameters=of(new Class[] {String.class, Object[].class});
//        Expression<Class> string=of(String.class);
//        Expression[] args = new Expression[] {quote(local("format", STRING))};
//        Expression<Object[]> varArgs = quote(local("there", ARRAY_OF_OBJECT));
//        Expression<Expression<String>> x = callCreateStaticMethodCallWithVarargs(name, parameters, string, varArgs, args);
//        assertEquals("com.ontology2.ferocity.MethodCall.createStaticMethodCallWithVarargs(\"format\",new java.lang.Class[] {java.lang.String.class, java.lang.Object[].class},java.lang.String.class,there,format)",x.toString());
//
//        Context ctx = new Context();
//        ctx.set("there", new Object[]{151});
//        ctx.set("format","we %d ew");
//        assertEquals("we 151 ew",x.evaluate(ctx));
//    }
//
////    publlc static void DoNothing() {
////
////        createStaticMethodCallWithVarargs(
////                "format",
////                new Class[]{String.class, Object[].class},
////                String.class,
////
////                format
////        );
////
////        com.ontology2.ferocity.MethodCall.createStaticMethodCallWithVarargs(
////                "format",
////                new java.lang.Class[] {java.lang.String.class, java.lang.Object[].class},
////                java.lang.String.class,
////                "we %d ew")
////
////    q}
//
////    }
//}
