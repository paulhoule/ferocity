package com.ontology2.ferocity;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.zip.ZipFile;

import static com.ontology2.ferocity.DefaultMap.newListMultiMap;
import static com.ontology2.ferocity.ExpressionDSL.*;
import static com.ontology2.ferocity.Literal.of;
import static com.ontology2.ferocity.ParameterDeclaration.parameter;
import static com.ontology2.ferocity.SelfDSL.callCreateMethodCall;
import static java.lang.reflect.Modifier.*;

record NameArity(String name, int arity) {
    public NameArity with(String newName) {
        return new NameArity(newName, this.arity);
    }
    public static NameArity of(Constructor c) {
        return new NameArity("", c.getParameterCount());
    }
}

record Signature(String name, List<Class> arguments) {
    public static Signature of(Class c,Method m) {
        List<Class> arguments = new ArrayList<>();
        if(!isStatic(m.getModifiers())) {
            arguments.add(c);
        }
        for(int i=0;i<m.getParameterCount();i++) {
            arguments.add(m.getParameters()[i].getType());
        }
        return new Signature(m.getName(),arguments);
    }
}

public class WrapperGenerator {
    private static final String SUPPRESSED = "\00";  // denote a method we won't generate
    private static String capitalize(String s) {
        StringBuilder sb=new StringBuilder();
        sb.append(Character.toUpperCase(s.charAt(0)));
        sb.append(s,1,s.length());
        return sb.toString();
    }
    private static final Expression<byte[]>[] EXPRESSION_OF_ARRAY_OF_BYTE = new Expression[0];

    Class target;
    public WrapperGenerator() {
        target = String.class;
    }

    public void generate() throws IOException, ClassNotFoundException {
//        System.out.print(ClassLoader.getSystemClassLoader().getDefinedPackages());
        String srcZip = "c:\\Users\\paul_2fb85i5\\.jdks\\openjdk-15.0.1\\lib\\src.zip";
        var entries = new ZipFile(srcZip).entries();
        while(entries.hasMoreElements()) {
            var e= entries.nextElement();
            if(e.getName().startsWith("java.base/java/lang/")) {
                String cn = e.getName().substring(10,e.getName().length()-5).replace("/",".");
                if(!cn.endsWith(".package-info")) {
                    Class c = getClass().getClassLoader().loadClass(cn);
                    if((c.getModifiers() & Modifier.PUBLIC)!=0) {
                        processClass(c);
                    }
                }
            }
        }
        Enumeration<URL> roots = ClassLoader.getPlatformClassLoader().getResources("");
        while(roots.hasMoreElements()) {
            System.out.print(roots.nextElement());
        }
    }

    private List<Class> parameterList(Method m) {
        var result = new ArrayList<Class>();
        if(!isStatic(m.getModifiers())) {
            result.add(m.getReturnType());
        }
        for(var p: m.getParameters()) {
            result.add(p.getType());
        }
        return result;
    }

    private void processClass(Class c) {
        if(!isInterface(c.getModifiers())) {
            var namedMethods = deconflictMethods(c);
            var namedConstructors = deconflictConstructors(c);
            System.out.println(c);
            for(NameArity name: namedConstructors.keySet()) {
                Executable method = namedConstructors.get(name);
                System.out.println("    _ctor"+name.name()+" "+method);
            }
            for(NameArity name: namedMethods.keySet()) {
                Executable method = namedMethods.get(name);
                System.out.println("    "+name.name()+" "+method);
            }
        }
    }

