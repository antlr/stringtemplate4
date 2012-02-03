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
#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>

//#import "Token.h"

/**
 * Represents the name of a formal argument defined in a template:
 * 
 * test(a,b) ::= "<a> <n>"
 * 
 * Each template has a set of these formal arguments or uses
 * a placeholder object: UNKNOWN (indicating that no arguments
 * were specified such as when we create a template with "new ST(...)").
 * 
 * Note: originally, I tracked cardinality as well as the name of an
 * attribute.  I'm leaving the code here as I suspect something may come
 * of it later.  Currently, though, cardinality is not used.
 */
@class CompiledST;

@interface FormalArgument : NSObject {
    __strong NSString *name;
    NSInteger index; // which argument is it? from 0..n-1
	/** If they specified default value x=y, store the token here */
    __strong CommonToken *defaultValueToken;
    id defaultValue; // x="str", x=true, x=false
    NSInteger cardinality;
    /*** If they specified name="value", store the template here */
    __strong CompiledST *compiledDefaultValue;
}

+ (NSInteger) OPTIONAL;
+ (NSInteger) REQUIRED;
+ (NSInteger) ZERO_OR_MORE;
+ (NSInteger) ONE_OR_MORE;
+ (NSString *) suffixes:(NSInteger)idx;
    
+ (id) newFormalArgument;
+ (id) newFormalArgument:(NSString *)aName;
+ (id) newFormalArgument:(NSString *)aName token:(CommonToken *)aToken;

- (id) init;
- (id) initWithName:(NSString *)name;
- (id) init:(NSString *)name token:(CommonToken *)aDefaultValueToken;

- (void) dealloc;
- (NSInteger) hash;
- (BOOL) isEqualTo:(NSString *)obj;
- (NSString *) toString;
- (NSString *) description;

@property (retain) NSString *name;
@property (assign) NSInteger index;
@property (retain) CommonToken *defaultValueToken;
@property (retain) id defaultValue;
@property (retain) CompiledST *compiledDefaultValue;

@end
