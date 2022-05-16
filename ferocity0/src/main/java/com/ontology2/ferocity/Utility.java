package com.ontology2.ferocity;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.function.Function;

public class Utility {
    public static <X> void appendItems(StringBuilder a, Iterable<X> s, Function<X,String> fn, CharSequence delimiter) {
        boolean isFirst=true;
        for(var item: s) {
            if(isFirst) {
                isFirst=false;
            } else {
                a.append(delimiter);
            }
            a.append(fn.apply(item));
        }
    }

    public static <X> void appendItems(StringBuilder a, X[] s, Function<X,String> fn, CharSequence delimiter) {
        boolean isFirst=true;
        for(var item: s) {
            if(isFirst) {
                isFirst=false;
            } else {
                a.append(delimiter);
            }
            a.append(fn.apply(item));
        }
    }

    static String sourceName(Type that) {
        return that.getTypeName().replace("$",".");
    }

    static String sourceName(TypeVariable<?> that) {
        if(that.getBounds().length==0)
            return that.getName();

        StringBuilder b = new StringBuilder(that.getName());
        b.append(" extends ");
        appendItems(b, that.getBounds(), Utility::sourceName, " & ");
        return b.toString();
    }
}
