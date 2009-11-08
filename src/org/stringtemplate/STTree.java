package org.stringtemplate;

/** A tree of groups (nodes).
 *
 *  Template references starting with '/' start in tree root dir.  W/o
 *  the '/' it's a relative template reference.  A reference to <b/c()> in
 *  template x, looks for template c in directory or group file b in same
 *  dir as x.  For example, assume rootDirName is /tmp.
 *  Then, a reference like </a()> loads /tmp/a.st file.  </a/b()> loads
 *  /tmp/a/b.st where 'a' can be a dir or group file, and so on.
 *
 *  TODO: how does this work with inheritance?  maybe think of just looking for templates in list of dirs/groupfiles?
 */
public class STTree {
    STGroup root;
    String rootDir;

    public STTree(String rootDir) { this.rootDir = rootDir; }
}
