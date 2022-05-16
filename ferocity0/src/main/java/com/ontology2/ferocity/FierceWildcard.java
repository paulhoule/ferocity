package com.ontology2.ferocity;

import org.pcollections.ConsPStack;
import org.pcollections.PStack;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

import static com.ontology2.ferocity.Utility.appendItems;
import static com.ontology2.ferocity.Utility.sourceName;

public record FierceWildcard(PStack<Type> upperBounds, PStack<Type> lowerBounds) implements WildcardType {
    @Override
    public Type[] getUpperBounds() {
        return upperBounds.toArray(new Type[] {});
    }

    @Override
    public Type[] getLowerBounds() {
        return lowerBounds.toArray(new Type[] {});
    }

    @Override
    public String getTypeName() {
        StringBuilder sb = new StringBuilder("?");
        if(!upperBounds.isEmpty()) {
            sb.append(" extends ");
            appendItems(sb, upperBounds, Utility::sourceName, " & ");
        }
        if(!lowerBounds.isEmpty()) {
            sb.append(" extends ");
            appendItems(sb, lowerBounds, Utility::sourceName, " & ");
        }
        return sb.toString();
    }

    public static FierceWildcard anyType() {
        return new FierceWildcard(ConsPStack.empty(),ConsPStack.empty());
    }

    public FierceWildcard boundedAboveBy(Type t) {
        return new FierceWildcard(upperBounds.plus(t), lowerBounds);
    }

    public FierceWildcard boundedBelowBy(Type t) {
        return new FierceWildcard(upperBounds, lowerBounds.plus(t));
    }
}
