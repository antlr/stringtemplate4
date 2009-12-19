/*
 [The "BSD licence"]
 Copyright (c) 2009 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.stringtemplate.misc;

/** All the errors that can happen and how to generate a message */
public enum ErrorType {      
    // RUNTIME SEMANTIC ERRORS
    NO_SUCH_TEMPLATE("no such template: %s"),
    CANT_SET_ATTRIBUTE("can't set attribute %s; template %s has no such attribute"),
    NO_IMPORTED_TEMPLATE("no such template: super.%s"),
    NO_ATTRIBUTE_DEFINITION("attribute %s isn't defined"),
    EXPECTING_SINGLE_ARGUMENT("expecting single arg in template reference %s (not %s args)"),
    MISSING_FORMAL_ARGUMENTS("missing argument definitions"),
    NO_SUCH_PROPERTY("no such property or can't access: %s"),
    MAP_ARGUMENT_COUNT_MISMATCH("iterating through %s arguments but parallel map has %s formal arguments"),
    EXPECTING_STRING("function %s expects a string not %s"),

    // COMPILE-TIME SYNTAX/SEMANTIC ERRORS
    SYNTAX_ERROR("%s"),    
    TEMPLATE_REDEFINITION("redefinition of template %s"),
    EMBEDDED_REGION_REDEFINITION("region %s is embedded and thus already implicitly defined"),
    REGION_REDEFINITION("redefinition of region %s"),
    MAP_REDEFINITION("redefinition of dictionary %s"),
    ALIAS_TARGET_UNDEFINED("cannot alias %s to undefined template: %s"),
    TEMPLATE_REDEFINITION_AS_MAP("redefinition of template %s as a map"),
    LEXER_ERROR("lexer error or bad character %s"),
    NO_DEFAULT_VALUE("missing dictionary default value"),
    NO_SUCH_FUNCTION("no such function: %s"),
    NO_SUCH_OPTION("no such option: %s"),

    // INTERNAL ERRORS
    INTERNAL_ERROR("%s"),
    WRITE_IO_ERROR("error writing output caused by"),
    CANT_LOAD_GROUP_FILE("can't load group file %s"),
    CANT_LOAD_TEMPLATE_FILE("can't load template file %s");

    public String message;

    ErrorType(String m) { message = m; }
}
