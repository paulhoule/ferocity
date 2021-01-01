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
     * @param argv
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static void main(String argv[]) throws ClassNotFoundException, IOException {
        String srcZip = argv[0];
        var entries = new ZipFile(srcZip).entries();
        while(entries.hasMoreElements()) {
            var e= entries.nextElement();
            if(e.getName().startsWith("java.base/java/lang/")) {
                String cn = e.getName().substring(10,e.getName().length()-5).replace("/",".");
                if(!cn.endsWith(".package-info")) {
                    Class c = GenerateClassList.class.getClassLoader().loadClass(cn);
                    if((c.getModifiers() & Modifier.PUBLIC)!=0) {
                        System.out.println(cn);
                    }
                }
            }
        }
    }
}
