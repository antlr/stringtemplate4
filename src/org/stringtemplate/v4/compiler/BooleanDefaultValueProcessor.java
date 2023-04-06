package org.stringtemplate.v4.compiler;

import org.stringtemplate.v4.STGroup;

public class BooleanDefaultValueProcessor extends DefaultValueProcessor {
    @Override
    public void process(FormalArgument fa, STGroup group) {
        fa.defaultValue = fa.defaultValueToken.getType()==GroupParser.TRUE;
    }
}
