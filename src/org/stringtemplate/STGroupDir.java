package org.stringtemplate;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.UnbufferedTokenStream;

import java.io.File;
import java.util.Arrays;

// TODO: caching?

public class STGroupDir extends STGroup {
    public File dir;

    public STGroupDir(String dirName) {
        dir = new File(dirName);
        if ( !dir.isDirectory() ) {
            throw new IllegalArgumentException("No such directory: "+ dirName);
        }
    }

    public String getName() { return dir.getName(); }

    public CompiledST lookupTemplate(String name) {
        if ( name.startsWith("/") ) return root.lookupTemplate(name);
        if ( name.indexOf('/')>=0 ) {
            String[] names = name.split("/");
            // look for a directory or group file called names[0]
            STGroup sub = null;
            File subF = new File(dir, names[0]);
            if ( subF.isDirectory() ) {
                sub = new STGroupDir(dir+"/"+names[0]);
            }
            else if ( new File(dir, names[0]+".stg").exists() ) {
                try {
                    sub = loadGroup(dir+"/"+names[0]+".stg");
                }
                catch (Exception e) {
                    listener.error("can't load group file: "+ names[0]+".stg", e);
                }
            }
            else listener.error("no such subgroup: "+names[0]);
            return sub.lookupTemplate(Misc.join(names, "/", 1, names.length));
        }
        File f = new File(dir, "/" + name + ".st");
        if ( !f.exists() ) { // TODO: add tolerance check here
            throw new IllegalArgumentException("no such template: "+name);
        }
        try {
            ANTLRFileStream fs = new ANTLRFileStream(f.toString());
            GroupLexer lexer = new GroupLexer(fs);
			UnbufferedTokenStream tokens = new UnbufferedTokenStream(lexer);
            GroupParser parser = new GroupParser(tokens);
            parser.group = this;
            parser.templateDef();
            alreadyLoaded = true;
        }
        catch (Exception e) {
            listener.error("can't load template file: "+ f +"/"+name, e);
        }
        return super.lookupTemplate(name);
    }    
}
