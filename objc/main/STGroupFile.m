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
#import <ANTLR/RuntimeException.h>
#import "STGroupFile.h"
#import "STException.h"
#import "ErrorManager.h"

@implementation STGroupFile

@synthesize alreadyLoaded;
@synthesize fileName;
@synthesize URL;

+ (id) newSTGroupFile
{
    return [[[STGroupFile alloc] init] retain];
}

+ (id) newSTGroupFile:(NSString *)aFileName
{
    return [[[STGroupFile alloc] initWithFileName:aFileName encoding:NSASCIIStringEncoding delimiterStartChar:'<' delimiterStopChar:'>'] retain];
}

+ (id) newSTGroupFile:(NSString *)aFileName encoding:(NSStringEncoding)theEncoding
{
    return [[[STGroupFile alloc] initWithFileName:aFileName encoding:theEncoding delimiterStartChar:'<' delimiterStopChar:'>'] retain];
}

+ (id) newSTGroupFile:(NSString *)aFileName delimiterStartChar:(unichar)aDelimiterStartChar delimiterStopChar:(unichar)aDelimiterStopChar
{
    return [[[STGroupFile alloc] initWithFileName:aFileName encoding:NSASCIIStringEncoding delimiterStartChar:aDelimiterStartChar delimiterStopChar:aDelimiterStopChar] retain];
}

+ (id) newSTGroupFile:(NSString *)aFileName encoding:(NSStringEncoding)theEncoding delimiterStartChar:(unichar)aDelimiterStartChar delimiterStopChar:(unichar)aDelimiterStopChar
{
    return [[[STGroupFile alloc] initWithFileName:aFileName encoding:theEncoding delimiterStartChar:aDelimiterStartChar delimiterStopChar:aDelimiterStopChar] retain];
}

+ (id) newSTGroupFileWithFQFN:(NSString *)aFileName encoding:(NSStringEncoding)theEncoding;
{
    return [[[STGroupFile alloc] initWithFQFN:aFileName encoding:theEncoding delimiterStartChar:'<' delimiterStopChar:'>'] retain];
}

+ (id) newSTGroupFileWithFQFN:(NSString *)aFileName encoding:(NSStringEncoding)theEncoding delimiterStartChar:(unichar)aDelimiterStartChar delimiterStopChar:(unichar)aDelimiterStopChar;
{
    return [[[STGroupFile alloc] initWithFQFN:aFileName encoding:theEncoding delimiterStartChar:aDelimiterStartChar delimiterStopChar:aDelimiterStopChar] retain];
}

+ (id) newSTGroupFileWithURL:(NSURL *)aURL encoding:(NSStringEncoding)theEncoding delimiterStartChar:(unichar)aDelimiterStartChar delimiterStopChar:(unichar)aDelimiterStopChar
{
    return [[[STGroupFile alloc] initWithURL:aURL encoding:theEncoding delimiterStartChar:aDelimiterStartChar delimiterStopChar:aDelimiterStopChar] retain];
}

- (id) init
{
    self=[super init:'<' delimiterStopChar:'>'];
    if ( self != nil ) {
        alreadyLoaded = NO;
        fileName = nil;
        encoding = NSASCIIStringEncoding;
    }
    return self;
}

- (id) initWithFileName:(NSString *)aFileName encoding:(NSStringEncoding)theEncoding delimiterStartChar:(unichar)aDelimiterStartChar delimiterStopChar:(unichar)aDelimiterStopChar
{
    BOOL fExists, isDir;
    self = [super init:aDelimiterStartChar delimiterStopChar:aDelimiterStopChar];
    if ( self != nil ) {
        if (![aFileName hasSuffix:@".stg"]) {
            @throw [IllegalArgumentException newException:[NSString stringWithFormat:@"Group file names must end in .stg: %@", aFileName]];
        }
        @try {
            fileName = aFileName;
            if ( fileName ) [fileName retain];
            if ((aFileName != nil) && ([aFileName characterAtIndex:0] == '~'))
                fileName = [aFileName stringByExpandingTildeInPath];
            alreadyLoaded = NO;
            encoding = theEncoding;
            NSFileManager *fm;
            fm = [NSFileManager defaultManager];
            fExists = [fm fileExistsAtPath:fileName isDirectory:&isDir];
            if (fExists && isDir) {
                @try {
                   URL = [NSURL fileURLWithPath:fileName];
                   if ( URL ) [URL retain];
                }
                @catch (MalformedURLException *e) {
                    @throw [MalformedURLException newException:fileName];
                }
                if ( STGroup.verbose )
                    NSLog(@"STGroupFile(%@) == file %@\n", aFileName, fileName);
            }
            else {
#ifdef DONTUSEYET
                ClassLoader *cl = [[Thread currentThread] contextClassLoader];
                URL = [cl getResource:fileName];
                if (URL == nil) {
                    cl = [[self class] classLoader];
                    URL = [cl getResource:fileName];
                }
#endif
                URL = [NSURL fileURLWithPath:fileName];
                if (URL == nil) {
                    @throw [IllegalArgumentException newException:[NSString stringWithFormat:@"No such group file: %@", fileName]];
                }
            }
        }
        @catch (NSException *e) {
            [errMgr internalError:nil msg:[NSString stringWithFormat:@"can't load group file %@", fileName] e:e];
        }
    }
    return self;
}

