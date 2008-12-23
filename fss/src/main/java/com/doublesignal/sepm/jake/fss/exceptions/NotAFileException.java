package com.doublesignal.sepm.jake.fss.exceptions;

import com.doublesignal.sepm.jake.fss.exceptions.NotAReadableFileException;

/**
 * Thrown if tried to read a file which is in fact a directory or something else
 * @author johannes
 *
 */
@SuppressWarnings("serial")
public class NotAFileException extends NotAReadableFileException {
    public NotAFileException() {
        super();
    }

    public NotAFileException(String s) {
        super(s);
    }
}
