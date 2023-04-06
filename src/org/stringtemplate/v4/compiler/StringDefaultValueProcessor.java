package org.stringtemplate.v4.compiler;

import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.misc.Misc;

public class StringDefaultValueProcessor extends DefaultValueProcessor {
    @Override
    public void process(FormalArgument fa, STGroup group) {
        fa.defaultValue = Misc.strip(fa.defaultValueToken.getText(), 1);
    }
}
