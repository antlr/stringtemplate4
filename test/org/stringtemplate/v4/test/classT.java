package org.stringtemplate.v4.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class classT extends BaseTest {

    public static class T extends BaseTest {
        String template;
        Object x;
        String expecting;
    
        String result;
    
        public T(String template, Object x, String expecting) {
            this.template = template;
            this.x = x;
            this.expecting = expecting;
        }
    
        public T(T t) {
            this.template = t.template;
            this.x = t.x;
            this.expecting = t.expecting;
        }
    
        @Override
        public String toString() {
            String s = x.toString();
            if ( x.getClass().isArray() ) {
                s = Arrays.toString((Object[])x);
            }
            return "('"+template+"', "+s+", '"+expecting+"', '"+result+"')";
        }
    }

    final static Object UNDEF = "<undefined>";
    final static List<?> LIST0 = new ArrayList<Object>();

    final static classT.T[] singleValuedTests = new classT.T[] {
        new classT.T("<x>", UNDEF, ""),
        new classT.T("<x>", null, ""),
        new classT.T("<x>", "", ""),
        new classT.T("<x>", LIST0, ""),

        new classT.T("<x:t()>", UNDEF, ""),
        new classT.T("<x:t()>", null, ""),
        new classT.T("<x:t()>", "", ""),
        new classT.T("<x:t()>", LIST0, ""),

        new classT.T("<x; null={y}>", UNDEF, "y"),
        new classT.T("<x; null={y}>", null, "y"),
        new classT.T("<x; null={y}>", "", ""),
        new classT.T("<x; null={y}>", LIST0, ""),

        new classT.T("<x:t(); null={y}>", UNDEF, "y"),
        new classT.T("<x:t(); null={y}>", null, "y"),
        new classT.T("<x:t(); null={y}>", "", ""),
        new classT.T("<x:t(); null={y}>", LIST0, ""),

        new classT.T("<if(x)>y<endif>", UNDEF, ""),
        new classT.T("<if(x)>y<endif>", null, ""),
        new classT.T("<if(x)>y<endif>", "", "y"),
        new classT.T("<if(x)>y<endif>", LIST0, ""),

        new classT.T("<if(x)>y<else>z<endif>", UNDEF, "z"),
        new classT.T("<if(x)>y<else>z<endif>", null, "z"),
        new classT.T("<if(x)>y<else>z<endif>", "", "y"),
        new classT.T("<if(x)>y<else>z<endif>", LIST0, "z"),
    };

    final static String[] LISTa = {"a"};
    final static String[] LISTab = {"a", "b"};
    final static String[] LISTnull = {null};
    final static String[] LISTa_null = {"a",null};
    final static String[] LISTnull_b = {null,"b"};
    final static String[] LISTa_null_b = {"a",null,"b"};

    final static classT.T[] multiValuedTests = new classT.T[] {
        new classT.T("<x>", LIST0,        ""),
        new classT.T("<x>", LISTa,        "a"),
        new classT.T("<x>", LISTab,       "ab"),
        new classT.T("<x>", LISTnull,     ""),
        new classT.T("<x>", LISTnull_b,   "b"),
        new classT.T("<x>", LISTa_null,   "a"),
        new classT.T("<x>", LISTa_null_b, "ab"),

        new classT.T("<x; null={y}>", LIST0,        ""),
        new classT.T("<x; null={y}>", LISTa,        "a"),
        new classT.T("<x; null={y}>", LISTab,       "ab"),
        new classT.T("<x; null={y}>", LISTnull,     "y"),
        new classT.T("<x; null={y}>", LISTnull_b,   "yb"),
        new classT.T("<x; null={y}>", LISTa_null,   "ay"),
        new classT.T("<x; null={y}>", LISTa_null_b, "ayb"),

        new classT.T("<x; separator={,}>", LIST0,        ""),
        new classT.T("<x; separator={,}>", LISTa,        "a"),
        new classT.T("<x; separator={,}>", LISTab,       "a,b"),
        new classT.T("<x; separator={,}>", LISTnull,     ""),
        new classT.T("<x; separator={,}>", LISTnull_b,   "b"),
        new classT.T("<x; separator={,}>", LISTa_null,   "a"),
        new classT.T("<x; separator={,}>", LISTa_null_b, "a,b"),

        new classT.T("<x; null={y}, separator={,}>", LIST0,        ""),
        new classT.T("<x; null={y}, separator={,}>", LISTa,        "a"),
        new classT.T("<x; null={y}, separator={,}>", LISTab,       "a,b"),
        new classT.T("<x; null={y}, separator={,}>", LISTnull,     "y"),
        new classT.T("<x; null={y}, separator={,}>", LISTnull_b,   "y,b"),
        new classT.T("<x; null={y}, separator={,}>", LISTa_null,   "a,y"),
        new classT.T("<x; null={y}, separator={,}>", LISTa_null_b, "a,y,b"),

        new classT.T("<if(x)>y<endif>", LIST0,        ""),
        new classT.T("<if(x)>y<endif>", LISTa,        "y"),
        new classT.T("<if(x)>y<endif>", LISTab,       "y"),
        new classT.T("<if(x)>y<endif>", LISTnull,     "y"),
        new classT.T("<if(x)>y<endif>", LISTnull_b,   "y"),
        new classT.T("<if(x)>y<endif>", LISTa_null,   "y"),
        new classT.T("<if(x)>y<endif>", LISTa_null_b, "y"),

        new classT.T("<x:{it | <it>}>", LIST0,        ""),
        new classT.T("<x:{it | <it>}>", LISTa,        "a"),
        new classT.T("<x:{it | <it>}>", LISTab,       "ab"),
        new classT.T("<x:{it | <it>}>", LISTnull,     ""),
        new classT.T("<x:{it | <it>}>", LISTnull_b,   "b"),
        new classT.T("<x:{it | <it>}>", LISTa_null,   "a"),
        new classT.T("<x:{it | <it>}>", LISTa_null_b, "ab"),

        new classT.T("<x:{it | <it>}; null={y}>", LIST0,        ""),
        new classT.T("<x:{it | <it>}; null={y}>", LISTa,        "a"),
        new classT.T("<x:{it | <it>}; null={y}>", LISTab,       "ab"),
        new classT.T("<x:{it | <it>}; null={y}>", LISTnull,     "y"),
        new classT.T("<x:{it | <it>}; null={y}>", LISTnull_b,   "yb"),
        new classT.T("<x:{it | <it>}; null={y}>", LISTa_null,   "ay"),
        new classT.T("<x:{it | <it>}; null={y}>", LISTa_null_b, "ayb"),

        new classT.T("<x:{it | <i>.<it>}>", LIST0,        ""),
        new classT.T("<x:{it | <i>.<it>}>", LISTa,        "1.a"),
        new classT.T("<x:{it | <i>.<it>}>", LISTab,       "1.a2.b"),
        new classT.T("<x:{it | <i>.<it>}>", LISTnull,     ""),
        new classT.T("<x:{it | <i>.<it>}>", LISTnull_b,   "1.b"),
        new classT.T("<x:{it | <i>.<it>}>", LISTa_null,   "1.a"),
        new classT.T("<x:{it | <i>.<it>}>", LISTa_null_b, "1.a2.b"),

        new classT.T("<x:{it | <i>.<it>}; null={y}>", LIST0,        ""),
        new classT.T("<x:{it | <i>.<it>}; null={y}>", LISTa,        "1.a"),
        new classT.T("<x:{it | <i>.<it>}; null={y}>", LISTab,       "1.a2.b"),
        new classT.T("<x:{it | <i>.<it>}; null={y}>", LISTnull,     "y"),
        new classT.T("<x:{it | <i>.<it>}; null={y}>", LISTnull_b,   "y1.b"),
        new classT.T("<x:{it | <i>.<it>}; null={y}>", LISTa_null,   "1.ay"),
        new classT.T("<x:{it | <i>.<it>}; null={y}>", LISTa_null_b, "1.ay2.b"),

        new classT.T("<x:{it | x<if(!it)>y<endif>}; null={z}>", LIST0,        ""),
        new classT.T("<x:{it | x<if(!it)>y<endif>}; null={z}>", LISTa,        "x"),
        new classT.T("<x:{it | x<if(!it)>y<endif>}; null={z}>", LISTab,       "xx"),
        new classT.T("<x:{it | x<if(!it)>y<endif>}; null={z}>", LISTnull,     "z"),
        new classT.T("<x:{it | x<if(!it)>y<endif>}; null={z}>", LISTnull_b,   "zx"),
        new classT.T("<x:{it | x<if(!it)>y<endif>}; null={z}>", LISTa_null,   "xz"),
        new classT.T("<x:{it | x<if(!it)>y<endif>}; null={z}>", LISTa_null_b, "xzx"),

        new classT.T("<x:t():u(); null={y}>", LIST0,        ""),
        new classT.T("<x:t():u(); null={y}>", LISTa,        "a"),
        new classT.T("<x:t():u(); null={y}>", LISTab,       "ab"),
        new classT.T("<x:t():u(); null={y}>", LISTnull,     "y"),
        new classT.T("<x:t():u(); null={y}>", LISTnull_b,   "yb"),
        new classT.T("<x:t():u(); null={y}>", LISTa_null,   "ay"),
        new classT.T("<x:t():u(); null={y}>", LISTa_null_b, "ayb")
    };

    final static classT.T[] listTests = new classT.T[] {
        new classT.T("<[]>", UNDEF, ""),
        new classT.T("<[]; null={x}>", UNDEF, ""),
        new classT.T("<[]:{it | x}>", UNDEF, ""),
        new classT.T("<[[],[]]:{it| x}>", UNDEF, ""),
        new classT.T("<[]:t()>", UNDEF, ""),
    };
    public static T T;
    
}