- (id) initWithFQFN:(NSString *)fullyQualifiedFileName encoding:(NSStringEncoding)theEncoding delimiterStartChar:(unichar)aDelimiterStartChar delimiterStopChar:(unichar)aDelimiterStopChar
{
    self=[super init:aDelimiterStartChar delimiterStopChar:aDelimiterStopChar];
    if ( self != nil ) {
        alreadyLoaded = NO;
        URL = [NSURL fileURLWithPath:fullyQualifiedFileName];
        if ( URL ) [URL retain];
        encoding = theEncoding;
    }
    return self;
}

- (id) initWithURL:(NSURL *)aURL encoding:(NSStringEncoding)theEncoding delimiterStartChar:(unichar)aDelimiterStartChar delimiterStopChar:(unichar)aDelimiterStopChar
{
    self=[super init:aDelimiterStartChar delimiterStopChar:aDelimiterStopChar];
    if ( self != nil ) {
        alreadyLoaded = NO;
        URL = aURL;
        if ( URL ) [URL retain];
        encoding = theEncoding;
    }
    return self;
}

- (void) dealloc {
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in STGroupDir" );
#endif
    if ( fileName ) [fileName release];
    if ( URL ) [URL release];
    [super dealloc];
}

- (BOOL) isDictionary:(NSString *)name
{
	if ( !alreadyLoaded ) [self load];
	return [super isDictionary:name];
}

- (BOOL) isDefined:(NSString *)name
{
    if (!alreadyLoaded)
        [self load];
    return [super isDefined:name];
}

- (void) unload
{
    [super unload];
    alreadyLoaded = NO;
}

- (CompiledST *) load:(NSString *)aName
{
    if (!alreadyLoaded)
        [self load];
    return [self rawGetTemplate:aName];
}

- (void) load
{
    if (alreadyLoaded)
        return;
    alreadyLoaded = YES; // do before actual load to say we're doing it
        // no prefix since this group file is the entire group, nothing lives
        // beneath it.
    if ( STGroup.verbose ) NSLog(@"loading group file %@\n", [URL description]);
    [self loadGroupFile:@"/" fileName:fileName];
    if ( STGroup.verbose ) NSLog(@"found %d templates in %@ = %@\n", [templates count], [URL description], [templates allKeys]);
 }

- (NSString *) show
{
    if (!alreadyLoaded)
        [self load];
    return [super show];
}

- (NSString *) getName
{
    return [Misc getFileNameNoSuffix:fileName];
}

- (NSString *) getFileName
{
    return fileName;
}

- (NSURL *) getRootDirURL
{
    NSString *parent = [Misc stripLastPathElement:fileName];
    @try {
        // return [NSURL newURL:parent];
		return [NSURL URLWithString:parent];
	}
    @catch (MalformedURLException *me) {
		[errMgr runTimeError:nil
                         who:nil
                          ip:0
                       error:INVALID_TEMPLATE_NAME
                           e:me
                         arg:fileName];
	}
    return nil;
    //		try {
    //			return new File(parent).toURI().toURL();
    //		}
    //		catch (MalformedURLException me) {
    //			errMgr.runTimeError(null, 0, ErrorType.INVALID_TEMPLATE_NAME,
    //								me, parent);
    //		}
    //		return null;
    //		File f = new File(path);
    //		System.out.println("getRootDir: path="+path);
    //		System.out.println("parent file="+f.getParentFile());
    //		System.out.println("parent="+f.getParent());
    //		System.out.println("filename="+fileName);
    //		try {
    //			return f.getParentFile().toURI().toURL();
    //		}
    //		catch (MalformedURLException me) {
    //			[errMgr.runTimeError(nil, nil, ErrorType.INVALID_TEMPLATE_NAME,
    //								me, f.getParentFile());
    //		}
    //      return nil;
}

@end
