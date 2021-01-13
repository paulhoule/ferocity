package com.ontology2.ferocity;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

public class Types {
    static Map<Class, Class> _box = new HashMap<>() {{
        put(boolean.class, Boolean.class);
        put(char.class, Character.class);
        put(byte.class, Byte.class);
        put(short.class, Short.class);
        put(int.class,Integer.class);
        put(long.class, Long.class);
        put(float.class, Float.class);
        put(double.class, Double.class);
        put(void.class, Void.class);
    }};

    public static Class box(Class raw) {
        if(raw.isPrimitive()) {
            return _box.get(raw);
        }
        return raw;
    }

    public static Object[] emptyArrayOf(Class that) {
        return (Object[]) Array.newInstance(box(that), 0);
    }
}
