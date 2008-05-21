package com.doublesignal.sepm.jake.core.domain.exceptions;

import java.io.IOException;

/**
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: domdorn
 * Date: May 9, 2008
 * Time: 12:42:06 AM
 */
public class NoSuchFileException extends IOException {
	public NoSuchFileException(String message) {
		super(message);
	}
}
