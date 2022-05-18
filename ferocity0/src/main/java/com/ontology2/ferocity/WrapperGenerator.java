package com.ontology2.ferocity;

import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;

import static com.ontology2.ferocity.DefaultMap.newListMultiMap;
import static com.ontology2.ferocity.ExpressionDSL.*;
import static com.ontology2.ferocity.FierceWildcard.anyType;
import static com.ontology2.ferocity.Literal.of;
import static com.ontology2.ferocity.ParameterDeclaration.parameter;
import static com.ontology2.ferocity.SelfDSL.callCreateMethodCall;
import static com.ontology2.ferocity.SelfDSL.callCreateStaticMethodCall;
import static com.ontology2.ferocity.Types.box;
import static com.ontology2.ferocity.Utility.*;
import static java.nio.file.Files.*;
import static java.util.function.Predicate.*;

record NameArity(String name, int arity) {
    public NameArity with(String newName) {
        return new NameArity(newName, this.arity);
    }
    public static NameArity of(Constructor<?> c) {
        return new NameArity("", c.getParameterCount());
    }
}

record Signature(String name, List<Class<?>> arguments) {
    public static Signature of(Class<?> c,Method m) {
        List<Class<?>> arguments = new ArrayList<>();
        if(!isStatic(m)) {
            arguments.add(c);
        }
        for(int i=0;i<m.getParameterCount();i++) {
            arguments.add(m.getParameters()[i].getType());
        }
        return new Signature(m.getName(),arguments);
    }
}

public class WrapperGenerator {
    private static String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    @SuppressWarnings("unchecked")
    private static final Expression<byte[]>[] EXPRESSION_OF_ARRAY_OF_BYTE = new Expression[]{};

    public WrapperGenerator() {
    }

    public void generate(Path dir, String s) throws IOException, ClassNotFoundException {
        Path p = dir.resolve("classes.txt");
        Path target= dir.resolve(s);
        List<String> classList = readAllLines(p.toAbsolutePath());
        for(final String qualifiedName: classList) {
            if(qualifiedName.length() == 0)
                continue;

            try {
                Class<?> c = WrapperGenerator.class.getClassLoader().loadClass(qualifiedName);
                if ((c.getModifiers() & Modifier.PUBLIC) != 0) {
                    processClass(c, target);
                }
            } catch(ClassNotFoundException notFound) {
                System.out.println("Failed to look up class "+qualifiedName);
            }
        }
    }
    private void processClass(Class<?> c, Path target) throws IOException {
        if(!isInterface(c)) {
            var namedMethods = deconflictMethods(c);
            var namedConstructors = deconflictConstructors(c);
            //
            // add support for fields
            //
            //

            UrClass uc = new UrClass("\uD835\uDD23."+c.getName());
            for(var method: namedMethods.entrySet()) {
                uc = updateClassForMethod(uc, method.getKey(), method.getValue());
            }
            uc.writeToSourceFile(target);
        }
    }

    static Map<NameArity,Method> deconflictMethods(Class<?> c) {
        Map<NameArity, List<Method>> methodGroups = newListMultiMap();
        for(final Method m: c.getDeclaredMethods()) {
            if(!isPublic(m)) {
                continue;
            }
            int arity = m.getParameterCount();
            if((m.getModifiers() & Modifier.STATIC)==0) {
                arity += 1;
            }
            var key = new NameArity(m.getName(),arity);
            methodGroups.get(key).add(m);
        }
        Map<NameArity, Method> namedMethods = new HashMap<>();
        for(var item: methodGroups.entrySet()) {
            if(item.getValue().size()>1) {
                for(var that: deconflictMethodGroup(c,item).entrySet()) {
                    namedMethods.put(that.getKey(), (Method) that.getValue());
                }
            } else {
                namedMethods.put(item.getKey(), item.getValue().get(0));
            }
        }
        return namedMethods;
    }

    Map<NameArity, Executable> deconflictConstructors(Class<?> c) {
        Map<NameArity, List<Executable>> ctorGroups= newListMultiMap();
        for(Constructor<?> ctor: c.getDeclaredConstructors()) {
            if(!isPublic(ctor)) {
                continue;
            }
            ctorGroups.get(NameArity.of(ctor)).add(ctor);
        }

        Map<NameArity, Executable> namedCtor = new HashMap<>();
        for(var item: ctorGroups.entrySet()) {
            if(item.getValue().size()>1) {
                namedCtor.putAll(deconflictConstructorGroup(item));
            } else {
                namedCtor.put(item.getKey(), item.getValue().get(0));
            }
        }
        return namedCtor;
    }

    private Map<NameArity, Executable> deconflictConstructorGroup(Map.Entry<NameArity, List<Executable>> item) {
        return getNameArityExecutableMap(item.getKey(), item.getValue());
    }

    static Map<NameArity, Executable> deconflictMethodGroup(Class<?> c, Map.Entry<NameArity, List<Method>> g) {
        List<Executable> filteredMethods = removeMethodsWithSuperclassReturns(c, g.getValue());
        return getNameArityExecutableMap(g.getKey(), filteredMethods);
    }

