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
#import "ErrorType.h"

@implementation ErrorType

static NSString *ErrorType_Data[NUM_OF_ERRORENUMS] = {
@"no such template: %@",
@"no such template: super.%@",
@"attribute %@ isn't defined",
@"implicitly-defined attribute %s not visible",
@"missing argument definitions",
@"no such property or can't access: %@",
@"iterating through %@ values in zip map but template has %@ declared arguments",
@"passed %d arg(s) to template %@ with %d declared arg(s)",
@"function %@ expects a string not %@",
@"%s(Writer) constructor doesn't exist",
@"can't find template(s) in import \"%s\"",
@"%@",
@"redefinition of template %@",
@"region %@ is embedded and thus already implicitly defined",
@"redefinition of region %@",
@"redefinition of dictionary %@",
@"cannot alias %@ to undefined template: %@",
@"redefinition of template %@ as a map",
@"%@",
@"missing dictionary default value",
@"no such function: %@",
@"template %@ doesn't have a region called %@",
@"no such option: %@",
@"invalid template name or path: %@",
@"anonymous template has %d arg(s) but mapped across %d value(s)",
@"required parameters (%s) must appear before optional parameters",
@"%@",
@"error writing output caused by",
@"can't load group file %@" };

+ (NSString *) NO_SUCH_TEMPLATE
{
//    return @"no such template: %@";
    return ErrorType_Data[NO_SUCH_TEMPLATE];
}

+ (NSString *) NO_IMPORTED_TEMPLATE
{
//    return @"no such template: super.%@";
    return ErrorType_Data[NO_IMPORTED_TEMPLATE];
}

+ (NSString *) NO_SUCH_ATTRIBUTE
{
//    return @"attribute %@ isn't defined";
    return ErrorType_Data[NO_SUCH_ATTRIBUTE];
}

+ (NSString *) REF_TO_IMPLICIT_ATTRIBUTE_OUT_OF_SCOPE
{
//    return @"attribute %@ isn't defined";
    return ErrorType_Data[REF_TO_IMPLICIT_ATTRIBUTE_OUT_OF_SCOPE];
}

+ (NSString *) MISSING_FORMAL_ARGUMENTS
{
//    return @"missing argument definitions";
    return ErrorType_Data[MISSING_FORMAL_ARGUMENTS];
}

+ (NSString *) NO_SUCH_PROPERTY
{
//    return @"no such property or can't access: %@";
    return ErrorType_Data[NO_SUCH_PROPERTY];
}

+ (NSString *) MAP_ARGUMENT_COUNT_MISMATCH
{
//    return @"iterating through %@ values in zip map but template has %@ declared arguments";
    return ErrorType_Data[MAP_ARGUMENT_COUNT_MISMATCH];
}

+ (NSString *) ARGUMENT_COUNT_MISMATCH
{
//    return @"passed %@ arg(s) to template %@ with %@ declared arg(s)";
    return ErrorType_Data[ARGUMENT_COUNT_MISMATCH];
}

+ (NSString *) EXPECTING_STRING
{
//    return @"function %@ expects a string not %@";
    return ErrorType_Data[EXPECTING_STRING];
}

+ (NSString *) WRITER_CTOR_ISSUE;
{
    //    return @"%s(Writer) constructor doesn't exist",
    return ErrorType_Data[WRITER_CTOR_ISSUE];
}

+ (NSString *) CANT_IMPORT;
{
    //    return @"can't find template(s) in import \"%s\"",
    return ErrorType_Data[CANT_IMPORT];
}


// COMPILE-TIME SYNTAX/SEMANTIC ERRORS
+ (NSString *) SYNTAX_ERROR
{
//    return @"%@";
    return ErrorType_Data[SYNTAX_ERROR];
}

+ (NSString *) TEMPLATE_REDEFINITION
{
//    return @"redefinition of template %@";
    return ErrorType_Data[TEMPLATE_REDEFINITION];
}

+ (NSString *) EMBEDDED_REGION_REDEFINITION
{
    //    return @"region %@ is embedded and thus already implicitly defined";
    return ErrorType_Data[EMBEDDED_REGION_REDEFINITION];
}

+ (NSString *) REGION_REDEFINITION
{
    //    return @"redefinition of region %@";
    return ErrorType_Data[REGION_REDEFINITION];
}

