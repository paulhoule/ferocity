package com.ontology2.ferocity;

import org.pcollections.PVector;
import org.pcollections.TreePVector;

/**
 * Enough of an implementation of a Java class that we can compile stub classes
 * for the Java standard library in it
 */

public class UrClass {
    private final String qualifiedName;
    private final PVector<UrMethod> methods;

    public UrClass(String qualifiedName) {
        this(qualifiedName, TreePVector.empty());
    }

    UrClass(String qualifiedName, PVector<UrMethod> methods) {
        this.qualifiedName = qualifiedName;
        if(!this.qualifiedName.contains(".")) {
            throw new IllegalArgumentException("Must specify a qualified class name");
        }
        this.methods = methods;
    }

    public UrClass def(UrMethod method) {
        return new UrClass(qualifiedName, methods.plus(method));
    }

    public String asSource() {
        StringBuilder sb=new StringBuilder();
        int lastPoint = qualifiedName.lastIndexOf(".");
        String packageName = qualifiedName.substring(0, lastPoint);
        String className = qualifiedName.substring(lastPoint+1);
        sb.append("package "+packageName+";\n\n");
        sb.append("public class "+className+ " {\n");
        for(UrMethod m: methods) {
            sb.append(m.asSource());
            sb.append("\n");
        }
        sb.append("}\n");
        return sb.toString();
    }
}
