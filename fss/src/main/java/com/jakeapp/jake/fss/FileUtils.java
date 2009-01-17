package com.jakeapp.jake.fss;

import java.io.File;
import java.util.LinkedList;
import java.util.List;


public class FileUtils {

	static Iterable<String> listMinusA(File f) {
		List<String> files = new LinkedList<String>();
		String[] fl = f.list();
		if (fl != null)
			for (String s : f.list()) {
				if (!s.equals("..") && !s.equals(".")) {
					files.add(s);
				}
			}
		return files;
	}

	static Iterable<File> listFilesMinusA(File f) {
		List<File> files = new LinkedList<File>();
		File[] fl = f.listFiles();
		if (fl != null)
			for (File s : fl) {
				if (!s.getName().equals("..") && !s.getName().equals(".")) {
					files.add(s);
				}
			}
		return files;
	}
}
