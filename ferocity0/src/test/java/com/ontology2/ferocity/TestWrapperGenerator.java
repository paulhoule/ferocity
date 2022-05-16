package com.ontology2.ferocity;

import org.junit.jupiter.api.Test;

import static com.ontology2.ferocity.WrapperGenerator.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.platform.commons.util.ReflectionUtils.isStatic;

public class TestWrapperGenerator {
    @Test
    public void tryBoolean() {
        var unique = deconflictMethods(Boolean.class);
        var logicalAnd = unique.get(new NameArity("logicalAnd", 2));
        assertEquals(Boolean.class, logicalAnd.getDeclaringClass());
        assertEquals("logicalAnd", logicalAnd.getName());
        assertFalse(isStatic(unique.get(new NameArity("hashCode",1))));
        assertTrue(isStatic(unique.get(new NameArity("hashCodeʌboolean",1))));
    }

    @Test
    public void tryRuntime() {
        var unique = deconflictMethods(Runtime.class);
        assertEquals(20, unique.size());
        var exec1 = unique.get(new NameArity("execʌStringʌStringʘʌFile",4));
        var exec2 = unique.get(new NameArity("execʌStringʘʌStringʘʌFile", 4));
        assertNotNull(exec1);
        assertNotNull(exec2);
        assertNotEquals(exec1,exec2);
        assertEquals("exec exec",exec1.getName()+ " " + exec2.getName());
        assertEquals(Process.class, exec1.getReturnType());
    }

    @Test
    public void tryProcess() {
        var unique = deconflictMethods(Process.class);
        assertEquals(22, unique.size());
        var info = unique.get(new NameArity("info",1));
        assertEquals(ProcessHandle.Info.class, info.getReturnType());
        assertEquals("java.lang.ProcessHandle$Info", info.getGenericReturnType().getTypeName());
        var u = wrapperForInstanceMethod(info, "info");
        var returns = expressionOf(info.getReturnType());

        assertEquals("com.ontology2.ferocity.Expression<java.lang.ProcessHandle$Info>", returns.getTypeName());
        assertEquals("public static com.ontology2.ferocity.Expression<java.lang.ProcessHandle.Info> " +
                "info(com.ontology2.ferocity.Expression<java.lang.Process> that)",u.header.asSource());

    }

    @Test
    public void tryEnum() {
        var unique = deconflictMethods(Enum.class);
        assertEquals(10, unique.size());
        var getDeclaringClass = unique.get(new NameArity("getDeclaringClass",1));
        assertEquals(Class.class, getDeclaringClass.getReturnType());
        var u = wrapperForInstanceMethod(getDeclaringClass, "xxx");

        assertEquals("public static <E extends java.lang.Enum<E>> com.ontology2.ferocity.Expression<java.lang.Class<E>> " +
                "xxx(com.ontology2.ferocity.Expression<java.lang.Enum> that)",u.header.asSource());

    }

}
