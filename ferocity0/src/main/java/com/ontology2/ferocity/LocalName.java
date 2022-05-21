package com.ontology2.ferocity;

public class LocalName<T> extends Expression<T> {
    private final String name;
    private final Class<?> type;

    @Override
    public T evaluate(Context ctx) throws Exception {
        if (ctx.has(name, type)) {
            //noinspection unchecked
            return (T) ctx.get(name);
        } else {
            throw new FierceException("Couldn't find " + name + " with " + type + " in context");
        }
    }

    LocalName(String name, T[] type) {
        this.name = name;
        this.type = type.getClass().getComponentType();
    }

    @Override
    public String toString() {
        return name;
    }
}
