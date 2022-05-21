package com.ontology2.ferocity;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.zip.ZipFile;

public class GenerateClassList {
    /**
     * The first parameter is the path of the ZIP file which contains the source
     * code for the Java Standard Library.  I found it installed by IntelliJ Idea
     * at
     *
     * $HOME/.jdks/openjdk-15.0.1/lib/src.zip
     *
     * @param argv first parameter is path to ZIP file for JDK source code
     * @throws IOException if there is trouble with IO
     */
    public static void main(String[] argv) throws IOException {
        String srcZip = argv[0];
        try(ZipFile zipFile = new ZipFile(srcZip)) {
            var entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                var e = entries.nextElement();
                if (e.getName().endsWith(".java")) {
                    String cn = e.getName().replace("/", ".").substring(0, e.getName().length() - 5);
                    try {
                        Class<?> c = GenerateClassList.class.getClassLoader().loadClass(cn);

                        if ((c.getModifiers() & Modifier.PUBLIC) != 0) {
                            if (c.getModule().getName().equals("java.base"))
                                System.out.println(cn);
                        }
                    } catch (ClassNotFoundException x) {
                        //noinspection ThrowablePrintedToSystemOut
                        System.out.println(x);
                    }
                }
            }
        }
    }
}
