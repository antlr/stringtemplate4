package org.stringtemplate;

public enum ErrorType {      
    // RUNTIME SEMANTIC ERRORS
    NO_SUCH_TEMPLATE("no such template: %s"),
    NO_IMPORTED_TEMPLATE("no such template: super.%s"),
    CANT_ACCESS_PROPERTY_METHOD("can't access property %s via method"),
    CANT_ACCESS_PROPERTY_FIELD("can't access property %s as field"),
    NO_SUCH_PROPERTY("%s doesn't have a %s property"),
    EXPECTING_SINGLE_ARGUMENT("expecting single arg in template %s, not %s args"),
    MISSING_FORMAL_ARGUMENTS("missing argument definitions"),
    ARGUMENT_COUNT_MISMATCH("template %s's actual and formal argument count does not match"),
    EXPECTING_STRING("function %s expects a string not %s"),

    // COMPILE-TIME SYNTAX/SEMANTIC ERRORS
    SYNTAX_ERROR("%s"),    
    TEMPLATE_REDEFINITION("redefinition of template %s"),
    EMBEDDED_REGION_REDEFINITION("region %s is embedded and thus already implicitly defined"),
    REGION_REDEFINITION("redefinition of region %s"),
    MAP_REDEFINITION("redefinition of dictionary %s"),
    TEMPLATE_REDEFINITION_AS_MAP("redefinition of template %s as a map"),
    LEXER_ERROR("lexer there are add character %s"),
    NO_DEFAULT_VALUE("missing dictionary default value"),
    NO_SUCH_FUNCTION("no such function: %s"),
    NO_SUCH_OPTION("no such option: %s"),

    // IO ERRORS
    WRITE_IO_ERROR("error writing output"),
    CANT_LOAD_GROUP_FILE("can't load group file %s"),
    CANT_LOAD_TEMPLATE_FILE("can't load template file %s"),
    INVALID_BYTECODE("invalid bytecode %s at IP %s"),

    GUI_ERROR("GUI error");

    public String messageTemplate;

    ErrorType(String m) { messageTemplate = m; }

}
