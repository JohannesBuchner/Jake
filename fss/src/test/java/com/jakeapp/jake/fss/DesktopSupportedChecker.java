/**
 * 
 */
package com.jakeapp.jake.fss;

import java.awt.Desktop;

import com.googlecode.junit.ext.Checker;

public class DesktopSupportedChecker implements Checker {

	public boolean satisfy() {
		
		if (Desktop.isDesktopSupported()) {
			return true;
		} else {
			FSServiceTestCase.log.warn("Desktop not supported, skipping tests!");
			return false;
		}
	}
}