#import <Foundation/Foundation.h>
#import <ANTLR/CharStream.h>
#import "STGroupDir.h"

/** A dir of templates w/o headers like ST v3 had.  Still allows group files
 *  in dir though like STGroupDir parent.
 */
@interface STRawGroupDir : STGroupDir {
}

+ (STRawGroupDir *) newSTRawGroupDir:(NSString *) aDirName;
+ (STRawGroupDir *) newSTRawGroupDir:(NSString *) aDirName
                  delimiterStartChar:(char) aDelimiterStartChar
                   delimiterStopChar:(char) aDelimiterStopChar;
+ (STRawGroupDir *) newSTRawGroupDir:(NSString *) aDirName
                            encoding:(NSStringEncoding) anEncoding;
+ (STRawGroupDir *) newSTRawGroupDir:(NSString *) aDirName
                            encoding:(NSStringEncoding) anEncoding
                  delimiterStartChar:(char) aDelimiterStartChar
                   delimiterStopChar:(char) aDelimiterStopChar;
+ (STRawGroupDir *) newSTRawGroupDirWithURL:(NSURL *) aRoot
                            encoding:(NSStringEncoding) anEncoding
                  delimiterStartChar:(char) aDelimiterStartChar
                   delimiterStopChar:(char) aDelimiterStopChar;

- (id) init:(NSString *)dirName encoding:(NSStringEncoding)theEncoding
                      delimiterStartChar:(unichar)aDelimiterStartChar
                       delimiterStopChar:(unichar)aDelimiterStopChar;

- (id) initWithURL:(NSString *)dirName
          encoding:(NSStringEncoding)theEncoding
delimiterStartChar:(unichar)aDelimiterStartChar
 delimiterStopChar:(unichar)aDelimiterStopChar;

- (CompiledST *)loadTemplateFile:(NSString *) aPrefix
                        fileName:(NSString *) aFileName
                  templateStream:(id<CharStream>) aTemplateStream;

@end