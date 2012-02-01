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
#import "CompiledST.h"
#import "STGroup.h"
#import "Misc.h"

/**
 * The internal representation of a single group file (which must end in
 * ".stg").  If we fail to find a group file, look for it via the
 * CLASSPATH as a resource.
 */

@interface STGroupFile : STGroup {
  __strong NSString *fileName;
  __strong NSURL *URL;
  BOOL alreadyLoaded;
}

+ (id) newSTGroupFile;
+ (id) newSTGroupFile:(NSString *)aFileName;
+ (id) newSTGroupFile:(NSString *)aFileName encoding:(NSStringEncoding)theEncoding;
+ (id) newSTGroupFile:(NSString *)aFileName delimiterStartChar:(unichar)aDelimiterStartChar delimiterStopChar:(unichar)aDelimiterStopChar;
+ (id) newSTGroupFile:(NSString *)aFileName encoding:(NSStringEncoding)theEncoding delimiterStartChar:(unichar)aDelimiterStartChar delimiterStopChar:(unichar)aDelimiterStopChar;
+ (id) newSTGroupFileWithFQFN:(NSString *)aFileName encoding:(NSStringEncoding)theEncoding;
+ (id) newSTGroupFileWithFQFN:(NSString *)aFileName encoding:(NSStringEncoding)theEncoding delimiterStartChar:(unichar)aDelimiterStartChar delimiterStopChar:(unichar)aDelimiterStopChar;
+ (id) newSTGroupFileWithURL:(NSURL *)aURL encoding:(NSStringEncoding)theEncoding delimiterStartChar:(unichar)aDelimiterStartChar delimiterStopChar:(unichar)aDelimiterStopChar;

- (id) init;
- (id) initWithFileName:(NSString *)fileName encoding:(NSStringEncoding)encoding delimiterStartChar:(unichar)delimiterStartChar delimiterStopChar:(unichar)delimiterStopChar;
- (id) initWithFQFN:(NSString *)fullyQualifiedFileName encoding:(NSStringEncoding)encoding delimiterStartChar:(unichar)delimiterStartChar delimiterStopChar:(unichar)delimiterStopChar;
- (id) initWithURL:(NSURL *)aURL encoding:(NSStringEncoding)encoding delimiterStartChar:(unichar)delimiterStartChar delimiterStopChar:(unichar)delimiterStopChar;
- (BOOL) isDictionary:(NSString *)name;
- (BOOL) isDefined:(NSString *)name;
- (void) unload;
- (CompiledST *) load:(NSString *)name;
- (void) load;
- (NSString *) show;
- (NSString *) getName;
- (NSString *) getFileName;
- (NSURL *) getRootDirURL;

@property (assign) BOOL alreadyLoaded;
@property (retain) NSString *fileName;
@property (retain) NSURL *URL;

@end
