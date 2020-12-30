package com.ontology2.ferocity;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;

/**
 * The current literal implementation is not aware of details such as the fact
 * that both "0x10" and "16" are ways to write the same integer value as a
 * literal.  The compiler doesn't care which one you use,  and they both
 * evaluate to the same things,  but if you want hex or octal as a matter of
 * style this class doesn't do that.
 *
 * @param <T>
 */
public class Literal<T> extends Expression<T> {
    T value;

    static Set<Class> supportedClasses = new HashSet<>() {{
        add(Boolean.class);
        add(Byte.class);
        add(Short.class);
        add(String.class);
        add(Class.class);
        add(Long.class);
        add(Integer.class);
        add(Character.class);
    }};
    protected Literal(T value) {
        this.value = value;
    };

    @Override
    public T evaluate(Context ctx) {
        return value;
    }

    @Override
    public String toString() {
        return literalizeAny(value);
    }

    static String literalizeAny(Object value) {
        if(value==null)
            return "null";

        if(value.getClass().isArray()) {
            return literalizeObjectArray((Object[]) value);
        }

        if(value instanceof String) {
            String that = literalize((String) value);
            return that;
        }
        if(value instanceof Long) {
            return value+"L";
        }
        if(value instanceof Character) {
            return literalize((Character) value);
        }
        if(value instanceof Float) {
            return value+"f";
        }
        if(value instanceof Class) {
            return ((Class) value).getCanonicalName() + ".class";
        }

        return value.toString();
    }

    static private String literalize(Character value) {
        StringBuilder that = new StringBuilder();
        that.append("'");
        if(value=='\'' || value=='\\') {
            that.append("\\");
            that.append(value);
        } else if(value=='\n') {
            that.append("\\n");
        } else if(value=='\r') {
            that.append("\\r");
        } else {
            that.append(value);
        }
        that.append("'");
        return that.toString();
    }

    static private String literalize(String value) {
        StringBuilder that = new StringBuilder();
        that.append('"');
        for(int i=0;i<value.length();i++) {
            char c=value.charAt(i);
            if(c=='"' || c=='\\' || c=='\n' || c=='\r') {
                that.append('\\');
            }
            if(c=='\r') {
                that.append("r");
                break;
            }
            if(c=='\n') {
                that.append("n");
                break;
            }

            that.append(c);
        }
        that.append('"');
        return that.toString();
    }

    static private String literalizeObjectArray(Object[] value) {
        Class componentType = value.getClass().getComponentType();
        if(!supportedClasses.contains(componentType)) {
            throw new IllegalArgumentException("I don't know how to make literals of " + componentType + " arrays");
        }

        StringBuilder that = new StringBuilder();
        that.append("new ");
        that.append(componentType.getCanonicalName());
        that.append("[] {" );
        for(int i=0;i<value.length;i++) {
            if(i>0) {
                that.append(", ");
            }
            that.append(literalizeAny(value[i]));
        }
        that.append("}");
        return that.toString();
    }

    public static Literal<String> of(String value) {
        return new Literal(value);
    }

    // There are integer and long literals,  but you cast to make smaller types

    public static Literal<Long> of(Long value) {
        return new Literal(value);
    }

    public static Literal<Integer> of(Integer value) {
        return new Literal(value);
    }

    public static Literal<Boolean> of(Boolean value) {
        return new Literal(value);
    }

    public static Literal<Character> of(Character value) {
        return new Literal(value);
    }

    public static Literal<Float> of(Float value) {
        return new Literal(value);
    }

    public static Literal<Double> of(Double value) {
        return new Literal(value);
    }

    public static Literal<Object> ofNull() {
        return new Literal(null);
    }

    public static Literal<Class> of(Class value) { return new Literal(value); }

    public static Literal<Class[]> of(Class[] value) {
        return new Literal(value);
    }
}