    private Map<NameArity, Method> deconflictMethods(Class c) {
        Map<NameArity, List<Method>> methodGroups = newListMultiMap();
        for(final Method m: c.getDeclaredMethods()) {
            if((m.getModifiers() & Modifier.PUBLIC)==0) {
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
                };
            } else {
                namedMethods.put(item.getKey(), item.getValue().get(0));
            }
        }
        return namedMethods;
    }

    Map<NameArity, Executable> deconflictConstructors(Class c) {
        Map<NameArity, List<Executable>> ctorGroups= newListMultiMap();
        for(Constructor ctor: c.getDeclaredConstructors()) {
            if(!isPublic(ctor.getModifiers())) {
                continue;
            }
            ctorGroups.get(NameArity.of(ctor)).add(ctor);
        }

        Map<NameArity, Executable> namedCtor = new HashMap<>();
        for(var item: ctorGroups.entrySet()) {
            if(item.getValue().size()>1) {
                for(var that: deconflictConstructorGroup(item).entrySet()) {
                    namedCtor.put(that.getKey(), that.getValue());
                };
            } else {
                namedCtor.put(item.getKey(), item.getValue().get(0));
            }
        }
        return namedCtor;
    }

    private Map<NameArity, Executable> deconflictConstructorGroup(Map.Entry<NameArity, List<Executable>> item) {
        return getNameArityExecutableMap(item.getKey(), item.getValue());
    }

    Map<NameArity, Executable> deconflictMethodGroup(Class c, Map.Entry<NameArity, List<Method>> g) {
        //
        // heuristic:  create a mapping from classes that appear in the
        // parameters to methods;  we're hoping that some of the classes
        // will be uniquely identified by a method in which case we
        // can make a name like
        //
        // append«long»
        //
        List<Executable> filteredMethods = removeMethodsWithSuperclassReturns(c, g.getValue());
        return getNameArityExecutableMap(g.getKey(), filteredMethods);
    }

    private Map<NameArity, Executable> getNameArityExecutableMap(NameArity a, List<Executable> filteredMethods) {
        Map<Class, Set<Executable>> md = generateIndexOfMethodsByClassUsedinParameter(filteredMethods);
        Map<Executable, TreeSet<String>> uniqueClass = findClassesWithUniqueIndex(md);

        List<String> suffix=new ArrayList(filteredMethods.size());
        giveNamesToCharactersWithAUniqueClass(filteredMethods, uniqueClass, suffix);
        uniqifyNames(filteredMethods, suffix);
        Map<NameArity, Executable> result = new HashMap<>();
        for(int i = 0; i< filteredMethods.size(); i++) {
            if(suffix.get(i)!=null) {
                String name = a.name()+suffix.get(i);
                result.put(a.with(name), filteredMethods.get(i));
            }
        }
        return result;
    }

    private void uniqifyNames(List<Executable> filteredMethods, List<String> suffix) {
        List<Integer> unnamed = new ArrayList<>();
        for(int i = 0; i< filteredMethods.size(); i++) {
            if(suffix.get(i)==null) {
                unnamed.add(i);
            }
        }

        if(unnamed.size()==1) {
            suffix.set(unnamed.get(0), "");
        } else {
            for(int i=0;i<unnamed.size();i++) {
                suffix.set(unnamed.get(i), new String(new char[] {(char) (0x2460+i)},0,1));
            }
        }
    }

    //
    // a related scenario which is not being handled yet is one where a class inherits from
    // (say) the interface Comparable<T> and we wind up with a compareTo<Object> and a
    // compareTo<ThisClass> example.
    //
    private List<Executable> removeMethodsWithSuperclassReturns(Class c, List<Method> g) {
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


    private void giveNamesToCharactersWithAUniqueClass(List<Executable> g, Map<Executable, TreeSet<String>> uniqueClass, List<String> suffix) {
        for(int i = 0; i< g.size(); i++) {
            Executable m = g.get(i);
            if(uniqueClass.containsKey(m)) {
                String name = uniqueClass.get(m).first();
                suffix.add("«"+name.replace("[]","⋯")+"»");
            } else {
                suffix.add(null);
            }
        }
    }

    private Map<Executable, TreeSet<String>> findClassesWithUniqueIndex(Map<Class, Set<Executable>> md) {
        Map<Executable, TreeSet<String>> uniqueClass = new HashMap<>();
        for(var that: md.entrySet()) {
            if(that.getValue().size()==1) {
                Executable method=that.getValue().iterator().next();
                String name=that.getKey().getSimpleName();
                if(!uniqueClass.containsKey(method)) {
                    uniqueClass.put(method, new TreeSet<String>());
                }
                uniqueClass.get(method).add(name);
            }
        }
        return uniqueClass;
    }

    private Map<Class, Set<Executable>> generateIndexOfMethodsByClassUsedinParameter(List<Executable> g) {
        Map<Class,Set<Executable>> md = new HashMap<>();
        BiConsumer<Class, Executable> insert = (type, method) -> {
            if(!md.containsKey(type)) {
                md.put(type, new HashSet<>());
            }
            md.get(type).add(method);
        };

        for(int i = 0; i< g.size(); i++) {
            Executable m = g.get(i);
            if(!isStatic(m.getModifiers())) {
                insert.accept(m.getDeclaringClass(), m);
            }
            for(var type:m.getParameterTypes()) {
                insert.accept(type, m);
            }
        }
        return md;
    }

    public void generateOne() {
        UrClass uc = defClass("com.ontology2.FierceString");
        for(Method m: target.getDeclaredMethods()) {
            if((m.getModifiers() & Modifier.PUBLIC)!=0) {
                if(m.getName().equals("getBytes")) {
                    uc = updateClassForMethod(uc, m);
                }
            }
        }
        System.out.println(uc.asSource());
    }

    private UrClass updateClassForMethod(UrClass uc, Method m) {
        String name = "call" + capitalize(m.getName());
        System.out.println(m+" "+m.getModifiers());
        if ((m.getModifiers() & Modifier.STATIC) > 0) {
            uc = updateClassForStaticMethod(uc, m, name);   
        } else {
            uc = updateClassForInstanceMethod(uc, m, name);
        }
        return uc;
    }

    private UrClass updateClassForStaticMethod(UrClass uc, Method m, String name) {
        System.out.println("Skipping static method "+m);
        return uc;
    }

    private UrClass updateClassForInstanceMethod(UrClass uc, Method m, String name) {
        var that = parameter(EXPRESSION,reify(Expression.class, target), "that");
        Expression<Expression<?>>[] arguments = new Expression[m.getParameterCount()];
        var header = method(name, EXPRESSION, reify(Expression.class, m.getGenericReturnType()));
        header = header.receives(that);
        for(int i=0;i<m.getParameterCount();i++) {
            var p = m.getParameters()[i];
            System.out.println(reify(Expression.class, p.getParameterizedType()));
            var pdecl = parameter(reify(Expression.class,p.getParameterizedType()),p.getName());
            header = header.receives(pdecl);
            arguments[i] = (Expression<Expression<?>>) pdecl.reference();
        }
        objectArrayExact((Expression<?>[]) EXPRESSION, arguments);
        uc = uc.def(header
                .withBody(
                    callCreateMethodCall(
                        (Expression) objectArray(STRING),
                        (Expression) that.reference(),
                        of(m.getName()),
                        of(m.getParameterTypes()),
                        objectArray(EXPRESSION, arguments)
                    )));
        return uc;
    }

    public static void main(String argv[]) throws IOException, ClassNotFoundException {
        WrapperGenerator w = new WrapperGenerator();
        w.generate();
    }
}
