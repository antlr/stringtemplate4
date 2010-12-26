package org.stringtemplate.v4.benchmark;

import org.stringtemplate.v4.ST;

public class WriteFixedTemplates {
	String bigTemplate;

	public WriteFixedTemplates() {
		StringBuilder buf = new StringBuilder();
		for (int i=1; i<=1000; i++) buf.append("some text");
		bigTemplate = buf.toString();
	}

	public void timeSingle(int reps) {
		String template =
			"A smallish string to write out";
		ST st = new ST(template);
		for (int i = 0; i < reps; i++) {
			st.render();
		}
	}

	public void timeSingleBigger(int reps) {
		ST st = new ST(bigTemplate);
		for (int i = 0; i < reps; i++) {
			st.render();
		}
	}
}
