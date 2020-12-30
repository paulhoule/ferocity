package com.ontology2.ferocity;

/**
 * For now an UrMethod is always public and static
 * @param <R>
 */
public class UrMethod <R> {
    final UrMethodHeader header;
    final Expression<R> body;

    UrMethod(UrMethodHeader header, Expression<R> body) {
        this.header = header;
        this.body = body;
    }

    public String asSource() {
        StringBuilder sb = new StringBuilder();
        sb.append(header.asSource());
        sb.append(" {");
        sb.append("\n");
        sb.append("   return ");
        sb.append(body.asSource());
        sb.append(";\n");
        sb.append("}");

        return sb.toString();
    }
}

