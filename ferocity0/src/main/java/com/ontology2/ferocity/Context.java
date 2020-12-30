package com.ontology2.ferocity;

import java.util.HashMap;
import java.util.Map;

/***
 * Represents the context that an expression is evaluated in,  roughly this is going to
 * be a hashtable that maps identifiers to objects
 */

public class Context {
    Map<String, Object> content = new HashMap<>();

    public Context() {
    }

    public void set(String name, Object value) {
        content.put(name ,value);
    };

    public boolean has(String name, Class ofType) {
        return content.containsKey(name)
            && ofType.isAssignableFrom(content.get(name).getClass());
    };

    public Object get(String name) {
        return content.get(name);
    }
}
