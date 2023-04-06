package org.stringtemplate.v4.compiler;

import org.stringtemplate.v4.STGroup;

import java.util.Collections;

public class ListDefaultValueProcessor extends DefaultValueProcessor {
    @Override
    public void process(FormalArgument fa, STGroup group) {
        fa.defaultValue = Collections.emptyList();
    }
}
