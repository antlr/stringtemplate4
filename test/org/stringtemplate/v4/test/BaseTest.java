/*
 [The "BSD license"]
 Copyright (c) 2009 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.stringtemplate.v4.test;

import java.util.concurrent.atomic.AtomicInteger;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.junit.Before;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.compiler.Compiler;
import org.stringtemplate.v4.compiler.STLexer;
import org.stringtemplate.v4.misc.Misc;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public abstract class BaseTest {
    public static final String pathSep = System.getProperty("path.separator");
    public static final boolean interactive = Boolean.parseBoolean(System.getProperty("test.interactive"));
    public static final String newline = Misc.newline;

    public String tmpdir = null;

    /**
     * When runnning from Maven, the junit tests are run via the surefire plugin. It sets the
     * classpath for the test environment into the following property. We need to pick this up
     * for the junit tests that are going to generate and try to run code.
     */
    public static final String SUREFIRE_CLASSPATH =
        System.getProperty("surefire.test.class.path", "");

    public static final String CLASSPATH = System.getProperty("java.class.path") +
                                           (SUREFIRE_CLASSPATH.equals("") ?
                                               "" :
                                               pathSep + SUREFIRE_CLASSPATH);

    public static class StreamVacuum implements Runnable {
        StringBuffer buf = new StringBuffer();
        BufferedReader in;
        Thread sucker;
        public StreamVacuum(InputStream in) {
            this.in = new BufferedReader( new InputStreamReader(in) );
        }
        public void start() {
            sucker = new Thread(this);
            sucker.start();
        }
        @Override
        public void run() {
            try {
                String line = in.readLine();
                while (line!=null) {
                    buf.append(line);
                    buf.append('\n');
                    line = in.readLine();
                }
            }
            catch (IOException ioe) {
                System.err.println("can't read output from process");
            }
        }
        /** wait for the thread to finish */
        public void join() throws InterruptedException {
            sucker.join();
        }
        @Override
        public String toString() {
            return buf.toString();
        }
    }

    @Before
    public void setUp() {
        STGroup.defaultGroup = new STGroup();
        Compiler.subtemplateCount = new AtomicInteger(0);

        String baseTestDirectory = System.getProperty("java.io.tmpdir");
        String testDirectory = getClass().getSimpleName() + "-" + System.currentTimeMillis();
        tmpdir = new File(baseTestDirectory, testDirectory).getAbsolutePath();
    }

    /**
     * Creates a file "Test.java" in the directory dirName containing a main
     * method with content starting as given by main.
     * <p>
     * The value of a variable 'result' defined in 'main' is written to
     * System.out, followed by a newline character.</p>
     * <p>
     * The final newline character is just the '\n' character, not the
     * system specific line separator ({@link #newline}).</p>
     *
     * @param main
     * @param dirName
     */
    public void writeTestFile(String main, String dirName) {
        ST outputFileST = new ST(
            "import org.antlr.runtime.*;\n" +
            "import org.stringtemplate.v4.*;\n" +
            "import org.stringtemplate.v4.misc.STMessage;\n"+
            "import org.antlr.runtime.tree.*;\n" +
            "import java.io.*;\n" +
            "import java.net.*;\n" +
            "\n" +
            "public class Test {\n" +
            "    public static void main(String[] args) throws Exception {\n" +
            "        final StringBuilder errors = new StringBuilder();" +
            "        STErrorListener listener = new STErrorListener() {\n"+
            "            public void compileTimeError(STMessage stMessage) { error(stMessage); }\n"+
            "            public void runTimeError(STMessage stMessage) { error(stMessage); }\n"+
            "            public void IOError(STMessage stMessage) { error(stMessage); }\n"+
            "            public void internalError(STMessage stMessage) { error(stMessage); }\n"+
            "            protected void error(STMessage stMessage) {\n"+
            "                errors.append(stMessage.toString());\n" +
            "            }\n"+
            "        };\n"+
            "        <code>\n"+
            "        System.out.println(result);\n"+
            "        if ( errors.length()>0 ) System.out.println(\"errors: \"+errors);\n"+
            "    }\n" +
            "}"
            );
        outputFileST.add("code", main);
        writeFile(dirName, "Test.java", outputFileST.render());
    }

    public String java(String mainClassName, String extraCLASSPATH, String workingDirName) throws InterruptedException, IOException {
        String classpathOption = "-classpath";

        String path = "."+pathSep+CLASSPATH;
        if ( extraCLASSPATH!=null ) path = "."+pathSep+extraCLASSPATH+pathSep+CLASSPATH;

        String[] args = new String[] {
                    "java",
                    classpathOption, path,
                    mainClassName
        };
        System.out.println("executing: "+Arrays.toString(args));
        return exec(args, null, workingDirName);
    }

    public void jar(String fileName, String[] files, String workingDirName) throws IOException {
        File workingDir = new File(workingDirName);
        JarOutputStream stream = new JarOutputStream(new FileOutputStream(new File(workingDir, fileName)));
        try {
            for (String inputFileName : files) {
                File file = new File(workingDirName, inputFileName);
                addJarFile(file, workingDir, stream);
            }
        }
        finally {
            stream.close();
        }
    }

    protected void addJarFile(File file, File workingDir, JarOutputStream stream) throws IOException {
        String name = workingDir.toURI().relativize(file.toURI()).getPath().replace("\\", "/");
        if (file.isDirectory()) {
            if (!name.isEmpty()) {
                if (!name.endsWith("/")) {
                    // the entry for a folder must end with "/"
                    name += "/";
                }

                JarEntry entry = new JarEntry(name);
                entry.setTime(file.lastModified());
                stream.putNextEntry(entry);
                stream.closeEntry();
            }

            final File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    addJarFile(child, workingDir, stream);
                }
            }

            return;
        }

        JarEntry entry = new JarEntry(name);
        entry.setTime(file.lastModified());
        stream.putNextEntry(entry);

        BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
        try {
            byte[] buffer = new byte[16 * 1024];
            while (true) {
                int count = input.read(buffer);
                if (count == -1) {
                    break;
                }

                stream.write(buffer, 0, count);
            }

            stream.closeEntry();
        }
        finally {
            input.close();
        }
    }

    protected void compile(String fileName, String workingDirName) {
        List<File> files = new ArrayList<File>();
        files.add(new File(workingDirName, fileName));

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        StandardJavaFileManager fileManager =
            compiler.getStandardFileManager(null, null, null);

        Iterable<? extends JavaFileObject> compilationUnits =
            fileManager.getJavaFileObjectsFromFiles(files);

        Iterable<String> compileOptions =
            Arrays.asList("-g", "-source", "1.6", "-target", "1.6", "-implicit:class", "-Xlint:-options", "-d", workingDirName, "-cp", workingDirName+pathSep+CLASSPATH);

        JavaCompiler.CompilationTask task =
            compiler.getTask(null, fileManager, null, compileOptions, null,
                             compilationUnits);
        boolean ok = task.call();

        try {
            fileManager.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }

        assertTrue(ok);
    }

    public String exec(String[] args, String[] envp, String workingDirName) throws InterruptedException, IOException {
        String cmdLine = Arrays.toString(args);
        File workingDir = new File(workingDirName);

        Process process =
            Runtime.getRuntime().exec(args, envp, workingDir);
        StreamVacuum stdout = new StreamVacuum(process.getInputStream());
        StreamVacuum stderr = new StreamVacuum(process.getErrorStream());
        stdout.start();
        stderr.start();
        process.waitFor();
        stdout.join();
        stderr.join();
        if ( stdout.toString().length()>0 ) {
            return stdout.toString();
        }
        if ( stderr.toString().length()>0 ) {
            System.err.println("compile stderr from: "+cmdLine);
            System.err.println(stderr);
        }
        int ret = process.exitValue();
        if ( ret!=0 ) System.err.println("failed");
        return null;
    }

    public static void writeFile(String dir, String fileName, String content) {
        try {
            File f = new File(dir, fileName);
            if ( !f.getParentFile().exists() ) f.getParentFile().mkdirs();
            FileWriter w = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(w);
            bw.write(content);
            bw.close();
            w.close();
        }
        catch (IOException ioe) {
            System.err.println("can't write file");
            ioe.printStackTrace(System.err);
        }
    }

    public void checkTokens(String template, String expected) {
        checkTokens(template, expected, '<', '>');
    }


    public void checkTokens(String template, String expected,
                            char delimiterStartChar, char delimiterStopChar)
    {
        STLexer lexer =
            new STLexer(STGroup.DEFAULT_ERR_MGR,
                        new ANTLRStringStream(template),
                        null,
                        delimiterStartChar,
                        delimiterStopChar);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        StringBuilder buf = new StringBuilder();
        buf.append("[");
        int i = 1;
        Token t = tokens.LT(i);
        while ( t.getType()!=Token.EOF ) {
            if ( i>1 ) buf.append(", ");
            buf.append(t);
            i++;
            t = tokens.LT(i);
        }
        buf.append("]");
        String result = buf.toString();
        assertEquals(expected, result);
    }

    public static class User {
        public int id;
        public String name;
        public User(int id, String name) { this.id = id; this.name = name; }
        public boolean isManager() { return true; }
        public boolean hasParkingSpot() { return true; }
        public String getName() { return name; }
    }

    public static class HashableUser extends User {
        public HashableUser(int id, String name) { super(id, name); }
        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if ( o instanceof HashableUser ) {
                HashableUser hu = (HashableUser)o;
                return this.id == hu.id && this.name.equals(hu.name);
            }
            return false;
        }
    }

    public String getRandomDir() {
        File randomDir = new File(tmpdir, "dir"+(int)(Math.random()*100000));
        randomDir.mkdirs();
        return randomDir.getAbsolutePath();
    }

    /**
     * Removes the specified file or directory, and all subdirectories.
     *
     * Nothing if the file does not exists.
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] list = file.listFiles();
            if (list != null) {
                for (final File value : list) {
                    deleteFile(value);
                }
            }
        }
        if (!file.delete()) {
            throw new RuntimeException("Error when deleting file "
                    + file.getAbsolutePath());
        }
    }
    /**
     * see {@link #deleteFile(File)}
     *
     * @param file
     */
    public static void deleteFile(String file) {
        deleteFile(new File(file));
    }

}
