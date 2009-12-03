package org.stringtemplate.misc;

/** An inclusive interval a..b */
public class Interval {
    public int a;
    public int b;
    public Interval(int a, int b) { this.a=a; this.b=b; }
    public String toString() { return a+".."+b; }    
}
