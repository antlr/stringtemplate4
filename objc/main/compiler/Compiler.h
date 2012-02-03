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
#import "Interpreter.h"
#import "ST.h"
#import "STGroup.h"
#import "ErrorManager.h"
#import "ErrorType.h"

@interface Compiler_Anon1 : NSObject {
    __strong AMutableDictionary *dict;
}

+ (id) newCompiler_Anon1;
- (id) init;

- (void) dealloc;
- (id) getDict;
- (id) objectForKey:(id)aKey;
- (void) setObject:(id)anObject forKey:(id)aKey;
- (NSInteger) count;

@property (retain) AMutableDictionary *dict;
@end

@interface Compiler_Anon2 : NSObject {
    __strong AMutableDictionary *dict;
}

+ (id) newCompiler_Anon2;
- (id) init;

- (void) dealloc;
- (id) copyWithZone:(NSZone *)aZone;
- (id) getDict;
- (id) objectForKey:(id)aKey;
- (void) setObject:(id)anObject forKey:(id)aKey;
- (NSInteger) count;

@property (retain) AMutableDictionary *dict;
@end

@interface Compiler_Anon3 : NSObject {
    __strong AMutableDictionary *dict;
}

+ (id) newCompiler_Anon3;
- (id) init;

- (void) dealloc;
- (id) copyWithZone:(NSZone *)aZone;
- (id) getDict;
- (short) instrForKey:(NSString *)aKey;
- (void) setInstr:(short)anInstr forKey:(NSString *)aKey;
- (NSInteger) count;

@property (retain) AMutableDictionary *dict;
@end

@interface Compiler : NSObject {
    __strong STGroup *group;
}

@property (retain) STGroup *group;
   //@property(nonatomic, retain, readonly) NSString *newSubtemplateName;

+ (void) initialize;
+ (NSString *) SUBTEMPLATE_PREFIX;
+ (NSString *) getNewSubtemplateName;
+ (NSInteger) supportedOptions;
+ (Compiler_Anon1 *) getSupportedOptions;
+ (NSInteger) NUM_OPTIONS;
+ (Compiler_Anon2 *) defaultOptionValues;
+ (Compiler_Anon3 *) funcs;
+ (NSInteger) subtemplateCount;
+ (CompiledST *) defineBlankRegion:(CompiledST *)outermostImpl token:(CommonToken *)token;

+ (Compiler *) newCompiler;
+ (Compiler *) newCompiler:(STGroup *)aSTGroup;

- (id) init;
- (id) initWithSTGroup:(STGroup *)aSTGroup;

- (void) dealloc;
- (CompiledST *) compile:(NSString *)template;
- (CompiledST *) compile:(NSString *)name template:(NSString *)template;
- (CompiledST *) compile:(NSString *)srcName
                    name:(NSString *)name
                    args:(AMutableArray *)args
                template:(NSString *)template
           templateToken:(CommonToken *)templateToken;
- (void) reportMessageAndThrowSTException:(CommonTokenStream *)tokens
                            templateToken:(CommonToken *)templateToken
                                  aParser:(Parser *)aParser
                                       re:(RecognitionException *)re;
@end