+ (NSString *) MAP_REDEFINITION
{
    //    return @"redefinition of dictionary %@";
    return ErrorType_Data[MAP_REDEFINITION];
}

+ (NSString *) ALIAS_TARGET_UNDEFINED
{
    //    return @"cannot alias %@ to undefined template: %@";
    return ErrorType_Data[ALIAS_TARGET_UNDEFINED];
}

+ (NSString *) TEMPLATE_REDEFINITION_AS_MAP
{
    //    return @"redefinition of template %@ as a map";
    return ErrorType_Data[TEMPLATE_REDEFINITION_AS_MAP];
}

+ (NSString *) LEXER_ERROR
{
    //    return @"%@";
    return ErrorType_Data[LEXER_ERROR];
}

+ (NSString *) NO_DEFAULT_VALUE
{
    //    return @"missing dictionary default value";
    return ErrorType_Data[NO_DEFAULT_VALUE];
}

+ (NSString *) NO_SUCH_FUNCTION
{
    //    return @"no such function: %@";
    return ErrorType_Data[NO_SUCH_FUNCTION];
}

+ (NSString *) NO_SUCH_REGION
{
    //    return @"template %@ doesn't have a region called %@";
    return ErrorType_Data[NO_SUCH_REGION];
}

+ (NSString *) NO_SUCH_OPTION
{
    //    return @"no such option: %@";
    return ErrorType_Data[NO_SUCH_OPTION];
}

+ (NSString *) INVALID_TEMPLATE_NAME
{
    //    return @"invalid template name or path: %@";
    return ErrorType_Data[INVALID_TEMPLATE_NAME];
}

+ (NSString *) ANON_ARGUMENT_MISMATCH
{
    //    return @"anonymous template has %@ arg(s) but mapped across %@ value(s)";
    return ErrorType_Data[ANON_ARGUMENT_MISMATCH];
}

+ (NSString *) REQUIRED_PARAMETER_AFTER_OPTIONAL
{
    //  return @"required parameters (%s) must appear before optional parameters",
    return ErrorType_Data[REQUIRED_PARAMETER_AFTER_OPTIONAL];
}

// INTERNAL ERRORS
+ (NSString *) INTERNAL_ERROR
{
    //    return @"%@";
    return ErrorType_Data[INTERNAL_ERROR];
}

+ (NSString *) WRITE_IO_ERROR
{
    //    return @"error writing output caused by";
    return ErrorType_Data[WRITE_IO_ERROR];
}

+ (NSString *) CANT_LOAD_GROUP_FILE
{
    //    return @"can't load group file %@";
    return ErrorType_Data[SYNTAX_ERROR];
}

+ (NSString *) ErrorNum:(NSInteger)anErr
{
    if (anErr < 0 || anErr >= NUM_OF_ERRORENUMS)
        return @"ILLEGAL ERROR NUMBER";
    return ErrorType_Data[anErr];
}

+ (id) newErrorType
{
    return [[ErrorType alloc] init];
}

+ (id) newErrorTypeWithErrNum:(NSInteger) msgNum
{
    return [[ErrorType alloc] init];
}

+ (id) newErrorTypeWithMsg:(NSString *) aMsg
{
    return [[ErrorType alloc] initWithMsg:aMsg];
}

- (id) init
{
    self=[super init];
    if (self != nil ) {
        message = nil;
        NSInteger idx = 0;
        msgs = [AMutableDictionary dictionaryWithCapacity:NUM_OF_ERRORENUMS];
        for (idx = 0; idx < NUM_OF_ERRORENUMS; idx++ ) {
            [msgs setObject:ErrorType_Data[idx] forKey:[NSString stringWithFormat:@"%d", idx]];
        }
    }
    return self;
}

- (id) initWithErrNum:(NSInteger) aNum
{
    self=[super init];
    if (self != nil ) {
        message = nil;
        NSInteger idx = 0;
        msgs = [AMutableDictionary dictionaryWithCapacity:NUM_OF_ERRORENUMS];
        for (idx = 0; idx < NUM_OF_ERRORENUMS; idx++ ) {
            [msgs setObject:ErrorType_Data[idx] forKey:[NSString stringWithFormat:@"%d", idx]];
        }
        message = [NSString stringWithString:[msgs objectForKey:[self description:aNum]]];
    }
    return self;
}

