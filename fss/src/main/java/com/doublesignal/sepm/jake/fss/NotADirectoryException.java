package com.doublesignal.sepm.jake.fss;

/**
 * Thrown if tried to reach a directory which is in fact a file or something else
 * @author johannes
 *
 */
@SuppressWarnings("serial")
public class NotADirectoryException extends Exception {
    public NotADirectoryException(String s) {
        super(s);
    }

    public NotADirectoryException() {
        super();
    }
}