    static Map<NameArity, Executable> getNameArityExecutableMap(NameArity a, List<Executable> filteredMethods) {
        Map<NameArity, Executable> result = new HashMap<>();
        for(var m:filteredMethods) {
            StringBuilder newName = new StringBuilder(a.name());
            for(int i=0;i<m.getParameterCount();i++) {
                newName.append("ʌ");
                newName.append(m.getParameters()[i].getType().getSimpleName().replace("[]","ʘ"));
            }
            result.put(a.with(newName.toString()),m);
        }
        return result;
    }

    //
    // a related scenario which is not being handled yet is one where a class inherits from
    // (say) the interface Comparable<T> and we wind up with a compareTo<Object> and a
    // compareTo<ThisClass> example.
    //
    static List<Executable> removeMethodsWithSuperclassReturns(Class<?> c, List<Method> g) {
        Map<Signature, List<Method>> world = new HashMap<>();
        BiConsumer<Signature, Method> insert = (Signature type, Method method) -> {
            if(!world.containsKey(type)) {
                world.put(type, new ArrayList<>());
            }
            world.get(type).add(method);
        };

        for(Method m: g) {
            insert.accept(Signature.of(c, m), m);
        }
        ArrayList<Executable> filteredMethods = new ArrayList<>();
        for(Signature s:world.keySet()) {
            var methodSet = world.get(s);
            Method mostSpecific = methodSet.get(0);
            for(int i=1;i<methodSet.size();i++) {
                Method nextMethod=methodSet.get(i);
                if(mostSpecific.getReturnType().isAssignableFrom(nextMethod.getReturnType())) {
                    mostSpecific = nextMethod;
                }
            }
            filteredMethods.add(mostSpecific);
        }
        return filteredMethods;
    }
    private UrClass updateClassForMethod(UrClass uc, NameArity key, Method m) {
        String name = "call" + capitalize(key.name());
        System.out.println(m+" "+m.getModifiers());
        return uc.def(wrapperForMethod(m, name));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static UrMethod<?> wrapperForMethod(Method m, String name) {
        var target = m.getDeclaringClass();
        var header = method(name, EXPRESSION, expressionOf(m.getGenericReturnType()));

        for(TypeVariable<?> tVar: getTypeVariables(m, target)) {
                header = header.typeVariable(tVar);
        }

        ParameterDeclaration<Expression<?>> that=null;
        if(!isStatic(m)) {
            that = parameter(EXPRESSION, reify(Expression.class, target), "that");
            header = header.receives(that);
        }

        Expression<Expression<?>>[] arguments = new Expression[m.getParameterCount()];
        for(int i = 0; i< m.getParameterCount(); i++) {
            var p = m.getParameters()[i];
            var pdecl = parameter(parameterExpressionType(p),p.getName());
            header = header.receives(pdecl);
            arguments[i] = (Expression<Expression<?>>) pdecl.reference();
        }

        //noinspection unchecked,RedundantCast,SwitchStatementWithTooFewBranches
        return header.withBody((Expression) switch(that) {
            case null -> callCreateStaticMethodCall(
                    objectArray((Object[]) Array.newInstance(target, 0)),
                    of(m.getName()),
                    of(m.getParameterTypes()),
                    objectArray(EXPRESSION, arguments));
            case default -> callCreateMethodCall(
                    objectArray((Object[]) Array.newInstance(target, 0)),
                    (Expression) that.reference(),
                    of(m.getName()),
                    of(m.getParameterTypes()),
                    objectArray(EXPRESSION, arguments));
        });
    }

    private static List<TypeVariable<?>> getTypeVariables(Method m, Class<?> target) {
        List<TypeVariable<?>> typeVariables = new ArrayList<>();
        Map<String,TypeVariable<?>> tv = new HashMap<>();
        if(!isStatic(m)) {
            for(TypeVariable<?> tVar: target.getTypeParameters()) {
                typeVariables.add(tVar);
                tv.put(tVar.getName(), tVar);
            }
        }
        for(TypeVariable<?> tVar: m.getTypeParameters()) {
            typeVariables.add(tVar);
            tv.put(tVar.getName(), tVar);
        }

        typeVariables.removeIf(not(tv::containsValue));
        return typeVariables;
    }

    private static Type getExpandedParameterType(Type t) {
        if (t instanceof Class c) {
            if(c.isPrimitive() || isFinal(c)) {
                return t;
            }
        }
        return anyType().boundedAboveBy(t);
    }

    private static Type parameterExpressionType(Parameter p) {
        return expressionOf(getExpandedParameterType(p.getParameterizedType()));
    }

    static Type expressionOf(Type t) {
        if(t instanceof Class<?> c) {
            if (c.isPrimitive()) {
                return reify(Expression.class,box(c));
            }
        }
        return reify(Expression.class, t);
    }
}
