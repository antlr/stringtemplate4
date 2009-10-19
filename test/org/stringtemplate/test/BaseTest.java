package org.stringtemplate.test;

import org.stringtemplate.Misc;

public class BaseTest {
    public static final String tmpdir = System.getProperty("java.io.tmpdir");
    public static final String newline = Misc.newline;

    public static class User {
        public int id;
        public String name;
        public User(int id, String name) { this.id = id; this.name = name; }
        public String getName() { return name; }
    }
}
