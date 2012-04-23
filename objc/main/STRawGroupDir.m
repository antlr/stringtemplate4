#import <Foundation/Foundation.h>
#import "STRawGroupDir.h"

/** A dir of templates w/o headers like ST v3 had.  Still allows group files
 *  in dir though like STGroupDir parent.
 */
@implementation STRawGroupDir
+ (STRawGroupDir *) newSTRawGroupDir:(NSString *) aDirName
{
    return [[STRawGroupDir alloc] init:aDirName encoding:NSASCIIStringEncoding delimiterStartChar:'<' delimiterStopChar:'>'];
}

+ (STRawGroupDir *) newSTRawGroupDir:(NSString *) aDirName
                  delimiterStartChar:(char) aDelimiterStartChar
                   delimiterStopChar:(char) aDelimiterStopChar
{
    return [[STRawGroupDir alloc] init:aDirName encoding:NSASCIIStringEncoding delimiterStartChar:aDelimiterStartChar delimiterStopChar:aDelimiterStopChar];
}

+ (STRawGroupDir *) newSTRawGroupDir:(NSString *) aDirName
                            encoding:(NSStringEncoding) anEncoding
{
    return [[STRawGroupDir alloc] init:aDirName encoding:anEncoding delimiterStartChar:'<' delimiterStopChar:'>'];
}

+ (STRawGroupDir *) newSTRawGroupDir:(NSString *) aDirName
                            encoding:(NSString *) anEncoding
                  delimiterStartChar:(char) aDelimiterStartChar
                   delimiterStopChar:(char) aDelimiterStopChar
{
    return [[STRawGroupDir alloc] init:aDirName encoding:NSASCIIStringEncoding delimiterStartChar:aDelimiterStartChar delimiterStopChar:aDelimiterStopChar];
}

+ (STRawGroupDir *) newSTRawGroupDirWithURL:(NSURL *) aRoot
                                   encoding:(NSStringEncoding) anEncoding
                         delimiterStartChar:(char) aDelimiterStartChar
                          delimiterStopChar:(char) aDelimiterStopChar
{
    return [[STRawGroupDir alloc] initWithURL:aRoot encoding:NSASCIIStringEncoding delimiterStartChar:aDelimiterStartChar delimiterStopChar:aDelimiterStopChar];
}

- (id) init:(NSString *)aDirName encoding:(NSStringEncoding)theEncoding delimiterStartChar:(unichar)aDelimiterStartChar delimiterStopChar:(unichar)aDelimiterStopChar
{
    self = [super init:aDirName encoding:(NSStringEncoding)theEncoding delimiterStartChar:(unichar)aDelimiterStartChar delimiterStopChar:(unichar)aDelimiterStopChar];
    if ( self != nil ) {
       
    }
    return self;
}

- (id) initWithURL:(NSURL *)aRoot
          encoding:(NSStringEncoding)theEncoding
delimiterStartChar:(unichar)aDelimiterStartChar
 delimiterStopChar:(unichar)aDelimiterStopChar
{
    self = [super initWithURL:aRoot
                     encoding:(NSStringEncoding)theEncoding
           delimiterStartChar:(unichar)aDelimiterStartChar
            delimiterStopChar:(unichar)aDelimiterStopChar];
    if ( self != nil ) {
       
    }
    return self;
}

- (CompiledST *)loadTemplateFile:(NSString *) aPrefix
                        fileName:(NSString *) unqualifiedFileName
                  templateStream:(id<CharStream>) aTemplateStream
{
    NSString *template = [aTemplateStream substringWithRange:NSMakeRange(0, [aTemplateStream size] - 1)];
    NSString *templateName = [Misc getFileNameNoSuffix:unqualifiedFileName];
    CompiledST *impl = [[Compiler newCompiler] compile:templateName template:template];
    impl.prefix = aPrefix;
    return impl;
}
@end
