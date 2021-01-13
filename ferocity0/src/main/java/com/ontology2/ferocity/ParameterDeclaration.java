package com.ontology2.ferocity;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.ontology2.ferocity.ExpressionDSL.local;
import static com.ontology2.ferocity.Types.box;

public class ParameterDeclaration<P> {
    final P[] parameterType;
    private final Type parameterizedType;
    final String parameterName;
    final boolean isVararg;
    ParameterDeclaration(P[] parameterType, Type parameterizedType, String parameterName, boolean isVararg) {
        this.parameterType = parameterType;
        this.parameterizedType = parameterizedType;
        this.parameterName = parameterName;
        this.isVararg = isVararg;
    }

    ParameterDeclaration(P[] parameterType, String parameterName, boolean isVararg) {
        this(parameterType, parameterType.getClass().getComponentType(), parameterName, isVararg);
    }

    static <PP> ParameterDeclaration<PP> parameter(PP[] type, String name) {
        return new ParameterDeclaration<PP>(type, name, false);
    }

    static <X> ParameterDeclaration<?> parameter(Type parameterizedType, String name) {
        //
        // create the object array corresponding to the type
        //
        var raw = (Class) (parameterizedType instanceof ParameterizedType
                ? ((ParameterizedType) parameterizedType).getRawType()
                : parameterizedType);
        raw = (Class) box(raw);
        Object any = Array.newInstance((Class) raw,0);
        return new ParameterDeclaration((X[]) any, parameterizedType, name, false);
    }



    static <PP> ParameterDeclaration<PP> parameter(PP[] type, Type parameterizedType, String name) {
        return new ParameterDeclaration<PP>(type,parameterizedType, name,false);
    }

    static <PP> ParameterDeclaration<PP> variableParameter(PP[] type, String name) {
        return new ParameterDeclaration<PP>(type, name, true);
    }

    static <PP> ParameterDeclaration<PP> variableParameter(PP[] type, Type parameterizedType, String name) {
        return new ParameterDeclaration<PP>(type, parameterizedType, name, true);
    }

    public String asSource() {
        StringBuilder sb = new StringBuilder();
        buildSource(sb);
        return sb.toString();
    }

    public void buildSource(StringBuilder sb) {
        sb.append(parameterizedType.getTypeName());
        sb.append(" ");
        sb.append(parameterName);
    }

    public Expression<P> reference() {
        return local(parameterName, parameterType);
    }
}
