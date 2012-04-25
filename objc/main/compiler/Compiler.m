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
#import <Foundation/Foundation.h>
#import <ANTLR/ANTLR.h>
#import "STErrorListener.h"
#import "Compiler.h"
#import "CodeGenerator.h"
#import "ST.h"
#import "CompiledST.h"
#import "Bytecode.h"
#import "Interpreter.h"
#import "STException.h"
#import "ErrorManager.h"
#import "STLexer.h"
#import "STParser.h"
#import "GroupParser.h"

@implementation Compiler_Anon1

+ (id) newCompiler_Anon1
{
    return [[Compiler_Anon1 alloc] init];
}

- (id) init
{
    if ( (self=[super init]) != nil ) {
        dict = [[LinkedHashMap newLinkedHashMap:16] retain];
        [dict put:@"anchor"    value:[ACNumber numberWithInteger:ANCHOR]];
        [dict put:@"format"    value:[ACNumber numberWithInteger:FORMAT]];
        [dict put:@"null"      value:[ACNumber numberWithInteger:_NULL]];
        [dict put:@"separator" value:[ACNumber numberWithInteger:SEPARATOR]];
        [dict put:@"wrap"      value:[ACNumber numberWithInteger:WRAP]];
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in Compiler_Anon1" );
#endif
    if ( dict ) [dict release];
    [super dealloc];
}

- (id) getDict
{
    return dict;
}

- (id) get:(id)aKey
{
    return [dict get:aKey];
}

- (void) put:(id)aKey value:(id)anObject
{
    [dict put:aKey value:anObject];
}

- (NSInteger) count
{
    return [dict count];
}

@synthesize dict;
@end

@implementation Compiler_Anon2

+ (id) newCompiler_Anon2
{
    return [[Compiler_Anon2 alloc] init];
}

- (id) init
{
    if ( (self=[super init]) != nil ) {
        dict = [[LinkedHashMap newLinkedHashMap:16] retain];
        [dict put:@"anchor" value:@"\"true\""];
        [dict put:@"wrap" value:@"\"\n\""];
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in Compiler_Anon2" );
#endif
    if ( dict ) [dict release];
    [super dealloc];
}

- (id) copyWithZone:(NSZone *)aZone
{
    Compiler_Anon2 *copy;
    
    copy = [[[self class] allocWithZone:aZone] init];
    if ( dict ) {
        if ( copy.dict ) [copy.dict release];
        copy.dict = [dict copyWithZone:aZone];
        [copy.dict retain];
    }
    return copy;
}

- (id) getDict
{
    return dict;
}

- (id) get:(id)aKey
{
    return [dict get:aKey];
}

- (void) put:(id)aKey value:(id)anObject
{
    [dict put:aKey value:anObject];
}

- (NSInteger) count
{
    return [dict count];
}

@synthesize dict;
@end

@implementation Compiler_Anon3

+ (id) newCompiler_Anon3
{
    return [[Compiler_Anon3 alloc] init];
}

- (id) init
{
    if ( (self=[super init]) != nil ) {
        dict = [[LinkedHashMap newLinkedHashMap:16] retain];
        [dict put:@"first"   value:[NSString stringWithFormat:@"%d", Bytecode.INSTR_FIRST]];
        [dict put:@"last"    value:[NSString stringWithFormat:@"%d", Bytecode.INSTR_LAST]];
        [dict put:@"rest"    value:[NSString stringWithFormat:@"%d", Bytecode.INSTR_REST]];
        [dict put:@"trunc"   value:[NSString stringWithFormat:@"%d", Bytecode.INSTR_TRUNC]];
        [dict put:@"strip"   value:[NSString stringWithFormat:@"%d", Bytecode.INSTR_STRIP]];
        [dict put:@"trim"    value:[NSString stringWithFormat:@"%d", Bytecode.INSTR_TRIM]];
        [dict put:@"length"  value:[NSString stringWithFormat:@"%d", Bytecode.INSTR_LENGTH]];
        [dict put:@"strlen"  value:[NSString stringWithFormat:@"%d", Bytecode.INSTR_STRLEN]];
        [dict put:@"reverse" value:[NSString stringWithFormat:@"%d", Bytecode.INSTR_REVERSE]];
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in Compiler_Anon3" );
#endif
    if ( dict ) [dict release];
    [super dealloc];
}

- (id) copyWithZone:(NSZone *)aZone
{
    Compiler_Anon3 *copy;
    
    copy = [[[self class] allocWithZone:aZone] init];
    if ( dict ) {
        if ( copy.dict ) [copy.dict release];
        copy.dict = [dict copyWithZone:aZone];
        if ( copy.dict ) [copy.dict retain];
    }
    return copy;
}

- (id) getDict
{
    return dict;
}

- (short) getInstr:(NSString *)aKey
{
    return (short)[(NSString *)[dict get:aKey] intValue];
}

- (void) put:(id)aKey setInstr:(short)anInstr
{
    [dict put:aKey value:[NSString stringWithFormat:@"%d", anInstr]];
}

- (NSInteger) count
{
    return [dict count];
}

@synthesize dict;
@end

@implementation Compiler

static Compiler_Anon1 *supportedOptions;
static NSInteger NUM_OPTIONS;
static Compiler_Anon2 *defaultOptionValues;
static Compiler_Anon3 *funcs;
/**
 * Name subtemplates _sub1, _sub2, ...
 */
static NSInteger subtemplateCount = 0;
static NSString *SUBTEMPLATE_PREFIX = @"_sub";
//static NSInteger TEMPLATE_INITIAL_CODE_SIZE = 15;

@synthesize group;

+ (void) initialize
{
    supportedOptions = [Compiler_Anon1 newCompiler_Anon1];
    NUM_OPTIONS = [supportedOptions count];
    defaultOptionValues = [Compiler_Anon2 newCompiler_Anon2];
    funcs = [Compiler_Anon3 newCompiler_Anon3];
}

+ (NSString *) SUBTEMPLATE_PREFIX
{
    return SUBTEMPLATE_PREFIX;
}

+ (NSInteger) supportedOptions
{
    return [supportedOptions count];
}

+ (Compiler_Anon1 *) getSupportedOptions
{
    return supportedOptions;
}

+ (NSInteger) NUM_OPTIONS
{
    return NUM_OPTIONS;
}

+ (Compiler_Anon2 *) defaultOptionValues
{
    return defaultOptionValues;
}

+ (Compiler_Anon3 *) funcs
{
    return funcs;
}

+ (NSInteger) subtemplateCount
{
    return subtemplateCount;
}

+ (Compiler *) newCompiler
{
    return [[Compiler alloc] initWithSTGroup:STGroup.defaultGroup];
}

+ (Compiler *) newCompiler:(STGroup *)aSTGroup
{
    return [[Compiler alloc] initWithSTGroup:aSTGroup];
}

- (id) init
{
    self=[super init];
    if ( self != nil ) {
        group = STGroup.defaultGroup;
        if ( group ) [group retain];
        subtemplateCount = 0;
    }
    return self;
}

- (id) initWithSTGroup:(STGroup *)aSTGroup
{
    self=[super init];
    if ( self != nil ) {
        group = aSTGroup;
        if ( group ) [group retain];
        subtemplateCount = 0;
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in Compiler" );
#endif
    if ( group ) [group release];
    [super dealloc];
}

/**
 * To compile a template, we need to know what the
 * enclosing template is (if any) in case of regions.
 */
- (CompiledST *) compile:(NSString *)template
{
    CompiledST *code = [self compile:nil name:nil args:nil template:template templateToken:nil];
    code.hasFormalArgs = NO;
    return code;
}

/**
 * Compile full template with unknown formal args.
 */
- (CompiledST *) compile:(NSString *)name template:(NSString *)template
{
    CompiledST *code = [self compile:nil name:name args:nil template:template templateToken:nil];
    code.hasFormalArgs = NO;
    return code;
}

/** Compile full template with respect to a list of formal args. */
- (CompiledST *) compile:(NSString *)srcName
                    name:(NSString *)name
                args:(AMutableArray *)args
                template:(NSString *)template
           templateToken:(CommonToken *)aTemplateToken
{
    CompiledST *impl = nil;
    BOOL mustRelease = NO;
    __strong FormalArgument *a;
    if ( args != nil ) {
            [args retain];
        if ( [args count] > 0 ) {
            a = [args objectAtIndex:0];
            [a retain];
            mustRelease = YES;
        }
    }
    if ( args == nil ) {
        NSLog( @"args is nil" );
    }
    ANTLRStringStream *is = [[ANTLRStringStream newANTLRStringStream:template] retain];
    is.name = (srcName != nil) ? srcName : name;
    STLexer *lexer = nil;
	if ( aTemplateToken != nil &&
		 aTemplateToken.type == GroupParser.TBIGSTRING_NO_NL ) {
            lexer = [STLexer_NO_NL newSTLexer_NO_NL:group.errMgr input:is templateToken:aTemplateToken delimiterStartChar:group.delimiterStartChar delimiterStopChar:group.delimiterStopChar];
		}
	else {
        lexer = [STLexer newSTLexer:group.errMgr input:is templateToken:aTemplateToken delimiterStartChar:group.delimiterStartChar delimiterStopChar:group.delimiterStopChar];
	}

    CommonTokenStream *tokens = [[CommonTokenStream newCommonTokenStreamWithTokenSource:lexer] retain];
    STParser *p = [STParser newSTParser:tokens error:group.errMgr token:aTemplateToken];
    STParser_templateAndEOF_return *r = nil;
    
    @try {
        r = [p templateAndEOF];
    }
    @catch (RecognitionException *re) {
        [self reportMessageAndThrowSTException:tokens templateToken:aTemplateToken aParser:p re:re];
        return nil;
    }
    if ([p getNumberOfSyntaxErrors] > 0 || r == nil || r.tree == nil) {
        impl = [CompiledST newCompiledST];
        [impl defineFormalArgs:args];
        return impl;
    }
    CommonTreeNodeStream *nodes = [[CommonTreeNodeStream newCommonTreeNodeStream:r.tree] retain];
    [nodes setTokenStream:tokens];
    CodeGenerator *gen = [CodeGenerator newCodeGenerator:nodes errMgr:group.errMgr name:name template:template token:aTemplateToken];

    @try {
        impl = [[gen template:name arg1:args] retain];
		impl.nativeGroup = group;
        if ( impl.nativeGroup ) [impl.nativeGroup retain];
		impl.template = template;
        if ( template ) [template retain];
        impl.ast = [(CommonTree *)r.tree retain];
        [impl.ast setUnknownTokenBoundaries];
        impl.tokens = [tokens retain];
    }
    @catch (RecognitionException *re) {
        [group.errMgr internalError:nil msg:@"bad tree structure" e:re];
    }
    if ( mustRelease ) [a release];
    return impl;
}

+ (CompiledST *) defineBlankRegion:(CompiledST *)outermostImpl token:(CommonToken *)nameToken
{
    NSString *outermostTemplateName = outermostImpl.name;
    NSString *mangled = [STGroup getMangledRegionName:outermostTemplateName name:nameToken.text];
    CompiledST *blank = [CompiledST newCompiledST];
    blank.isRegion = YES;
    blank.templateDefStartToken = nameToken;
    blank.regionDefType = IMPLICIT;
    blank.name = mangled;
    [outermostImpl addImplicitlyDefinedTemplate:blank];
    return blank;
}

+ (NSString *) getNewSubtemplateName
{
    return [NSString stringWithFormat:@"%@%d", SUBTEMPLATE_PREFIX, ++subtemplateCount];
}

- (void) reportMessageAndThrowSTException:(CommonTokenStream *)tokens
                            templateToken:(CommonToken *)templateToken
                                  aParser:(Parser *)aParser
                                       re:(RecognitionException *)re
{
    NSString *msg;
    if ( re.token.type == STLexer.EOF_TYPE) {
        msg = @"premature EOF";
        [group.errMgr compileTimeError:SYNTAX_ERROR templateToken:templateToken t:re.token arg:msg];
    }
    else if ([re isKindOfClass:[NoViableAltException class]]) {
        msg = [NSString stringWithFormat:@"'%@' came as a complete surprise to me", re.token.text];
        [group.errMgr compileTimeError:SYNTAX_ERROR templateToken:templateToken t:re.token arg:msg];
    }
    else if (tokens.index == 0) {
        msg = [NSString stringWithFormat:@"this doesn't look like a template: \"%@\"", [tokens description]];
        [group.errMgr compileTimeError:SYNTAX_ERROR templateToken:templateToken t:re.token arg:msg];
    }
    else if ([tokens LA:1] == STLexer.LDELIM) {
        msg = @"doesn't look like an expression";
        [group.errMgr compileTimeError:SYNTAX_ERROR templateToken:templateToken t:re.token arg:msg];
    }
    else {
        msg = [aParser getErrorMessage:re TokenNames:[aParser getTokenNames]];
        [group.errMgr compileTimeError:SYNTAX_ERROR templateToken:templateToken t:re.token arg:msg];
    }
    @throw [STException newException:msg];
}

@end
