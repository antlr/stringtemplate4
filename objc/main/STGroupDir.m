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
#import "STGroupDir.h"
#import "STException.h"
#import "GroupLexer.h"
#import "GroupParser.h"

@implementation STGroupDir

@synthesize groupDirName;
@synthesize root;

+ (id) newSTGroupDir:(NSString *)aDirName
{
    return [[STGroupDir alloc] init:aDirName encoding:NSASCIIStringEncoding delimiterStartChar:'<' delimiterStopChar:'>'];
}

+ (id) newSTGroupDir:(NSString *)aDirName encoding:(NSStringEncoding)theEncoding
{
    return [[STGroupDir alloc] init:aDirName encoding:theEncoding delimiterStartChar:'<' delimiterStopChar:'>'];
}

+ (id) newSTGroupDir:(NSString *)aDirName delimiterStartChar:(unichar)aDelimiterStartChar delimiterStopChar:(unichar)aDelimiterStopChar
{
    return [[STGroupDir alloc] init:aDirName encoding:NSASCIIStringEncoding delimiterStartChar:aDelimiterStartChar delimiterStopChar:aDelimiterStopChar];
}

+ (id) newSTGroupDir:(NSString *)aDirName encoding:(NSStringEncoding)theEncoding delimiterStartChar:(unichar)aDelimiterStartChar delimiterStopChar:(unichar)aDelimiterStopChar
{
    return [[STGroupDir alloc] init:aDirName encoding:theEncoding delimiterStartChar:aDelimiterStartChar delimiterStopChar:aDelimiterStopChar];
}

+ (id) newSTGroupDirWithURL:(NSURL *)theRoot encoding:(NSStringEncoding)theEncoding delimiterStartChar:(unichar)aDelimiterStartChar delimiterStopChar:(unichar)aDelimiterStopChar
{
    return [[STGroupDir alloc] initWithURL:theRoot encoding:theEncoding delimiterStartChar:aDelimiterStartChar delimiterStopChar:aDelimiterStopChar];
}

- (id) init:(NSString *)aDirName encoding:(NSStringEncoding)theEncoding delimiterStartChar:(unichar)aDelimiterStartChar delimiterStopChar:(unichar)aDelimiterStopChar
{
    BOOL fExists, isDir;
    self=[super init:aDelimiterStartChar delimiterStopChar:aDelimiterStopChar];
    if ( self != nil ) {
        groupDirName = aDirName;
        encoding = theEncoding;
        @try {
            NSFileManager *fm;
            fm = [NSFileManager defaultManager];
            fExists = [fm fileExistsAtPath:aDirName isDirectory:&isDir];
            if (fExists && isDir) {
                root = [NSURL fileURLWithPath:aDirName];
            }
            else {
                [NSThread currentThread];
#ifdef DONTUSEYET
                ClassLoader *cl = [[NSThread currentThread] contextClassLoader];
                root = [cl getResource:aDirName];
                if (root == nil) {
                    cl = [[self class] classLoader];
                    root = [cl getResource:aDirName];
                }
                if (root == nil) {
                    @throw [IllegalArgumentException newException:[NSString stringWithFormat@"No such directory: %@", aDirName]];
                }
#endif
            }
        }
        @catch (NSException *e) {
            [errMgr internalError:nil msg:[NSString stringWithFormat:@"can't load group dir %@", aDirName] e:e];
        }
    }
    return self;
}

- (id) initWithURL:(NSURL *)theRoot encoding:(NSStringEncoding)theEncoding delimiterStartChar:(unichar)aDelimiterStartChar delimiterStopChar:(unichar)aDelimiterStopChar
{
    self=[super init:aDelimiterStartChar delimiterStopChar:aDelimiterStopChar];
    if ( self != nil ) {
        root = theRoot;
        if ( root ) [root retain];
        encoding = theEncoding;
    }
    return self;
}

- (void) dealloc {
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in STGroupDir" );
#endif
    if ( groupDirName ) [groupDirName release];
    if ( root ) [root release];
    [super dealloc];
}

/**
 * Load a template from dir or group file.  Group file is given
 * precedence over dir with same name.
 */
