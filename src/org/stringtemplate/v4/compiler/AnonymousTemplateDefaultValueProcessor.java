package org.stringtemplate.v4.compiler;

import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.misc.Misc;

public class AnonymousTemplateDefaultValueProcessor extends DefaultValueProcessor {
    @Override
    public void process(FormalArgument fa, STGroup group) {
        String argSTname = fa.name+"_default_value";
        Compiler c2 = new Compiler(group);
        String defArgTemplate = Misc.strip(fa.defaultValueToken.getText(), 1);
        fa.compiledDefaultValue =
            c2.compile(group.getFileName(), argSTname, null,
                defArgTemplate, fa.defaultValueToken);
        fa.compiledDefaultValue.name = argSTname;
        fa.compiledDefaultValue.defineImplicitlyDefinedTemplates(group);
    }
}
