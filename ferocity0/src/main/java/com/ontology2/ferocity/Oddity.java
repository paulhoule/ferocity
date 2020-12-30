package com.ontology2.ferocity;

import java.lang.reflect.Method;
import java.util.List;

abstract class AbstractOddity {
    AbstractOddity getAnother() { return null; };
    abstract Boolean football();
}

public class Oddity extends AbstractOddity {
    Oddity getAnother() {
        return null;
    }

    Boolean football() {
        return false;
    }

    public static void main(String argv[]) {
        for(Method m: Oddity.class.getDeclaredMethods()) {
            System.out.println(m);
        }
    }
}