- (id) initWithMsg:(NSString *) aMsg
{
    self=[super init];
    if (self != nil ) {
        message = [NSString stringWithString:aMsg];
        if ( message ) [message retain];
        NSInteger idx = 0;
        msgs = [[AMutableDictionary dictionaryWithCapacity:NUM_OF_ERRORENUMS] retain];
        for (idx = 0; idx < NUM_OF_ERRORENUMS; idx++ ) {
            [msgs setObject:ErrorType_Data[idx] forKey:[NSString stringWithString:[self description:idx]]];
        }
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in ErrorType" );
#endif
    if ( message ) [message release];
    if ( msgs ) [msgs release];
    [super dealloc];
}

- (NSInteger) ErrorTypeValueOf:(NSString *)text
{
    if (text) {
        if ([text isEqualToString:@"NO_SUCH_TEMPLATE"])
            return NO_SUCH_TEMPLATE;
        else if ([text isEqualToString:@"NO_IMPORTED_TEMPLATE"])
            return NO_IMPORTED_TEMPLATE;
        else if ([text isEqualToString:@"NO_SUCH_ATTRIBUTE"])
            return NO_SUCH_ATTRIBUTE;
        else if ([text isEqualToString:@"REF_TO_IMPLICIT_ATTRIBUTE_OUT_OF_SCOPE"])
            return REF_TO_IMPLICIT_ATTRIBUTE_OUT_OF_SCOPE;
        else if ([text isEqualToString:@"MISSING_FORMAL_ARGUMENTS"])
            return MISSING_FORMAL_ARGUMENTS;
        else if ([text isEqualToString:@"NO_SUCH_PROPERTY"])
            return NO_SUCH_PROPERTY;
        else if ([text isEqualToString:@"MAP_ARGUMENT_COUNT_MISMATCH"])
            return MAP_ARGUMENT_COUNT_MISMATCH;
        else if ([text isEqualToString:@"ARGUMENT_COUNT_MISMATCH"])
            return ARGUMENT_COUNT_MISMATCH;
        else if ([text isEqualToString:@"EXPECTING_STRING"])
            return EXPECTING_STRING;
        else if ([text isEqualToString:@"WRITER_CTOR_ISSUE"])
            return WRITER_CTOR_ISSUE;
        else if ([text isEqualToString:@"CANT_IMPORT"])
            return CANT_IMPORT;
        else if ([text isEqualToString:@"SYNTAX_ERROR"])
            return SYNTAX_ERROR;
        else if ([text isEqualToString:@"TEMPLATE_REDEFINITION"])
            return TEMPLATE_REDEFINITION;
        else if ([text isEqualToString:@"EMBEDDED_REGION_REDEFINITION"])
            return EMBEDDED_REGION_REDEFINITION;
        else if ([text isEqualToString:@"REGION_REDEFINITION"])
            return REGION_REDEFINITION;
        else if ([text isEqualToString:@"MAP_REDEFINITION"])
            return MAP_REDEFINITION;
        else if ([text isEqualToString:@"ALIAS_TARGET_UNDEFINED"])
            return ALIAS_TARGET_UNDEFINED;
        else if ([text isEqualToString:@"TEMPLATE_REDEFINITION_AS_MAP"])
            return TEMPLATE_REDEFINITION_AS_MAP;
        else if ([text isEqualToString:@"LEXER_ERROR"])
            return LEXER_ERROR;
        else if ([text isEqualToString:@"NO_DEFAULT_VALUE"])
            return NO_DEFAULT_VALUE;
        else if ([text isEqualToString:@"NO_SUCH_FUNCTION"])
            return NO_SUCH_FUNCTION;
        else if ([text isEqualToString:@"NO_SUCH_REGION"])
            return NO_SUCH_REGION;
        else if ([text isEqualToString:@"NO_SUCH_OPTION"])
            return NO_SUCH_OPTION;
        else if ([text isEqualToString:@"INVALID_TEMPLATE_NAME"])
            return INVALID_TEMPLATE_NAME;
        else if ([text isEqualToString:@"ANON_ARGUMENT_MISMATCH"])
            return ANON_ARGUMENT_MISMATCH;
        else if ([text isEqualToString:@"REQUIRED_PARAMETER_AFTER_OPTIONAL"])
            return REQUIRED_PARAMETER_AFTER_OPTIONAL;
        else if ([text isEqualToString:@"INTERNAL_ERROR"])
            return INTERNAL_ERROR;
        else if ([text isEqualToString:@"WRITE_IO_ERROR"])
            return WRITE_IO_ERROR;
        else if ([text isEqualToString:@"CANT_LOAD_GROUP_FILE"])
            return CANT_LOAD_GROUP_FILE;
    }
    return -1;
}

- (NSString *) description
{
    return (message != nil) ? message : @"message=<nil>";
}

- (NSString *) toString
{
    return [self description];
}

- (NSString *) toString:(NSInteger) value
{
    return [self description:value];
}

- (NSString *) description:(NSInteger) value
{
    if (value < NO_SUCH_TEMPLATE || value >= NUM_OF_ERRORENUMS)
        return @"ILLEGAL ERROR NUMBER!!!";
    switch (value) {
        case NO_SUCH_TEMPLATE:
            return @"NO_SUCH_TEMPLATE";
        case NO_IMPORTED_TEMPLATE:
            return @"NO_IMPORTED_TEMPLATE";
        case NO_SUCH_ATTRIBUTE:
            return @"NO_SUCH_ATTRIBUTE";
        case REF_TO_IMPLICIT_ATTRIBUTE_OUT_OF_SCOPE:
            return @"REF_TO_IMPLICIT_ATTRIBUTE_OUT_OF_SCOPE";
        case MISSING_FORMAL_ARGUMENTS:
            return @"MISSING_FORMAL_ARGUMENTS";
        case NO_SUCH_PROPERTY:
            return @"NO_SUCH_PROPERTY";
        case MAP_ARGUMENT_COUNT_MISMATCH:
            return @"MAP_ARGUMENT_COUNT_MISMATCH";
        case ARGUMENT_COUNT_MISMATCH:
            return @"ARGUMENT_COUNT_MISMATCH";
        case EXPECTING_STRING:
            return @"EXPECTING_STRING";
        case WRITER_CTOR_ISSUE:
            return @"WRITER_CTOR_ISSUE";
        case CANT_IMPORT:
            return @"CANT_IMPORT";
       case SYNTAX_ERROR:
            return @"SYNTAX_ERROR";
        case TEMPLATE_REDEFINITION:
            return @"TEMPLATE_REDEFINITION";
        case EMBEDDED_REGION_REDEFINITION:
            return @"EMBEDDED_REGION_REDEFINITION";
        case REGION_REDEFINITION:
            return @"REGION_REDEFINITION";
        case MAP_REDEFINITION:
            return @"MAP_REDEFINITION";
        case ALIAS_TARGET_UNDEFINED:
            return @"ALIAS_TARGET_UNDEFINED";
        case TEMPLATE_REDEFINITION_AS_MAP:
            return @"TEMPLATE_REDEFINITION_AS_MAP";
        case LEXER_ERROR:
            return @"LEXER_ERROR";
        case NO_DEFAULT_VALUE:
            return @"NO_DEFAULT_VALUE";
        case NO_SUCH_FUNCTION:
            return @"NO_SUCH_FUNCTION";
        case NO_SUCH_REGION:
            return @"NO_SUCH_REGION";
        case NO_SUCH_OPTION:
            return @"NO_SUCH_OPTION";
        case INVALID_TEMPLATE_NAME:
            return @"INVALID_TEMPLATE_NAME";
        case ANON_ARGUMENT_MISMATCH:
            return @"ANON_ARGUMENT_MISMATCH";
        case REQUIRED_PARAMETER_AFTER_OPTIONAL:
            return @"REQUIRED_PARAMETER_AFTER_OPTIONAL";
        case INTERNAL_ERROR:
            return @"INTERNAL_ERROR";
        case WRITE_IO_ERROR:
            return @"WRITE_IO_ERROR";
        case CANT_LOAD_GROUP_FILE:
            return @"CANT_LOAD_GROUP_FILE";
        case NUM_OF_ERRORENUMS:
            return @"INVALID_ERROR_NUMBER!!!";
    }
    return @"YOU SHOULD NEVER SEE THIS MESSAGE";
}

// getters and setters

@synthesize message;
@synthesize msgs;
@end

