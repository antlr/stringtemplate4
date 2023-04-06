package org.stringtemplate.v4.compiler;

public class DefaultValueProcessorFactory {
    public static DefaultValueProcessor createDefaultValueProcessor(int defaultValueTokenType) {
        switch ( defaultValueTokenType ) {
            case GroupParser.ANONYMOUS_TEMPLATE:
                return new AnonymousTemplateDefaultValueProcessor();

            case GroupParser.STRING:
                return new StringDefaultValueProcessor();

            case GroupParser.LBRACK:
                return new ListDefaultValueProcessor();

            case GroupParser.TRUE:
            case GroupParser.FALSE:
                return new BooleanDefaultValueProcessor();

            default:
                throw new UnsupportedOperationException("Unexpected default value token type.");
        }
    }
}
