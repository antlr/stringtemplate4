/*
 * [The "BSD license"]
 *  Copyright (c) 2011-today Terence Parr
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.stringtemplate.v4;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents ST template annotations.
 * @author <a href="rydnr@acm-sl.org">rydnr</a>
 */
public class STAnnotation {
    /**
     * The annotation name.
     */
    private final String name;

    /**
     * The annotation value.
     */
    private final String value;

    /**
     * Creates a new {@code STAnnotation}.
     * @param name the name.
     * @param value the value.
     */
    public STAnnotation(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Retrieves the name.
     * @return such information.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Retrieves the value.
     * @return such information.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Checks whether the value of this annotation, as a regex,
     * is able to parse given text.
     * @param text the text.
     * @return {@code true} in such case.
     */
    public boolean matches(String text) {
        boolean result = Pattern.matches(removeSurroundingQuotes(getValue()), text);

        return result;
    }

    /**
     * Retrieves the groups contained in given text, should
     * this annotation's value is able to parse it and extract
     * its groups.
     * @param text the text.
     * @return the parsed groups.
     */
    public List<String> getParameters(String text) {
        List<String> result = new ArrayList<String>();

        Pattern pattern = Pattern.compile(removeSurroundingQuotes(getValue()));

        Matcher matcher = pattern.matcher(text);

        if (matcher.matches()) {
            for (int index = 0; index < matcher.groupCount(); index++) {
                result.add(matcher.group(index + 1));
            }
        }
        
        return result;
    }

    //-- helper methods --//
    protected String removeSurroundingQuotes(String value) {
        String result = (value != null) ? value.trim() : "";

        if (result.startsWith("\"")) {
            result = result.substring(1);
        }
        if (result.endsWith("\"")) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }
    
    //-- Effective Java stuff --//
    /**
     * Checks whether this instance is semantically equal to
     * given object.
     * @param target the target.
     * @return {@code true} if both instances are equal.
     */
    @Override
    public boolean equals(final Object target) {

        final boolean result;

        if (this == target) {
            result = true;
        } else if (target == null) {
            result = false;
        } else if (target instanceof STAnnotation) {
            final STAnnotation annotation = (STAnnotation) target;
            
            result =
                   areEqual(this.name, annotation.getName())
                && areEqual(this.value, annotation.getValue());
        } else {
            result = false;
        }

        return result;
    }

    /**
     * Checks whether both texts are equal.
     * @param first the first.
     * @param second the second.
     * @return {@code true} if both are equal (or both are {@code null}).
     */
    protected boolean areEqual(final String first, final String second) {
        final boolean result;

        if (first == null) {
            result = (second == null);
        } else if (second == null) {
            result = false;
        } else {
            result = first.equals(second);
        }

        return result;
    }

    /**
     * Retrieves the hashcode.
     * @return such value.
     */
    @Override
    public int hashCode() {
        return (STAnnotation.class.getName() + '.' + getName() + '.' + getValue()).hashCode();
    }

    /**
     * Builds a string representation of the annotation.
     * @return such representation.
     */
    @Override
    public String toString() {
        return
              "{ "
            +   " \"class\": \"" + STAnnotation.class.getName() + '"'
            +   ", \"name\": \"" + getName() + '"'
            +   ", \"value\": \"" + getValue() + '"'
            + "}";
    }
}
