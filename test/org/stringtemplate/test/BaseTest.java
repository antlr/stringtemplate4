package org.stringtemplate.test;

import org.stringtemplate.Misc;

import java.io.File;

public class BaseTest {
    public static final String tmpdir = System.getProperty("java.io.tmpdir");
    public static final String newline = Misc.newline;

    public static class User {
        public int id;
        public String name;
        public User(int id, String name) { this.id = id; this.name = name; }
        public String getName() { return name; }
    }

    public static class HashableUser extends User {
        public HashableUser(int id, String name) { super(id, name); }
        public int hashCode() {
            return id;
        }

        public boolean equals(Object o) {
            if ( o instanceof HashableUser ) {
                HashableUser hu = (HashableUser)o;
                return this.id == hu.id && this.name.equals(hu.name);
            }
            return false;
        }
	}
    
    protected String getRandomDir() {
        String randomDir = tmpdir+"/dir"+String.valueOf((int)(Math.random()*100000));
        File f = new File(randomDir);
        f.mkdirs();
        return randomDir;
    }
}
