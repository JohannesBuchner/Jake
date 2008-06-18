package com.doublesignal.sepm.jake.fss.exceptions;

/**
 * Trying to read a file, a reading error occured.
 * @author johannes
 *
 */
@SuppressWarnings("serial")
public class NotAReadableFileException extends Exception {
    public NotAReadableFileException() {
        super();
    }

    public NotAReadableFileException(String s) {
        super(s);
    }
}
