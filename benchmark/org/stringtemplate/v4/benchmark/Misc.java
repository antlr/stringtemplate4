package org.stringtemplate.v4.benchmark;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Misc {
	public static void writeFile(String dir, String fileName, String content) {
		try {
			File f = new File(dir, fileName);
			if ( !f.getParentFile().exists() ) f.getParentFile().mkdirs();
			FileWriter w = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(w);
			bw.write(content);
			bw.close();
			w.close();
		}
		catch (IOException ioe) {
			System.err.println("can't write file");
			ioe.printStackTrace(System.err);
		}
	}
}
