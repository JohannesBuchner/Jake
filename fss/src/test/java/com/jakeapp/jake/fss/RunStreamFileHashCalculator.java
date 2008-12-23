package com.jakeapp.jake.fss;


import java.io.File;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.junit.Ignore;

@Ignore
public class RunStreamFileHashCalculator {

	private static final Logger log = Logger
			.getLogger(RunStreamFileHashCalculator.class);

	public static void main(String[] args) {
		StreamFileHashCalculator fhc;
		try {
			fhc = new StreamFileHashCalculator();
		} catch (NoSuchAlgorithmException e) {
			return;
		}
		File folder = new File(".");
		for (File f : folder.listFiles()) {
			if (f.isFile()) {
				try {
					log
							.info(f.getAbsolutePath() + " - "
									+ fhc.calculateHash(f));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
