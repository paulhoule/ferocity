package com.ontology2.ferocity;

import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.lang.reflect.Type;

class UrMethodHeader<R> {
    final R[] returnType;
    final Type parameterizedReturnType;
    final String name;
    final PVector<ParameterDeclaration> parameters;

    UrMethodHeader(R[] returns, String name, Type parameterizedReturnType, PVector<ParameterDeclaration> parameters) {
        this.returnType = returns;
        this.name = name;
        this.parameterizedReturnType = parameterizedReturnType;
        this.parameters = parameters;
    }

    public UrMethodHeader(R[] returnType, String name, Type parameterizedReturnType) {
        this(returnType, name, parameterizedReturnType, TreePVector.empty());
    }

    public UrMethodHeader(R[] returnType, String name) {
        this(returnType, name, returnType.getClass().getComponentType(), TreePVector.empty());
    }

    public UrMethodHeader<R> receives(ParameterDeclaration pdecl) {
        return new UrMethodHeader<R>(returnType, name, parameterizedReturnType, parameters.plus(pdecl));
    }

    public UrMethod<R> withBody(Expression<R> body) {
        return new UrMethod(this, body);
    }

    public String asSource() {
        StringBuilder sb = new StringBuilder();
        sb.append("public static ");
        sb.append(parameterizedReturnType.getTypeName());
        sb.append(" ");
        sb.append(name);
        sb.append("(");
        for(int i=0;i<parameters.size();i++) {
            if (i>0)
                sb.append(",");
            sb.append(parameters.get(i).asSource());
        }
        sb.append(")");
        return sb.toString();
    }

}
