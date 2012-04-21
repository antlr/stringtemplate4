/*
 * [The "BSD license"]
 *  Copyright (c) 2011 Terence Parr and Alan Condit
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
#import <ANTLR/ANTLR.h>
#import "STErrorListener.h"
#import "AggregateModelAdaptor.h"
#import "Interpreter.h"
#import "CompiledST.h"

@implementation AggregateModelAdaptor

+ (id) newModelAdaptor
{
    return [[AggregateModelAdaptor alloc] init];
}

- (id) init
{
    self = [super init];
    return self;
}

- (id) getProperty:(Interpreter *)interp who:(ST *)aWho obj:(id)obj property:(id)aProperty propertyName:(NSString *)aPropertyName
{
    id value;
    Aggregate *aggr = (Aggregate *)obj;
    if ( aProperty == nil ) {
        value = [aggr.props get:STGroup.DEFAULT_KEY];
    }
    else if ( [aProperty isEqualTo:@"keys"] ) {
        value = [[aggr.props keySet] toArray];
    }
    else if ( [aProperty isEqualTo:@"values"] ) {
        value = [[aggr.props values] toArray];
    }
    else if ( [aProperty isKindOfClass:[NSString class]] && [aggr.props get:aProperty] ) {
        value = [aggr.props get:aProperty];
    }
    else if ( [aggr.props get:aPropertyName] ) { // if can't find the key, try toString version
        value = [aggr.props get:aPropertyName];
    }
    else {
        value = [aggr.props get:STGroup.DEFAULT_KEY]; // not found, use default
    }
    if ( value == STGroup.DICT_KEY ) {
        value = aProperty;
    }
/*
    if ( [value isKindOfClass:[ST class]] ) {
        ST *st = (ST *)value;
        st = [st.groupThatCreatedThisInstance createStringTemplateInternally:[CompiledST newCompiledST]];
        st.enclosingInstance = aWho;
        value = st;
    }
 */
    return value;
}

@end
