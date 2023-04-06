package org.stringtemplate.v4.compiler;

import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.compiler.FormalArgument;
import org.stringtemplate.v4.misc.Misc;

import java.util.Collections;

public abstract class DefaultValueProcessor {
    public abstract void process(FormalArgument fa, STGroup group);
}

