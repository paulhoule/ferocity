package com.ontology2.ferocity;

import static com.ontology2.ferocity.Utility.appendItems;

abstract public class Lambda<ReturnsFunction> extends Expression<ReturnsFunction> {
    protected final ParameterDeclaration<?>[] pdecl;

    protected Lambda(ParameterDeclaration<?>[] pdecl) {
        this.pdecl = pdecl;
    }

    @Override
    public String asSource() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        appendItems(sb, pdecl, ParameterDeclaration::asSource, ", ");
        sb.append(") -> ");
        sb.append(buildFunctionDefinition().asSource());
        return sb.toString();
    }

    @SuppressWarnings("rawtypes")
    protected abstract Expression buildFunctionDefinition();
}