- (CompiledST *) load:(NSString *)aName
{
    NSURL *groupFileURL = nil;
    NSFileHandle *fh;
    NSError *error = nil;
    
    if ( STGroup.verbose ) NSLog(@"STGroupDir.load(%@)", aName);
    NSString *parent = [Misc getParent:aName];
    NSString *prefix = [Misc getPrefix:aName];
    NSLog( @"parent = \"%@\"\nprefix = \"%@\"\nroot = \"%@\"\n", parent, prefix, root );
    @try {
        // fileName = [NSString stringWithFormat:@"%@.stg", parent] stringByStandardizingPath];
        // groupFileURL = [NSURL fileURLWithPath:[[root URLByAppendingPathComponent:@"%@.stg", parent] stringByStandardizingPath]];
        groupFileURL = [[NSURL fileURLWithPath:[NSString stringWithFormat:@"%@%@.stg", [root path], parent]] URLByStandardizingPath];
    }
    @catch (MalformedURLException *e) {
        [errMgr internalError:nil msg:[NSString stringWithFormat:@"bad URL: %@%@.stg", root, parent] e:e];
        return nil;
    }
    ANTLRInputStream *is = nil;
    @try {
        //is = [fh openStream];
        fh = [NSFileHandle fileHandleForReadingFromURL:groupFileURL error:&error];
        if (error != nil) {
            NSLog( @"%@", [error localizedDescription] );
            NSException *myException = [FileNotFoundException newException:@"File Not Found on System"];
            @throw myException;
        }
        is = [ANTLRInputStream newANTLRInputStream:fh];
    }
    @catch (FileNotFoundException *fnfe) {
        NSString *unqualifiedName = [Misc getFileName:aName];
        return [self loadTemplateFile:prefix fileName:[NSString stringWithFormat:@"%@.st", unqualifiedName]];
    }
    @catch (IOException *ioe) {
        [errMgr internalError:nil msg:[@"can't load template file " stringByAppendingString:aName] e:ioe];
    }
    
    @try {
        if (is != nil)
            [is close];
    }
    @catch (IOException *ioe) {
        [errMgr internalError:nil msg:[@"can't close template file stream " stringByAppendingString:aName] e:ioe];
    }
    [self loadGroupFile:prefix fileName:[NSString stringWithFormat:@"%@%@.stg", root, parent]];
    return [self rawGetTemplate:aName];
}


/**
 * Load full path name .st file relative to root by prefix
 */
- (CompiledST *) loadTemplateFile:(NSString *)prefix fileName:(NSString *)unqualifiedFileName
{
    NSFileHandle *fh = nil;
    NSURL *f = nil;
    NSError *error = nil;
    
    if ( STGroup.verbose ) NSLog(@"loadTemplateFile(%@) in groupdir from %@ prefix=%@", unqualifiedFileName, root, prefix);
    @try {
        //f = [NSURL fileURLWithPath:[root URLByAppendingPathComponent:aFileName]];
        f = [NSURL fileURLWithPath:[NSString stringWithFormat:@"%@%@%@", [root path], prefix, unqualifiedFileName]];
        if (![f isFileURL]) {
            MalformedURLException *me = [MalformedURLException newException:@"Not a File URL"];
            @throw me;
        }
    }
    @catch (MalformedURLException *me) {
        [errMgr runTimeError:nil who:nil ip:0 error:INVALID_TEMPLATE_NAME e:me arg:[f absoluteString]];
        return nil;
    }
    
    ANTLRInputStream *fs;
    @try {
        fh = [NSFileHandle fileHandleForReadingFromURL:f error:&error];
        if (error != nil) {
            NSLog(@"%@", [error localizedDescription] );
            IOException *IOExcept = [IOException newException:@"Error opening file"];
            @throw IOExcept;
        }
        fs = [ANTLRInputStream newANTLRInputStream:fh encoding:encoding];
        fs.name = unqualifiedFileName;
    }
    @catch (IOException *ioe) {
        return nil;
    }
#ifdef DONTUSENOMO
    GroupLexer *lexer = [GroupLexer newGroupLexerWithCharStream:fs];
    fs.name = unqualifiedFileName;
    CommonTokenStream *tokens = [CommonTokenStream newCommonTokenStreamWithTokenSource:lexer];
    GroupParser *aParser = [GroupParser newGroupParser:tokens];
    aParser.group = self;
    lexer.group = self;
    
    @try {
        [aParser templateDef:prefix];
    }
    @catch (RecognitionException *re) {
        if ( STGroup.verbose ) NSLog(@"%@/%@ doesn't exist", root, unqualifiedFileName);
        //[errMgr groupSyntaxError:SYNTAX_ERROR srcName:[Misc getFileName:[f absoluteString]] e:re msg:[re reason]];
    }
#endif
    return [self loadTemplateFile:prefix fileName:unqualifiedFileName stream:fs];
}

- (NSString *) getName
{
    return groupDirName;
}

- (NSString *) getFileName
{
    return [root lastPathComponent];
}

- (NSString *) getRootDir
{
    return groupDirName;
}

@end
