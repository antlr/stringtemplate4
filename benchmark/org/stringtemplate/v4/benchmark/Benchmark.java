package org.stringtemplate.v4.benchmark;

import org.stringtemplate.v4.misc.MultiMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.*;

/** A Java benchmark tool inspired by Caliper from google.  This isn't nearly
 *  as good but probably ok for my needs.
 *
 *  Use -XX:+PrintCompilation to see if compilation happens during trials
 *  java -XX:+PrintCompilation org.stringtemplate.v4.benchmark.Benchmark MyTest
 */
public class Benchmark {
	private static final int WARMUP_REPS = 10000; // HotSpot needs this to warm up
	public static final int MIN_BENCHMARK_TIME_IN_MS = 200;
	public static final double MAX_ERROR_IN_WORK_PER_MS = 0.05;

	public static void main(String[] args) throws Exception {
		DateFormat df = DateFormat.getDateTimeInstance();
		System.err.print("# Env ");
		System.err.print("Host "+ InetAddress.getLocalHost().getHostName());
		System.err.print(", "+df.format(new GregorianCalendar().getTime()));
		System.err.print(", Java " + System.getProperty("java.runtime.version"));
		System.err.print(", "+System.getProperty("os.name")+" "+System.getProperty("os.version"));
		System.err.print(" on " + System.getProperty("os.arch"));
		System.err.println();

		for (int i = 0; i < args.length; i++) {
			String benchmarkClassName = args[i];
			run(benchmarkClassName);
		}
	}

	// keep run as one big method so it all gets compiled.

	public static void run(String benchmarkClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnknownHostException, InvocationTargetException {
		Class c = Class.forName(benchmarkClassName);
		Object suite = c.newInstance();
		Method[] methods = c.getDeclaredMethods();
		List<Method> benchmarks = new ArrayList<Method>();
		for (Method m : methods) {
			if ( m.getName().startsWith("time") ) benchmarks.add(m);
		}

		// TODO: grab interpreted time to check for compiler removing
		// dead code and giving inside speedups?

		// warm everybody up to ensure they are compiled.
		// must run them all since loading later test can force recompilation
		System.err.println("# HotSpot warmup");
		for (Method m : benchmarks) m.invoke(suite,WARMUP_REPS);

		System.err.println("# Computing number of reps per trial");
		// Compute a reps num that kicks each benchmark over MIN_BENCHMARK_TIME_IN_MS
		Map<Method, Integer> min_reps = new HashMap<Method, Integer>();
		for (Method m : benchmarks) {
			int r = 10;
			while (true) {
				long start = System.nanoTime();
				m.invoke(suite,r);
				long stop = System.nanoTime();
				long duration_in_ns = (stop - start);
//				System.out.println(r+" r cost "+duration_in_ns+"ns");
				if ( duration_in_ns > MIN_BENCHMARK_TIME_IN_MS*1000000 ) break;
				double reps_per_ns = (double)r / duration_in_ns;
//				System.out.println("r/ns="+reps_per_ns);
				r = (int)(reps_per_ns * MIN_BENCHMARK_TIME_IN_MS*1000000 * 1.10); // 10% fudgefactor
//				System.out.println("trying "+r+" reps");
				min_reps.put(m, r);
			}
		}

//		System.err.println("min_reps="+min_reps);

		MultiMap<Method, Long> duration = new MultiMap<Method, Long>();
		// run all benchmarks in same order, recording duration
		for (Method m : benchmarks) {
			System.err.println("# "+m.getName()+" benchmarking"); // show progress
			List<Integer> reps = getReps(min_reps, m);
			for (int r : reps) {
				System.gc();
				long start = System.nanoTime();
				m.invoke(suite,r);
				long stop = System.nanoTime();
				duration.map(m, stop - start);
			}
		}

//		System.err.printf("%-30s: ", "repetitions");
//		for (int r : min_reps.get(benchmarks.get(0)) ) System.err.printf("%8d", r);
//		System.err.println();

		MultiMap<Method, Double> avgs = new MultiMap<Method, Double>();
		for (Method m : duration.keySet()) {
			System.err.printf("%-30s: ", m.getName());
			List<Long> d = duration.get(m);
			List<Integer> reps = getReps(min_reps, m);
			double avg_across = 0.0;
			for (int i=0; i<d.size(); i++) {
				//System.out.println(reps.get(i)+" reps = "+d.get(i));
				double avg = ((double)reps.get(i)) / d.get(i);
				avgs.map(m, avg*1000000);
				avg_across += avg;
				System.err.printf("%8.2f",avg * 1000000);
			}
			avg_across /= d.size();
			System.err.printf(" = %8.2f units of work / ms", avg_across * 1000000);
			double ratio;
			List<Double> a = avgs.get(m);
			if ( a.get(0) < a.get(a.size() - 1) ) ratio = a.get(0) / a.get(a.size() - 1);
			else ratio = a.get(a.size() - 1) / a.get(0);
			if ( (1-ratio) > MAX_ERROR_IN_WORK_PER_MS ) {
				System.err.print(" warning: variable average work");
			}
			System.err.println();
		}
	}

	protected static List<Integer> getReps(Map<Method, Integer> min_reps, Method m) {
		List<Integer> reps = new ArrayList<Integer>();
		reps.add(min_reps.get(m));
		reps.add(min_reps.get(m)*2);
		reps.add(min_reps.get(m)*4);
		reps.add(min_reps.get(m)*8);
		reps.add(min_reps.get(m)*16);
		return reps;
	}
}
