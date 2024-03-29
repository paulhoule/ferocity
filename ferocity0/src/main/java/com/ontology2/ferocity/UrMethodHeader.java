package com.ontology2.ferocity;

import org.pcollections.PVector;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import static com.ontology2.ferocity.Utility.appendItems;
import static org.pcollections.TreePVector.*;

class UrMethodHeader<R> {
    final R[] returnType;
    final Type parameterizedReturnType;
    final String name;
    final PVector<ParameterDeclaration<?>> parameters;
    final PVector<TypeVariable<?>> typeVariables;

    UrMethodHeader(R[] returns,
                   String name,
                   Type parameterizedReturnType,
                   PVector<ParameterDeclaration<?>> parameters,
                   PVector<TypeVariable<?>> typeVariables) {
        this.returnType = returns;
        this.name = name;
        this.parameterizedReturnType = parameterizedReturnType;
        this.parameters = parameters;
        this.typeVariables = typeVariables;
    }

    public UrMethodHeader(R[] returnType, String name, Type parameterizedReturnType) {
        this(returnType, name, parameterizedReturnType, empty(), empty());
    }

    public UrMethodHeader(R[] returnType, String name) {
        this(returnType, name, returnType.getClass().getComponentType(), empty(), empty());
    }

    public UrMethodHeader<R> receives(ParameterDeclaration<?> pdecl) {
        return new UrMethodHeader<>(returnType, name, parameterizedReturnType, parameters.plus(pdecl),typeVariables);
    }

    public UrMethodHeader<R> typeVariable(TypeVariable<?> tVar) {
        return new UrMethodHeader<>(returnType, name, parameterizedReturnType, parameters,typeVariables.plus(tVar));
    }

    public UrMethod<R> withBody(Expression<R> body) {
        return new UrMethod<>(this, body);
    }

    public String asSource() {
        StringBuilder sb = new StringBuilder();
        sb.append("public static ");
        if(!typeVariables.isEmpty()) {
            sb.append('<');
            boolean isFirst=true;
            for(TypeVariable<?> tVar:typeVariables) {
                if(isFirst) {
                    isFirst=false;
                } else {
                    sb.append(',');
                }
                sb.append(Utility.sourceName(tVar));
            }
            sb.append('>');
            sb.append(' ');
        }
        sb.append(Utility.sourceName(parameterizedReturnType));
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
